package com.optimumtransfer.tests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public final class TestSupport {
    private TestSupport() {
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " Expected: " + expected + ", Actual: " + actual);
        }
    }

    public static void assertArrayEquals(int[] expected, int[] actual, String message) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(message + " Expected: " + Arrays.toString(expected) + ", Actual: " + Arrays.toString(actual));
        }
    }

    public static void assertContains(String expectedFragment, String actual, String message) {
        if (!actual.contains(expectedFragment)) {
            throw new AssertionError(message + " Expected fragment: " + expectedFragment + ", Actual: " + actual);
        }
    }

    public static void assertThrows(Class<? extends Throwable> expectedType, ThrowingRunnable runnable, String message) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            if (expectedType.isInstance(throwable)) {
                return;
            }
            throw new AssertionError(message + " Expected exception: " + expectedType.getSimpleName() + ", Actual: " + throwable.getClass().getSimpleName(), throwable);
        }

        throw new AssertionError(message + " Expected exception: " + expectedType.getSimpleName());
    }

    public static String captureStdOut(ThrowingRunnable runnable) throws Exception {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream capture = new PrintStream(buffer);
        try {
            System.setOut(capture);
            runnable.run();
        } finally {
            System.setOut(originalOut);
            capture.close();
        }
        return buffer.toString();
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }
}
