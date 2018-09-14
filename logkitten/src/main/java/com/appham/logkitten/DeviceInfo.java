package com.appham.logkitten;

import android.os.Build;
import android.support.annotation.NonNull;

public class DeviceInfo {

    private String model, brand, product;

    public DeviceInfo() {
        model = Build.MODEL;
        brand = Build.BRAND;
        product = Build.PRODUCT;
    }

    @NonNull
    public String getModel() {
        return model;
    }

    @NonNull
    public String getBrand() {
        return brand;
    }

    @NonNull
    public String getProduct() {
        return product;
    }

    @Override
    public String toString() {
        return "Device info: " + model + ", " + brand + ", " + product;
    }
    
}
