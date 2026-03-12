package com.optimumtransfer.application;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class RuntimeDefaults {
    private static final String DISPLAY_LIMIT_PROPERTY = "optimumtransfer.displayLimit";
    private static final String EXPORT_PATH_PROPERTY = "optimumtransfer.exportPath";
    private static final String BATCH_PREVIEW_LIMIT_PROPERTY = "optimumtransfer.batchPreviewLimit";

    private RuntimeDefaults() {
    }

    public static int displayLimit() {
        return positiveIntProperty(DISPLAY_LIMIT_PROPERTY, 50);
    }

    public static int batchPreviewLimit() {
        return positiveIntProperty(BATCH_PREVIEW_LIMIT_PROPERTY, 10);
    }

    public static Path exportPath() {
        return Paths.get(System.getProperty(EXPORT_PATH_PROPERTY, "transfer_log.txt"));
    }

    private static int positiveIntProperty(String propertyName, int defaultValue) {
        String rawValue = System.getProperty(propertyName);
        if (rawValue == null || rawValue.isBlank()) {
            return defaultValue;
        }

        int parsedValue = Integer.parseInt(rawValue.trim());
        if (parsedValue <= 0) {
            throw new IllegalArgumentException(propertyName + " must be positive.");
        }
        return parsedValue;
    }
}
