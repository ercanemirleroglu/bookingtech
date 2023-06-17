package com.shameless.bookingtech.integration.automation.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeviceType {
    DESKTOP(DeviceTypeSpecs.DESKTOP_SPEC),
    MOBILE(DeviceTypeSpecs.MOBILE_SPEC);

    private DeviceTypeSpec deviceTypeSpec;

    public DeviceTypeSpec getDeviceTypeSpec() {
        return deviceTypeSpec;
    }

    DeviceType(DeviceTypeSpec deviceTypeSpec) {
        this.deviceTypeSpec = deviceTypeSpec;
    }


    public static interface DeviceTypeSpec {
        boolean widthCompability(int width);
    }

    static class DeviceTypeSpecs {
        static final DeviceTypeSpec DESKTOP_SPEC = new DeviceTypeSpec() {
            @Override
            public boolean widthCompability(int width) {
                return width > 1023;
            }

        };

        static final DeviceTypeSpec MOBILE_SPEC = new DeviceTypeSpec() {
            @Override
            public boolean widthCompability(int width) {
                return width <= 1023;
            }
        };
    }
}
