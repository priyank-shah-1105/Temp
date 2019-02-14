package com.dell.asm.ui.adapter.service;

import com.dell.asm.asmcore.asmmanager.client.setting.Setting;

public interface SettingServiceAdapter {

    public Setting getSettingByName(String name);

    public Setting update(Setting setting);

    public Setting create(Setting setting);

    public void delete(final String id);
}
