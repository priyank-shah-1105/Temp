package com.dell.asm.ui.converter;

import java.beans.PropertyEditorSupport;

import com.dell.asm.ui.download.DownloadType;

public class DownloadTypeEnumConverter extends PropertyEditorSupport {

    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        setValue(DownloadType.findFromUrlIgnoreCase(text));
    }
}
