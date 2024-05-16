package com.uteating.foodapp.Interface;


import com.uteating.foodapp.model.Address;

public interface IAddressAdapterListener {
    void onCheckedChanged(Address selectedAddress);
    void onDeleteAddress();
}
