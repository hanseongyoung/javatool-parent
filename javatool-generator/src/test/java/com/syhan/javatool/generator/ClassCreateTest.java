package com.syhan.javatool.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;
import org.junit.Test;

import java.util.EnumSet;

public class ClassCreateTest {

    @Test
    public void testCreate() {
        // creates the compilation unit
        CompilationUnit cu = createCU();

        // prints the created compilation unit
        System.out.println(cu.toString());
    }

    /**
     * creates the compilation unit
     */
    private static CompilationUnit createCU() {
        CompilationUnit cu = new CompilationUnit();
        // set the package
        cu.setPackageDeclaration(new PackageDeclaration(Name.parse("java.parser.test")));

        // or a shortcut
        cu.setPackageDeclaration("java.parser.test");

        // create the type declaration
        ClassOrInterfaceDeclaration type = cu.addClass("GeneratedClass");

        // create a method
        EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PUBLIC);
        MethodDeclaration method = new MethodDeclaration(modifiers, new VoidType(), "main");
        modifiers.add(Modifier.STATIC);
        method.setModifiers(modifiers);
        type.addMember(method);

        // or a shortcut
        MethodDeclaration main2 = type.addMethod("main2", Modifier.PUBLIC, Modifier.STATIC);
        ClassOrInterfaceType returnType = JavaParser.parseClassOrInterfaceType("List");
        returnType.setTypeArguments(JavaParser.parseClassOrInterfaceType("TestDTO"));
        main2.setType(returnType);


        // add a parameter to the method
        Parameter param = new Parameter(JavaParser.parseClassOrInterfaceType("String"), "args");
        param.setVarArgs(true);
        method.addParameter(param);

        // or a shortcut
        main2.addAndGetParameter(String.class, "args").setVarArgs(true);

        // add a body to the method
        BlockStmt block = new BlockStmt();
        method.setBody(block);

        // add a statement do the method body
        NameExpr clazz = new NameExpr("System");
        FieldAccessExpr field = new FieldAccessExpr(clazz, "out");
        MethodCallExpr call = new MethodCallExpr(field, "println");
        call.addArgument(new StringLiteralExpr("Hello World!"));
        block.addStatement(call);

        return cu;
    }
}
