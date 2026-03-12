package com.optimumtransfer.tests;

import com.optimumtransfer.application.RuntimeDefaults;

public class RuntimeDefaultsTest implements TestCase {
    @Override
    public String name() {
        return "Runtime defaults support overrides without hardcoding";
    }

    @Override
    public void run() {
        System.clearProperty("optimumtransfer.displayLimit");
        System.clearProperty("optimumtransfer.exportPath");
        System.clearProperty("optimumtransfer.batchPreviewLimit");

        TestSupport.assertEquals(50, RuntimeDefaults.displayLimit(), "Display limit should use the default value.");
        TestSupport.assertEquals(10, RuntimeDefaults.batchPreviewLimit(), "Batch preview limit should use the default value.");
        TestSupport.assertTrue(RuntimeDefaults.exportPath().toString().endsWith("transfer_log.txt"), "Export path should use the default filename.");

        System.setProperty("optimumtransfer.displayLimit", "7");
        System.setProperty("optimumtransfer.exportPath", "custom-log.txt");
        System.setProperty("optimumtransfer.batchPreviewLimit", "3");

        TestSupport.assertEquals(7, RuntimeDefaults.displayLimit(), "Display limit should allow overriding through system properties.");
        TestSupport.assertEquals(3, RuntimeDefaults.batchPreviewLimit(), "Batch preview limit should allow overriding through system properties.");
        TestSupport.assertTrue(RuntimeDefaults.exportPath().toString().endsWith("custom-log.txt"), "Export path should allow overriding through system properties.");

        System.clearProperty("optimumtransfer.displayLimit");
        System.clearProperty("optimumtransfer.exportPath");
        System.clearProperty("optimumtransfer.batchPreviewLimit");
    }
}
