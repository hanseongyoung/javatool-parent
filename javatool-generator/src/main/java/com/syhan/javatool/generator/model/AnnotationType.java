package com.syhan.javatool.generator.model;

public class AnnotationType extends ClassType {
    //
    public static AnnotationType copyOf(AnnotationType other) {
        //
        if (other == null) {
            return null;
        }
        return new AnnotationType(other);
    }

    public AnnotationType(String className) {
        super(className);
    }

    private AnnotationType(AnnotationType other) {
        super(other);
    }
}
