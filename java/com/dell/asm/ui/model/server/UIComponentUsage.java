package com.dell.asm.ui.model.server;


public enum UIComponentUsage {


    System_Board_CPU_Usage("CPU Usage"),
    System_Board_MEM_Usage("Memory Usage"),
    System_Board_SYS_Usage("System Usage"),
    System_Board_IO_Usage("I/O Usage");


    private String _label;

    private UIComponentUsage(String label) {
        _label = label;
    }

    public String getLabel() {
        return _label;
    }

    public String getValue() {
        return name();
    }

    @Override
    public String toString() {
        return _label;
    }
}


    