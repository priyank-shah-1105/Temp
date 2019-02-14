package com.dell.asm.ui.converter;

import java.beans.PropertyEditorSupport;
import java.util.UUID;


public class StringToUUIDConverter extends PropertyEditorSupport {

    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        setValue(UUID.fromString(text));
    }
}
