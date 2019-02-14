package com.dell.asm.ui.adapter.service;

import com.dell.asm.alcm.client.model.AvailableTimeZones;
import com.dell.asm.alcm.client.model.TimeZoneInfo;

/**
 * The Interface ApplianceTimeZoneServiceAdapter.
 */
public interface ApplianceTimeZoneServiceAdapter {

    /**
     * Gets the available time zones.
     *
     * @return the available time zones
     */
    AvailableTimeZones getAvailableTimeZones();

    /**
     * Gets the time zone.
     *
     * @return the time zone
     */
    TimeZoneInfo getTimeZone();

    /**
     * Sets the time zone.
     *
     * @param timezone
     *            the new time zone
     */
    void setTimeZone(TimeZoneInfo timezone);

}
