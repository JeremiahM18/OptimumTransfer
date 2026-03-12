package tests;

public interface TestCase {
    String name();
    void run() throws Exception;
}
