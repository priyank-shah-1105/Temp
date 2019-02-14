package com.dell.asm.ui.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class ConversionUtils.
 */
public final class ConversionUtility {
    /** The Constant logger. */
    private static final Logger logger = Logger.getLogger(ConversionUtility.class);

    private ConversionUtility() {
    }

    /**
     * Convert ip string to long.
     *
     * @param address the address
     * @return the long
     */
    public static long convertIpStringToLong(String address) {
        if (null == address || StringUtils.isEmpty(address)) {
            logger.debug("IP conversion failed: Address string was null or empty.");
            throw new IllegalArgumentException("IP Address string cannot be null or empty.");
        }

        long result = 0;
        String[] parts = address.split("\\.");

        if (parts.length != 4) {
            logger.debug(
                    "IP conversion failed: Address string did not have 4 dot-separated fields.");
            throw new IllegalArgumentException(
                    "IP Address string must have 4 dot-separated fields.");
        }

        for (String part : parts) {
            int partValue = -1;
            try {
                partValue = Integer.parseInt(part);
            } catch (NumberFormatException e) {
                logger.debug(
                        "IP conversion failed: integer parse of part threw NumberFormatException (IP part not numeric)");
                throw new IllegalArgumentException("Parts of IP address must be numeric",
                                                   e);
            }

            if (0 > partValue || 255 < partValue) {
                logger.debug("IP conversion failed: octed out of range (not between 0 and 255)");
                throw new IllegalArgumentException("Parts of IP address must be in range 0-255");
            }

            result = result * 0x100 + partValue;
        }
        return result;
    }

    /**
     * Convert ip value to string.
     *
     * @param address the address
     * @return the string
     */
    public static String convertIpValueToString(long address) {
        long aField = ((address & 0x00000000FF000000l) >>> 24);
        long bField = ((address & 0x0000000000FF0000l) >>> 16);
        long cField = ((address & 0x000000000000FF00l) >>> 8);
        long dField = (address & 0x00000000000000FFl);
        return String.format("%1$1d.%2$1d.%3$1d.%4$1d",
                             aField,
                             bField,
                             cField,
                             dField);
    }

    /**
     * Helper for storage values.
     * @param unit TB/MB/GB
     * @return
     */
    public static double getMultiplierForGBConversion(String unit) {
        if (unit != null) {
            unit = unit.trim().toUpperCase();
            if ("TB".equals(unit)) {
                return 1024;
            } else if ("MB".equals(unit)) {
                DecimalFormat df2 = new DecimalFormat("#.#####");
                return Double.valueOf(df2.format(1.0 / 1024));
            } else if ("GB".equals(unit)) {
                return 1;
            }
        }
        return 1;
    }

    /**
     * Helper for UI date strings. These do not contain the timezone and will be assumed to
     * be in the appliance timezone.
     *
     * WARNING: This method will probably go away. Only the firmware update scheduling is
     * using this format, the service deployment schedule is using the standard xml
     * dateTime format.
     *
     * @param dateTime The date time string, e.g. "September 29 2014 1:00 AM"
     * @return the corresponding Date object
     */
    public static Date getDateFromGuiString(String dateTime) throws ParseException {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
        // NOTE: always using US locale because the date string is built by the javascript GUI
        // and presumably that will always use English month names.
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(dateTime);
    }
}
