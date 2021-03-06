package com.syhan.javatool.generator.model;

import com.syhan.javatool.share.data.Pair;
import com.syhan.javatool.share.rule.NameRule;
import com.syhan.javatool.share.rule.PackageRule;
import com.syhan.javatool.share.util.file.PathUtil;
import com.syhan.javatool.share.util.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ClassType {
    //
    private static final Logger logger = LoggerFactory.getLogger(ClassType.class);

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
    private boolean array;
    private List<ClassType> typeArguments;

    public static ClassType newClassType(String className) {
        //
        if (isPrimitiveType(className)) {
            return new ClassType(className.toLowerCase(), true);
        }
        return new ClassType(className);
    }

    private static boolean isPrimitiveType(String className) {
        //
        if (PRIMITIVE_INT.toLowerCase().equals(className)) {
            return true;
        }
        if (PRIMITIVE_BOOLEAN.toLowerCase().equals(className)) {
            return true;
        }
        if (PRIMITIVE_CHAR.toLowerCase().equals(className)) {
            return true;
        }
        if (PRIMITIVE_BYTE.toLowerCase().equals(className)) {
            return true;
        }
        if (PRIMITIVE_SHORT.toLowerCase().equals(className)) {
            return true;
        }
        if (PRIMITIVE_LONG.toLowerCase().equals(className)) {
            return true;
        }
        if (PRIMITIVE_FLOAT.toLowerCase().equals(className)) {
            return true;
        }
        if (PRIMITIVE_DOUBLE.toLowerCase().equals(className)) {
            return true;
        }
        return false;
    }

    public static ClassType newClassType(String name, String packageName) {
        //
        return new ClassType(name, packageName);
    }

    public static ClassType newPrimitiveType(String primitiveName) {
        //
        return new ClassType(primitiveName, true);
    }

    public static ClassType newArrayType(ClassType componentType) {
        //
        ClassType classType = new ClassType(componentType);
        classType.array = true;
        return classType;
    }

    public static ClassType newArrayElementType(ClassType arrayType) {
        ClassType eleType = new ClassType(arrayType);
        eleType.array = false;
        return eleType;
    }

    public static ClassType copyOf(ClassType other) {
        //
        if (other == null) {
            return null;
        }
        return new ClassType(other);
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
        this.typeArguments = new ArrayList<>();
    }

    private ClassType(String name, boolean primitive) {
        //
        this.name = name;
        this.primitive = primitive;
        this.packageName = null;
        this.typeArguments = new ArrayList<>();
    }

    protected ClassType(ClassType other) {
        //
        this.name = other.getName();
        this.packageName = other.getPackageName();
        this.primitive = other.isPrimitive();
        this.array = other.isArray();
        this.typeArguments = new ArrayList<>();
        if (other.hasTypeArgument()) {
            for (ClassType typeArg : other.getTypeArguments()) {
                this.typeArguments.add(new ClassType(typeArg));
            }
        }
    }

    private ClassType(String name, String packageName) {
        //
        this.name = name;
        this.packageName = packageName;
        this.typeArguments = new ArrayList<>();
    }

    public void addTypeArgument(ClassType typeArgument) {
        //
        this.typeArguments.add(typeArgument);
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
        return StringUtil.getRecommendedVariableName(name);
    }

    public boolean hasTypeArgument() {
        //
        return typeArguments != null && typeArguments.size() > 0;
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

    public List<ClassType> getTypeArguments() {
        return typeArguments;
    }

    public boolean isArray() {
        return array;
    }

    public static void main(String[] args) {
        //
        ClassType classType = new ClassType("com.foo.bar.SomeClass");
        logger.info("classType name {}, packageName {}", classType.getName(), classType.getPackageName());
    }

}
