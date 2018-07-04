package com.example.root.securityalert;

import java.util.Objects;

public class ViewHolder {
    String deviceName;
    String deviceAddress;

    public ViewHolder(String device, String __address) {
        this.deviceName =device;
        this.deviceAddress= __address;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewHolder a = (ViewHolder) o;
        return Objects.equals(deviceAddress, a.deviceAddress);
    }
}
