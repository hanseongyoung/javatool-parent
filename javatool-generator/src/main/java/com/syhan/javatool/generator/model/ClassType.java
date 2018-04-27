package com.syhan.javatool.generator.model;

public class ClassType {
    //
    private String name;
    private String packageName;

    public ClassType(String className) {
        //
        int lastDotIndex = className.lastIndexOf(".");
        this.packageName = className.substring(0, lastDotIndex);
        this.name = className.substring(lastDotIndex + 1, className.length());
    }

    public ClassType(String name, String packageName) {
        //
        this.name = name;
        this.packageName = packageName;
    }

    public String getClassName() {
        //
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

    public static void main(String[] args) {
        //
        ClassType classType = new ClassType("com.foo.bar.SomeClass");
        System.out.println(classType.getName());
        System.out.println(classType.getPackageName());
    }

}
