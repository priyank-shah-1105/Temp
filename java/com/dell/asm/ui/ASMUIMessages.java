package com.dell.asm.ui;

import com.dell.asm.i18n2.EEMILocalizableMessage;
import com.dell.asm.i18n2.EEMILocalizableMessage.EEMICategory;
import com.dell.asm.i18n2.EEMILocalizableMessage.EEMISeverity;
import com.dell.asm.i18n2.ResourceBundleLocalizableMessage;
import com.dell.asm.ui.model.chassis.UISetupChassisConfig;

public final class ASMUIMessages {

    private static final String MESSAGE_BUNDLE_NAME = "AsmUI";
    private static final String AGENT_ID = "ASMUI";

    private ASMUIMessages() {
        // do not instantiate
    }

    private static EEMILocalizableMessage buildDetailedMessage(MsgCodes msgCode,
                                                               EEMISeverity severity,
                                                               EEMICategory category,
                                                               Object... params) {
        return new EEMILocalizableMessage(
                new ResourceBundleLocalizableMessage(MESSAGE_BUNDLE_NAME, msgCode.name(),
                        params), severity, category, AGENT_ID);
    }

    public static EEMILocalizableMessage invalidChassisSlot(final UISetupChassisConfig config) {
        return buildDetailedMessage(
                MsgCodes.VXFMUI0001,
                EEMISeverity.ERROR,
                EEMICategory.USER_FACING,
                config.slot);
    }

    public static EEMILocalizableMessage invalidPowerCapRange(final UISetupChassisConfig config,
                                                              final Number lowerBound,
                                                              final Number upperBound,
                                                              final String rangeUnit) {
        return buildDetailedMessage(
                MsgCodes.VXFMUI0002,
                EEMISeverity.ERROR,
                EEMICategory.USER_FACING,
                config.powercap,
                lowerBound.toString(),
                upperBound.toString(),
                rangeUnit);
    }

    public static EEMILocalizableMessage networkAlreadyInUseByTemplates(final String templates) {
        return buildDetailedMessage(
                MsgCodes.VXFMUI0003,
                EEMISeverity.ERROR,
                EEMICategory.USER_FACING,
                templates);
    }

    public static EEMILocalizableMessage networkAlreadyInUseByDeployments(
            final String deployments) {
        return buildDetailedMessage(
                MsgCodes.VXFMUI0004,
                EEMISeverity.ERROR,
                EEMICategory.USER_FACING,
                deployments);
    }

    public static EEMILocalizableMessage addOnModuleErrorUploading(final String addOnFileName) {
        return buildDetailedMessage(
                MsgCodes.VXFMUI0005,
                EEMISeverity.ERROR,
                EEMICategory.USER_FACING,
                addOnFileName);
    }

    public static EEMILocalizableMessage addOnModuleErrorUploadingEmptyFile(
            final String addOnFileName) {
        return buildDetailedMessage(
                MsgCodes.VXFMUI0005,
                EEMISeverity.ERROR,
                EEMICategory.USER_FACING,
                addOnFileName);
    }

    public static EEMILocalizableMessage addOnModuleCannotFindUpload() {
        return buildDetailedMessage(
                MsgCodes.VXFMUI0006,
                EEMISeverity.ERROR,
                EEMICategory.USER_FACING);
    }

    public static EEMILocalizableMessage usersImported(int successes, int conflicts, int failures) {
        return buildDetailedMessage(
                MsgCodes.VXFMUI0007,
                EEMISeverity.INFO,
                EEMICategory.USER_FACING,
                successes,
                conflicts,
                failures);
    }

    public static EEMILocalizableMessage userFoundInTemplates(final String user,
                                                              final String eachTemplate) {
        return buildDetailedMessage(
                MsgCodes.VXFMUI0008,
                EEMISeverity.ERROR,
                EEMICategory.USER_FACING,
                user,
                eachTemplate);
    }

    public static EEMILocalizableMessage networkUsedInTemplates(final String name,
                                                                final String templates) {
        return buildDetailedMessage(
                MsgCodes.VXFMUI0009,
                EEMISeverity.ERROR,
                EEMICategory.USER_FACING,
                name,
                templates);
    }

    public static EEMILocalizableMessage networkUsedInDeployments(final String name,
                                                                  final String deployments) {
        return buildDetailedMessage(
                MsgCodes.VXFMUI0010,
                EEMISeverity.ERROR,
                EEMICategory.USER_FACING,
                name,
                deployments);
    }

    public enum MsgCodes {
        VXFMUI0001, VXFMUI0002, VXFMUI0003, VXFMUI0004, VXFMUI0005, VXFMUI0006, VXFMUI0007, VXFMUI0008, VXFMUI0009,
        VXFMUI0010
    }
}
