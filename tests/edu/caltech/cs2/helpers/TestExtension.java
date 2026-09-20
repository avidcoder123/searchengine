package edu.caltech.cs2.helpers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class TestExtension implements TestExecutionExceptionHandler {
    public static final String TEXT_RED = "\u001B[31m";
    public static final String TEXT_BLACK = "\u001B[30m";
    public static final String TEXT_GREEN = "\u001B[32m";
    public static final String TEXT_BLUE = "\u001B[34m";
    public static final String TEXT_RESET = "\u001B[0m";
    public static final String TEXT_PURPLE = "\u001B[35m";
    public static final String TEXT_CYAN = "\u001B[36m";
    public static final String TEXT_YELLOW = "\u001B[33m";
    public static final String TEXT_WHITE = "\u001B[37m";

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
            throws Throwable {
        DependsOn d = context.getTestMethod().get().getAnnotation(DependsOn.class);
        System.out.print(TEXT_YELLOW);
        System.out.println(context.getDisplayName());
        System.out.println("=".repeat(context.getDisplayName().length()));
        TestDescription t2 = context.getTestMethod().get().getAnnotation(TestDescription.class);
        if (t2 != null) {
            System.out.println(t2.value());
        }
        System.out.print(TEXT_PURPLE);
        if (d != null) {
            System.out.println("Hint: This test depends on the following being implemented correctly:\n    - " + String.join("\n    - ", d.value()));
        }
        TestHint t = context.getTestMethod().get().getAnnotation(TestHint.class);
        if (t != null) {
            System.out.println("Hint: " + t.value());
        }
        System.out.print(TEXT_RESET);
        throw throwable;
    }
}