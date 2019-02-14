package com.dell.asm.ui.adapter.service;

import com.dell.asm.asmcore.asmmanager.client.perfmonitoring.PerformanceMetric;


public interface PerformanceMonitorServiceAdapter {

    public PerformanceMetric[] performanceMonitoring(String refId, String duration, String time);

}