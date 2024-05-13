package com.uteating.foodapp.adapter.orderAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uteating.foodapp.R;
import com.uteating.foodapp.activity.order.OrderActivity;
import com.uteating.foodapp.activity.order.OrderDetailActivity;
import com.uteating.foodapp.databinding.ItemOrderRowBinding;
import com.uteating.foodapp.model.Bill;
import com.uteating.foodapp.model.CurrencyFormatter;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<Bill> listOrder;
    private int type;
    private String userId;

    public OrderAdapter(Context context, ArrayList<Bill> listOrder, int type, String userId) {
        this.context = context;
        this.listOrder = listOrder;
        this.type = type;
        this.userId = userId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemOrderRowBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Bill tmp = listOrder.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        if (type == OrderActivity.CURRENT_ORDER){
            viewHolder.binding.btnSee.setText("Received");
            viewHolder.binding.btnSee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //new CustomAlertDialog(context,"Do you want to confirm this order?");
                }
            });
        }
        else {
            viewHolder.binding.txtStatus.setTextColor(Color.parseColor("#48DC7D"));
            viewHolder.binding.btnSee.setText("Feedback & Rate");
            if (tmp.isCheckAllComment()) {
                viewHolder.binding.btnSee.setEnabled(false);
                viewHolder.binding.btnSee.setBackgroundResource(R.drawable.background_feedback_disnable_button);
            }
            else {
                viewHolder.binding.btnSee.setEnabled(true);
                viewHolder.binding.btnSee.setBackgroundResource(R.drawable.background_feedback_enable_button);
            }
            viewHolder.binding.btnSee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, OrderDetailActivity.class);
                    intent.putExtra("Bill",tmp);
                    intent.putExtra("userId",userId);
                    context.startActivity(intent);
                }
            });
        }
        viewHolder.binding.txtId.setText(tmp.getBillId()+"");
        viewHolder.binding.txtDate.setText(tmp.getOrderDate()+"");
        viewHolder.binding.txtStatus.setText(tmp.getOrderStatus());
        viewHolder.binding.txtTotal.setText(CurrencyFormatter.getFormatter().format(Double.valueOf(tmp.getTotalPrice()))+"");
        viewHolder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, OrderDetailActivity.class);
                intent.putExtra("Bill",tmp);
                intent.putExtra("userId",userId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOrder == null ? 0 : listOrder.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderRowBinding binding;

        public ViewHolder(@NonNull ItemOrderRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
