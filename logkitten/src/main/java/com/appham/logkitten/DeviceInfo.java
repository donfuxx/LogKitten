package com.appham.logkitten;

import android.os.Build;
import android.support.annotation.NonNull;

public class DeviceInfo {

    private String model, brand, product, manufacturer, device;

    public DeviceInfo() {
        model = Build.MODEL;
        brand = Build.BRAND;
        product = Build.PRODUCT;
        manufacturer = Build.MANUFACTURER;
        device = Build.DEVICE;
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

    public String getManufacturer() {
        return manufacturer;
    }

    public String getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "Device info: " + model + ", " + brand + ", " + product + ", " + manufacturer + ", " + device;
    }

}
