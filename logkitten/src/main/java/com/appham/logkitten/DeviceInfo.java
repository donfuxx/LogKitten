package com.appham.logkitten;

import android.os.Build;
import android.support.annotation.NonNull;

public class DeviceInfo {

    private String model, brand, product, manufacturer, device, os;

    public DeviceInfo() {
        model = Build.MODEL;
        brand = Build.BRAND;
        product = Build.PRODUCT;
        manufacturer = Build.MANUFACTURER;
        device = Build.DEVICE;
        os = "Android " + Build.VERSION.RELEASE + ", API " + Build.VERSION.SDK_INT;
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

    public String getOs() {
        return os;
    }

    @Override
    public String toString() {
        return "\uD83D\uDC31 Device info: " + model + ", " + brand + ", " + product + ", " + manufacturer +
                ", " + device + ", " + os + " \uD83D\uDC31";
    }
}
