package com.dell.asm.ui.model.device;

import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;

public class UIUpdateDeviceFirmwareRequest extends UIBaseObject {

    public List<String> idList;

    //updatenow, nextreboot, forcereboot, schedule
    public String scheduleType;

    public boolean exitMaintenanceMode;

    public String scheduleDate;
}