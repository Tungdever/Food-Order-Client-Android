package com.uteating.foodapp.Interface;

import com.uteating.foodapp.model.CartInfo;

import java.util.ArrayList;

public interface IAdapterItemListener {
    void onCheckedItemCountChanged(int count, long price, ArrayList<CartInfo> selectedItems);
    void onAddClicked();
    void onSubtractClicked();
    void onDeleteProduct();
}
