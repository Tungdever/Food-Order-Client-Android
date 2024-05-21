package com.uteating.foodapp.adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uteating.foodapp.Interface.APIService;
import com.uteating.foodapp.R;
import com.uteating.foodapp.RetrofitClient;
import com.uteating.foodapp.activity.feedback.FeedBackActivity;
import com.uteating.foodapp.custom.FailToast;
import com.uteating.foodapp.custom.SuccessfulToast;
import com.uteating.foodapp.databinding.LayoutFeedbackBillifoBinding;
import com.uteating.foodapp.dialog.UploadDialog;
import com.uteating.foodapp.helper.FirebaseNotificationHelper;
import com.uteating.foodapp.model.Bill;
import com.uteating.foodapp.model.BillInfo;
import com.uteating.foodapp.model.CurrencyFormatter;
import com.uteating.foodapp.model.IntegerWrapper;
import com.uteating.foodapp.model.Notification;
import com.uteating.foodapp.model.Product;
import com.uteating.foodapp.model.Comment;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedBackAdapter extends RecyclerView.Adapter<FeedBackAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<BillInfo> ds;
    private final Bill currentBill;
    private final String userId;
    private APIService apiService;
    private Product tmp;

    //Contructor
    public FeedBackAdapter(Context mContext, ArrayList<BillInfo> ds, Bill currentBill, String id) {
        this.mContext = mContext;
        this.ds = ds;
        this.currentBill = currentBill;
        this.userId = id;
    }

    @NonNull
    @Override
    public FeedBackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FeedBackAdapter.ViewHolder(LayoutFeedbackBillifoBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FeedBackAdapter.ViewHolder holder, int position) {

        BillInfo item = ds.get(position);

        holder.binding.edtComment.setText("");
        //Biến lưu lại rate với star bao nhiêu
        IntegerWrapper starRating = new IntegerWrapper(5);

        //Set sự kiện cho rating star
        setEventForStar(holder, starRating);

        //Cho star 5 được rating khi mới khởi tạo
        holder.binding.star5.performClick();

        //Tìm thông tin products
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getProductInfor(item.getProductId()).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                tmp = response.body();
                Log.d("ProductBillInfo", tmp.getProductName());
                //set Thông tin
                holder.binding.lnBillInfo.txtPrice.setText(CurrencyFormatter.getFormatter().format(item.getAmount() * Double.valueOf(tmp.getProductPrice())) + "");
                holder.binding.lnBillInfo.txtName.setText(tmp.getProductName());
                holder.binding.lnBillInfo.txtCount.setText("Count: " + item.getAmount() + "");
                Glide.with(mContext).load(tmp.getProductImage1()).placeholder(R.drawable.default_image).into(holder.binding.lnBillInfo.imgFood);
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {

            }
        });

        //Set một listener theo dõi editText đó có vượt quá 200 kí tự không
        holder.binding.edtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Không thực hiện gì trong đây cả
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                // Kiểm tra độ dài của văn bản sau mỗi lần thay đổi
                // Văn bản đã đạt tới giới hạn 200 kí tự, không cho phép nhập thêm
                // Văn bản chưa đạt tới giới hạn 200 kí tự, cho phép nhập tiếp
                if (s.length() >= 200)
                    new FailToast(mContext, "Your comment's length must not be over 200 characters!").showToast();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Không thực hiện gì trong đây cả
            }
        });
        //Set sự kiện cho button
        holder.binding.btnSend.setOnClickListener(view -> {
            if (!holder.binding.edtComment.getText().toString().isEmpty()) {
                UploadDialog dialog = new UploadDialog(mContext);
                dialog.show();

                String commentId = FirebaseDatabase.getInstance().getReference().push().getKey();
                Comment comment = new Comment(holder.binding.edtComment.getText().toString().trim(), commentId, userId, starRating.getValue());
                FirebaseDatabase.getInstance().getReference("Comments").child(item.getProductId()).child(commentId).setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            new SuccessfulToast(mContext, "Thank you for giving feedback to my product!").showToast();
                            pushNotificationFeedBack(item);
                            dialog.dismiss();
                            updateListBillInfo(item);

                            int ratingAmount = tmp.getRatingAmount() + 1;
                            double ratingStar = (tmp.getRatingStar() * tmp.getRatingAmount() + starRating.getValue()) / ratingAmount;
                            apiService = RetrofitClient.getRetrofit().create(APIService.class);
                            apiService.addComment(ratingAmount, ratingStar, item.getProductId()).enqueue(new Callback<Product>() {
                                @Override
                                public void onResponse(Call<Product> call, Response<Product> response) {
                                    if (response.isSuccessful()) {

                                        Log.d("Comment", "Success");

                                    } else {
                                        Log.d("Comment", "Fail");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Product> call, Throwable t) {
                                    Log.d("CommentFailure", t.getMessage());
                                }
                            });

                        } else {
                            new FailToast(mContext, "Some errors occurred!").showToast();
                            dialog.dismiss();
                        }
                    }
                });
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setIcon(R.drawable.icon_alert);
                builder.setTitle("Chú ý");
                builder.setMessage("Nhớ ghi comment nha bạn ơi!");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
            }
        });
    }

    public void updateListBillInfo(BillInfo item) {
        ds.remove(item);
        notifyDataSetChanged();
        //Cập nhật lại biến check cho billInfo đó
        FirebaseDatabase.getInstance().getReference("BillInfos").child(currentBill.getBillId()).child(item.getBillInfoId()).child("check").setValue(true);
        //Cập nhật lại Bill nếu tất cả BillInfo đã feedback hết
        if (ds.isEmpty()) {
            FirebaseDatabase.getInstance().getReference("Bills").child(currentBill.getBillId()).child("checkAllComment").setValue(true);
            FeedBackActivity activity = getFeedBackActivity(mContext);
            if (activity != null) {
                Log.d("FeedBackAdapter", "Finishing activity");
                activity.finish();
            } else {
                Log.d("FeedBackAdapter", "Activity is null, cannot finish");
            }
//            if (activity != null) {
//                activity.finish();
//            }
        }
    }

    private void setEventForStar(ViewHolder viewHolder, IntegerWrapper starRating) {
        viewHolder.binding.star1.setOnClickListener(view -> onStarClicked(view, viewHolder, starRating));
        viewHolder.binding.star2.setOnClickListener(view -> onStarClicked(view, viewHolder, starRating));
        viewHolder.binding.star3.setOnClickListener(view -> onStarClicked(view, viewHolder, starRating));
        viewHolder.binding.star4.setOnClickListener(view -> onStarClicked(view, viewHolder, starRating));
        viewHolder.binding.star5.setOnClickListener(view -> onStarClicked(view, viewHolder, starRating));
    }

    public static FeedBackActivity getFeedBackActivity(Context context) {
        if (context instanceof FeedBackActivity) {
            return (FeedBackActivity) context;
        }
        return null;
    }

    public void onStarClicked(View view, ViewHolder viewHolder, IntegerWrapper starRating) {
        int clickedStarPosition = Integer.parseInt(view.getTag().toString());
        starRating.setValue(clickedStarPosition);
        viewHolder.binding.star1.setImageResource(clickedStarPosition >= 1 ? R.drawable.star_filled : R.drawable.star_none);
        viewHolder.binding.star2.setImageResource(clickedStarPosition >= 2 ? R.drawable.star_filled : R.drawable.star_none);
        viewHolder.binding.star3.setImageResource(clickedStarPosition >= 3 ? R.drawable.star_filled : R.drawable.star_none);
        viewHolder.binding.star4.setImageResource(clickedStarPosition >= 4 ? R.drawable.star_filled : R.drawable.star_none);
        viewHolder.binding.star5.setImageResource(clickedStarPosition >= 5 ? R.drawable.star_filled : R.drawable.star_none);
    }


    @Override
    public int getItemCount() {
        return ds == null ? 0 : ds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LayoutFeedbackBillifoBinding binding;

        public ViewHolder(@NonNull LayoutFeedbackBillifoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void pushNotificationFeedBack(BillInfo billInfo) {
        String title = "Product feedback";
        String content = "Your product '" + tmp.getProductName() + "' have just got a new feedback. Go to product information to check it.";
        Notification notification = FirebaseNotificationHelper.createNotification(title, content, tmp.getProductImage1(), tmp.getProductId(), "None", "None", null);
        new FirebaseNotificationHelper(mContext).addNotification(tmp.getPublisherId(), notification, new FirebaseNotificationHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Notification> notificationList, List<Notification> notificationListToNotify) {

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }
}
