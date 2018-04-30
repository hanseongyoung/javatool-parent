package com.syhan.javatool.generator.model;

public class ClassType {
    //
    public static final String PRIMITIVE_INT = "INT";
    public static final String PRIMITIVE_BOOLEAN = "BOOLEAN";
    public static final String PRIMITIVE_CHAR = "CHAR";
    public static final String PRIMITIVE_BYTE = "BYTE";
    public static final String PRIMITIVE_SHORT = "SHORT";
    public static final String PRIMITIVE_LONG = "LONG";
    public static final String PRIMITIVE_FLOAT = "FLOAT";
    public static final String PRIMITIVE_DOUBLE = "DOUBLE";

    private String name;
    private String packageName;
    private boolean primitive;

    public ClassType(String className) {
        //
        int lastDotIndex = className.lastIndexOf(".");
        if (lastDotIndex > 0) {
            this.packageName = className.substring(0, lastDotIndex);
            this.name = className.substring(lastDotIndex + 1, className.length());
            this.primitive = false;
        } else {
            this.name = className;
            this.packageName = null;
            this.primitive = true;
        }
    }

    public ClassType(String name, String packageName) {
        //
        this.name = name;
        this.packageName = packageName;
    }

    public String getClassName() {
        //
        if (packageName == null) {
            return name;
        }
        return packageName + "." + name;
    }

    public String getRecommendedVariableName() {
        //
        return toFirstLowerCase(name);
    }

    private String toFirstLowerCase(String str) {
        //
        if (str == null) return null;

        char c[] = str.toCharArray();
        c[0] += 32;
        return new String(c);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public static void main(String[] args) {
        //
        ClassType classType = new ClassType("com.foo.bar.SomeClass");
        System.out.println(classType.getName());
        System.out.println(classType.getPackageName());
    }

}
