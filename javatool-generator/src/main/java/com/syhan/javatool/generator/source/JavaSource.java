package com.syhan.javatool.generator.source;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.syhan.javatool.generator.ast.AstMapper;
import com.syhan.javatool.generator.model.JavaModel;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;

public class JavaSource {
    //
    private CompilationUnit compilationUnit;
    private String physicalSourceFile;

    public JavaSource(String physicalSourceFile) throws FileNotFoundException {
        //
        this.compilationUnit = JavaParser.parse(new FileInputStream(physicalSourceFile));
        this.physicalSourceFile = physicalSourceFile;
    }

    public JavaSource(JavaModel model) {
        //
        this.compilationUnit = AstMapper.createCompilationUnit(model);
    }

    public JavaModel toModel() {
        //
        return AstMapper.toJavaModel(compilationUnit, this::findFullName);
    }

    private String findFullName(String returnTypeName) {
        //
        for (Object obj : compilationUnit.getImports()) {
            ImportDeclaration importDeclaration = (ImportDeclaration) obj;
            String packageName = importDeclaration.getName().asString();
            if (packageName.endsWith(returnTypeName)) {
                return packageName;
            }
        }
        return returnTypeName;
    }

    public boolean isFromFile() {
        //
        return physicalSourceFile != null;
    }

    public static boolean exists(String physicalSourceFile) {
        //
        File file = new File(physicalSourceFile);
        return file.exists() && !file.isDirectory();
    }

    // com/foo/bar/SampleService.java
    public String getSourceFilePath() {
        //
        String packageName = compilationUnit.getPackageDeclaration().get().getNameAsString();
        String typeName = compilationUnit.getType(0).getNameAsString();
        return packageName.replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + File.separator + typeName + ".java";
    }

    public void write(String physicalTargetFilePath) throws IOException {
        //
        File file = new File(physicalTargetFilePath);
        System.out.println(compilationUnit.toString());
        FileUtils.writeStringToFile(file, compilationUnit.toString(), "UTF-8");
    }

    public static void main(String[] args) {
        // In the case of Windows replaceAll with File.separator causes 'character to be escaped is missing' error.
        String packageName = "com.foo.bar";
        //System.out.println(packageName.replaceAll("\\.", File.separator));
        System.out.println(packageName.replaceAll("\\.", Matcher.quoteReplacement(File.separator)));
    }
}
