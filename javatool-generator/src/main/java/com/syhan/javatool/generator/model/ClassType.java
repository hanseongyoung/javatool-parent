package com.syhan.javatool.generator.model;

import com.syhan.javatool.share.data.Pair;
import com.syhan.javatool.share.rule.NameRule;
import com.syhan.javatool.share.rule.PackageRule;
import com.syhan.javatool.share.util.file.PathUtil;

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
    private ClassType typeArgument;

    public static ClassType newClassType(String className) {
        //
        return new ClassType(className);
    }

    public static ClassType newClassType(String name, String packageName) {
        //
        return new ClassType(name, packageName);
    }

    public static ClassType newPrimitiveType(String primitiveName) {
        //
        return new ClassType(primitiveName, true);
    }

    protected ClassType(String className) {
        //
        int lastDotIndex = className.lastIndexOf(".");
        if (lastDotIndex > 0) {
            this.packageName = className.substring(0, lastDotIndex);
            this.name = className.substring(lastDotIndex + 1, className.length());
        } else {
            this.name = className;
            this.packageName = null;
        }
        this.primitive = false;
    }

    private ClassType(String name, boolean primitive) {
        //
        this.name = name;
        this.primitive = primitive;
        this.packageName = null;
        this.typeArgument = null;
    }

    public ClassType(ClassType other) {
        //
        this.name = other.getName();
        this.packageName = other.getPackageName();
        this.primitive = other.isPrimitive();
        if (other.hasTypeArgument()) {
            this.typeArgument = new ClassType(other.getTypeArgument());
        }
    }

    private ClassType(String name, String packageName) {
        //
        this.name = name;
        this.packageName = packageName;
    }

    public boolean changeWholePackageAndName(PackageRule packageRule) {
        //
        String changedClassName = packageRule.findWholeChangeImportName(getClassName());
        if (changedClassName == null) {
            return false;
        }

        Pair<String, String> packageNameAndName = PathUtil.devideClassName(changedClassName);
        setPackageName(packageNameAndName.x);
        setName(packageNameAndName.y);
        return true;
    }

    public void changePackage(PackageRule packageRule) {
        //
        if (packageRule == null) {
            return;
        }

        if (!primitive && packageName != null) {
            this.packageName = packageRule.changePackage(packageName, name);
        }
    }

    public void changeName(NameRule nameRule) {
        //
        if (nameRule == null) {
            return;
        }

        this.name = nameRule.changeName(name);
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

        if (Character.isLowerCase(str.charAt(0))) {
            return str;
        }

        char c[] = str.toCharArray();
        c[0] += 32;
        return new String(c);
    }

    public boolean hasTypeArgument() {
        //
        return typeArgument != null;
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

    public ClassType getTypeArgument() {
        return typeArgument;
    }

    public void setTypeArgument(ClassType typeArgument) {
        this.typeArgument = typeArgument;
    }

    public static void main(String[] args) {
        //
        ClassType classType = new ClassType("com.foo.bar.SomeClass");
        System.out.println(classType.getName());
        System.out.println(classType.getPackageName());
    }


}
