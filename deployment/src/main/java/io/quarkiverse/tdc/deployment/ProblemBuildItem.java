package io.quarkiverse.tdc.deployment;

import io.quarkus.builder.item.MultiBuildItem;

public final class ProblemBuildItem extends MultiBuildItem {

    private final String message;
    private final String methodName;
    private final String className;

    public ProblemBuildItem(String message, String className, String methodName) {
        this.message = message;
        this.className = className;
        this.methodName = methodName;
    }

    public String getMessage() {
        return message;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }
}
