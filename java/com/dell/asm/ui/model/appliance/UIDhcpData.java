package com.dell.asm.ui.model.appliance;

import java.util.HashMap;
import java.util.Map;

import com.dell.asm.alcm.client.model.DHCPSetting;
import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dhcp data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDhcpData extends UIBaseObject {

    @JsonProperty
    public boolean enableDhcpServer;
    @JsonProperty
    public String subnet;
    @JsonProperty
    public String netmask;
    @JsonProperty
    public String startingIpAddress;
    @JsonProperty
    public String endingIpAddress;
    @JsonProperty
    public String defaultLeaseTimeDays = "00";
    @JsonProperty
    public String defaultLeaseTimeHours = "01";
    @JsonProperty
    public String defaultLeaseTimeMinutes = "00";
    @JsonProperty
    public String defaultLeaseTimeSeconds = "00";
    @JsonProperty
    public String maxLeaseTimeDays = "00";
    @JsonProperty
    public String maxLeaseTimeHours = "03";
    @JsonProperty
    public String maxLeaseTimeMinutes = "00";
    @JsonProperty
    public String maxLeaseTimeSeconds = "00";
    @JsonProperty
    public String gateway;
    @JsonProperty
    public String dns;
    @JsonProperty
    public String domain;
    @JsonProperty
    public int currentWizardStep;

    public UIDhcpData() {
        super();
    }

    ;

    public UIDhcpData(DHCPSetting settings) {
        Map<String, String> defaultTimeMap = parseTime(settings.getDefaultLeaseTime());
        Map<String, String> maxTimeMap = parseTime(settings.getMaxLeaseTime());
        defaultLeaseTimeDays = defaultTimeMap.get("days");
        defaultLeaseTimeHours = defaultTimeMap.get("hours");
        defaultLeaseTimeMinutes = defaultTimeMap.get("minutes");
        defaultLeaseTimeSeconds = defaultTimeMap.get("seconds");
        maxLeaseTimeDays = maxTimeMap.get("days");
        maxLeaseTimeHours = maxTimeMap.get("hours");
        maxLeaseTimeMinutes = maxTimeMap.get("minutes");
        maxLeaseTimeSeconds = maxTimeMap.get("seconds");
        dns = settings.getDns();
        domain = settings.getDomain();
        startingIpAddress = settings.getStartingIpAddress();
        endingIpAddress = settings.getEndingIpAddress();
        gateway = settings.getGateway();
        subnet = settings.getSubnet();
        netmask = settings.getNetmask();
        enableDhcpServer = settings.isEnabled();
    }

    public DHCPSetting createDhcpSettingFromUiData() {
        DHCPSetting settings = new DHCPSetting();
        settings.setDns(dns);
        int defaultLeaseTime = (Integer.parseInt(defaultLeaseTimeDays) * 24 * 60 * 60) +
                (Integer.parseInt(defaultLeaseTimeHours) * 60 * 60) +
                (Integer.parseInt(defaultLeaseTimeMinutes) * 60) +
                Integer.parseInt(defaultLeaseTimeSeconds);
        settings.setDefaultLeaseTime(defaultLeaseTime);
        int maxLeaseTime = (Integer.parseInt(maxLeaseTimeDays) * 24 * 60 * 60) +
                (Integer.parseInt(maxLeaseTimeHours) * 60 * 60) +
                (Integer.parseInt(maxLeaseTimeMinutes) * 60) +
                Integer.parseInt(maxLeaseTimeSeconds);
        settings.setMaxLeaseTime(maxLeaseTime);
        settings.setDomain(domain);
        settings.setNetmask(netmask);
        settings.setStartingIpAddress(startingIpAddress);
        settings.setEndingIpAddress(endingIpAddress);
        settings.setGateway(gateway);
        settings.setSubnet(subnet);
        settings.setEnabled(enableDhcpServer);
        return settings;
    }

    private Map<String, String> parseTime(int time) {
        Map<String, String> result = new HashMap<>();
        int seconds_in_day = 24 * 60 * 60;
        int seconds_in_hour = 60 * 60;
        int seconds_in_minute = 60;
        int days = time / seconds_in_day;
        int hours = (time - (days * seconds_in_day)) / seconds_in_hour;
        int mins = (time - ((days * seconds_in_day) + (hours * seconds_in_hour))) / seconds_in_minute;
        int secs = time - ((days * seconds_in_day) + (hours * seconds_in_hour) + (mins * seconds_in_minute));
        result.put("days", String.format("%02d", days));
        result.put("hours", String.format("%02d", hours));
        result.put("minutes", String.format("%02d", mins));
        result.put("seconds", String.format("%02d", secs));
        return result;
    }


}
