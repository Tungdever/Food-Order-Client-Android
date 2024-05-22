package com.uteating.foodapp.adapter.orderAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uteating.foodapp.Interface.APIService;
import com.uteating.foodapp.R;
import com.uteating.foodapp.RetrofitClient;
import com.uteating.foodapp.activity.order.OrderActivity;
import com.uteating.foodapp.activity.order.OrderDetailActivity;
import com.uteating.foodapp.custom.CustomAlertDialog;
import com.uteating.foodapp.custom.SuccessfulToast;
import com.uteating.foodapp.databinding.ItemOrderLayoutBinding;
import com.uteating.foodapp.helper.FirebaseStatusOrderHelper;
import com.uteating.foodapp.model.Bill;
import com.uteating.foodapp.model.BillInfo;
import com.uteating.foodapp.model.CurrencyFormatter;
import com.uteating.foodapp.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Bill> dsOrder;
    private int type;
    private String userId;
    APIService apiService;

    public OrderAdapter(Context context, ArrayList<Bill> dsOrder, int type, String id) {
        this.context = context;
        this.dsOrder = dsOrder;
        this.type = type;
        this.userId = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemOrderLayoutBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Bill tmp = dsOrder.get(position);

        // Cập nhật thông tin hóa đơn vào giao diện
        viewHolder.binding.txtId.setText(tmp.getBillId() + "");
        viewHolder.binding.txtDate.setText(tmp.getOrderDate() + "");
        viewHolder.binding.txtStatus.setText(tmp.getOrderStatus());
        viewHolder.binding.txtTotal.setText(CurrencyFormatter.getFormatter().format(Double.valueOf(tmp.getTotalPrice())) + "");

        // Hiển thị hình ảnh sản phẩm liên quan đến hóa đơn
        FirebaseDatabase.getInstance().getReference("BillInfos").child(tmp.getBillId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BillInfo billInfo = snapshot.getChildren().iterator().next().getValue(BillInfo.class);

                if (billInfo != null) {
                    apiService = RetrofitClient.getRetrofit().create(APIService.class);
                    apiService.getProductInfor(billInfo.getProductId()).enqueue(new Callback<Product>() {
                        @Override
                        public void onResponse(Call<Product> call, Response<Product> response) {
                            if (response.body() != null) {
                                Product product = response.body();
                                Glide.with(context)
                                        .load(product.getProductImage1())
                                        .placeholder(R.drawable.default_image)
                                        .into(viewHolder.binding.imgFood);
                            }
                        }

                        @Override
                        public void onFailure(Call<Product> call, Throwable t) {
                            // Xử lý lỗi khi gọi API thất bại
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi khi không thể lấy dữ liệu từ Firebase
            }
        });

        if (type == OrderActivity.CURRENT_ORDER) {
            viewHolder.binding.btnSee.setText("Received");
            updateReceivedButtonState(viewHolder, tmp.getOrderStatus());

            viewHolder.binding.btnSee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String status = viewHolder.binding.txtStatus.getText().toString();
                    if (status.equals("Shipping")) {
                        new CustomAlertDialog(context, "Do you want to confirm this order?");
                        CustomAlertDialog.binding.btnYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new FirebaseStatusOrderHelper().setShippingToCompleted(tmp.getBillId(), new FirebaseStatusOrderHelper.DataStatus() {
                                    @Override
                                    public void DataIsLoaded(List<Bill> bills, boolean isExistingBill) {
                                        // Không cần xử lý
                                    }

                                    @Override
                                    public void DataIsInserted() {
                                        // Không cần xử lý
                                    }

                                    @Override
                                    public void DataIsUpdated() {
                                        new SuccessfulToast(context, "Your order has been changed to completed state!").showToast();
                                        viewHolder.binding.txtStatus.setText("Completed");
                                        updateReceivedButtonState(viewHolder, "Completed");
                                    }

                                    @Override
                                    public void DataIsDeleted() {
                                        // Không cần xử lý
                                    }
                                });
                                CustomAlertDialog.alertDialog.dismiss();
                            }
                        });
                        CustomAlertDialog.binding.btnNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CustomAlertDialog.alertDialog.dismiss();
                            }
                        });
                        CustomAlertDialog.showAlertDialog();
                    }
                }
            });

        } else {
            viewHolder.binding.txtStatus.setTextColor(Color.parseColor("#48DC7D"));
            viewHolder.binding.btnSee.setText("Feedback & Rate");

            if (tmp.isCheckAllComment()) {
                viewHolder.binding.btnSee.setEnabled(false);
                viewHolder.binding.btnSee.setBackgroundResource(R.drawable.background_feedback_disnable_button);
            } else {
                viewHolder.binding.btnSee.setEnabled(true);
                viewHolder.binding.btnSee.setBackgroundResource(R.drawable.background_feedback_enable_button);
            }

            viewHolder.binding.btnSee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, OrderDetailActivity.class);
                    intent.putExtra("Bill", tmp);
                    intent.putExtra("userId", userId);
                    context.startActivity(intent);
                }
            });
        }

        viewHolder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("Bill", tmp);
                intent.putExtra("userId", userId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dsOrder == null ? 0 : dsOrder.size();
    }

    private void updateReceivedButtonState(ViewHolder viewHolder, String status) {
        if (status.equals("Shipping")) {
            viewHolder.binding.btnSee.setEnabled(true);
        } else {
            viewHolder.binding.btnSee.setEnabled(false);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderLayoutBinding binding;

        public ViewHolder(@NonNull ItemOrderLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
