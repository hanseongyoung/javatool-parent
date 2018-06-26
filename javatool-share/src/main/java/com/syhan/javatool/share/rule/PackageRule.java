package com.syhan.javatool.share.rule;

import com.syhan.javatool.share.data.Pair;

import java.util.ArrayList;
import java.util.List;

// change package rule
// amis3.mc.oo.od.controller -> kr.amc.amis.mc.order.od.rest
// amis3.vo.mc.oo.od         -> [reorder]amis3.mc.oo.od.vo -> kr.amc.amis.mc.order.od.entity
//  - 1, vo         -> [order]4
//  - 0, amis3      -> kr.amc.amis
//  - 2, oo         -> order
//  - 4, controller -> rest
//  - 4, vo         -> entity

// amis3.vo.mc.oo.od         -> [reorder]amis3.mc.oo.od.vo -> kr.amc.amis.mc.order.ext.spec.sdo
//  - 1, vo         -> [order]4
//  - 4, vo         -> skip 1, ext.spec.sdo
public class PackageRule {
    //
    private String prefix;
    private String postfix;
    private List<Statement> statements;
    private List<ClassNameStatement> classNameStatements;
    private List<String> removeImports;
    private List<ChangeImport> changeImports;

    public static PackageRule newInstance() {
        //
        return new PackageRule();
    }

    public static PackageRule copyOf(PackageRule other) {
        //
        return new PackageRule(other);
    }

    private PackageRule() {
        //
        this.statements = new ArrayList<>();
        this.classNameStatements = new ArrayList<>();
        this.removeImports = new ArrayList<>();
        this.changeImports = new ArrayList<>();
    }

    private PackageRule(PackageRule other) {
        //
        this();
        this.prefix = other.prefix;
        this.postfix = other.postfix;
        for (Statement statement : other.statements) {
            this.statements.add(new Statement(statement));
        }
        for (ClassNameStatement statement : classNameStatements) {
            this.classNameStatements.add(new ClassNameStatement(statement));
        }
        for (String removeImport : removeImports) {
            this.removeImports.add(removeImport);
        }
        for (ChangeImport changeImport : changeImports) {
            this.changeImports.add(new ChangeImport(changeImport));
        }
    }

    public PackageRule add(String fromClassNamePostFix, String additionalPackage) {
        //
        if (exist(fromClassNamePostFix))
            throw new RuntimeException(String.format("rule already exists. -> %s", fromClassNamePostFix));

        this.classNameStatements.add(new ClassNameStatement(fromClassNamePostFix, additionalPackage));
        return this;
    }

    public PackageRule add(int fromIndex, String fromPackage, int toIndex) {
        //
        if (fromIndex >= toIndex) {
            throw new RuntimeException("toIndex must bigger than fromIndex");
        }

        if (exist(fromIndex, fromPackage)) {
            throw new RuntimeException(String.format("rule already exists. -> %d, %s", fromIndex, fromPackage));
        }

        this.statements.add(new Statement(fromIndex, fromPackage, toIndex));
        return this;
    }

    public PackageRule set(int fromIndex, String fromPackage, int toIndex) {
        //
        Statement exists = find(fromIndex, fromPackage);
        if (exists != null) {
            this.statements.remove(exists);
        }
        this.statements.add(new Statement(fromIndex, fromPackage, toIndex));
        return this;
    }

    public PackageRule add(int fromIndex, String fromPackage, String toPackage) {
        //
        return add(fromIndex, fromPackage, toPackage, 0);
    }

    public PackageRule add(int fromIndex, String fromPackage, String toPackage, int skipSize) {
        //
        if (exist(fromIndex, fromPackage))
            throw new RuntimeException(String.format("rule already exists. -> %d, %s", fromIndex, fromPackage));

        this.statements.add(new Statement(fromIndex, fromPackage, toPackage, skipSize));
        return this;
    }

    public PackageRule set(int fromIndex, String fromPackage, String toPackage, int skipSize) {
        //
        Statement exists = find(fromIndex, fromPackage);
        if (exists != null) {
            this.statements.remove(exists);
        }
        this.statements.add(new Statement(fromIndex, fromPackage, toPackage, skipSize));
        return this;
    }

    public PackageRule set(int fromIndex, String fromPackage, String toPackage) {
        //
        return set(fromIndex, fromPackage, toPackage, 0);
    }

    public PackageRule setPrefix(String prefix) {
        //
        this.prefix = prefix;
        return this;
    }

    public PackageRule setPostfix(String postfix) {
        //
        this.postfix = postfix;
        return this;
    }

    public PackageRule addRemoveImport(String importName) {
        //
        this.removeImports.add(importName);
        return this;
    }

    public PackageRule addChangeImport(String beforeName, String afterName) {
        //
        this.changeImports.add(new ChangeImport(beforeName, afterName));
        return this;
    }

    public PackageRule putChangeImport(ChangeImport changeImport) {
        //
        ChangeImport exist = findChangeImport(changeImport.before);
        if (exist != null) {
            this.changeImports.remove(exist);
        }
        this.changeImports.add(changeImport);
        return this;
    }

    public boolean hasRemoveImports() {
        //
        return this.removeImports != null && !this.removeImports.isEmpty();
    }

    public boolean hasChangeImports() {
        //
        return this.changeImports != null && !this.changeImports.isEmpty();
    }

    public boolean containsRemoveImport(String importName) {
        //
        return this.removeImports.contains(importName);
    }

    private ChangeImport findChangeImport(String beforeName) {
        //
        for (ChangeImport changeImport : changeImports) {
            if (changeImport.before.equals(beforeName)) {
                return changeImport;
            }
        }
        return null;
    }

    public String findWholeChangeImportName(String beforeImportName) {
        //
        for (ChangeImport changeImport : changeImports) {
            if (changeImport.before.equals(beforeImportName)) {
                return changeImport.after;
            }
        }
        return null;
    }

    public String changePackage(String packageName, String classShortName) {
        //
        String changed = changePackageWithRule(packageName);

        for (ClassNameStatement statement : classNameStatements) {
            if (classShortName.endsWith(statement.fromClassNamePostFix)) {
                return changed + "." + statement.additionalPackage;
            }
        }

        return processPrefixPostfix(changed);
    }

    public String changePackage(String packageName) {
        //
        String changed = changePackageWithRule(packageName);
        return processPrefixPostfix(changed);
    }

    private String processPrefixPostfix(String packageName) {
        //
        return (prefix != null ? prefix + "." : "")
                + packageName
                + (postfix != null ? "." + postfix : "");
    }

    private String changePackageWithRule(String packageName) {
        //
        String[] packageFrags = packageName.split("\\.");
        int length = packageFrags.length;

        // reordering package frags
        packageFrags = reorder(packageFrags);

        // change package frag
        String[] changedPackageFrag = new String[length];
        for (int i = 0; i < length; i++) {
            Pair<String, Integer> changeData = changePackageFrag(i, packageFrags[i]);
            changedPackageFrag[i] = changeData.x;

            // skip data
            int skipSize = changeData.y;
            if (skipSize > 0) {
                for (int y = 0; y < skipSize; y++) {
                    changedPackageFrag[i - (y + 1)] = null;
                }
            }
            //
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            String data = changedPackageFrag[i];
            if (data != null) {
                sb.append(data);
                sb.append(".");
            }
        }

        // remove last '.'
        String result = sb.toString();
        if (result.endsWith(".")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    // only 1 orderStatement is permitted.
    private String[] reorder(String[] packageFrags) {
        //
        Statement orderStatement = findOrderStatement(packageFrags);
        if (orderStatement == null) {
            return packageFrags;
        }

        int fromIndex = orderStatement.fromIndex;
        int toIndex = orderStatement.toIndex;

        String[] orderedFrags = new String[packageFrags.length];
        String tmp = packageFrags[fromIndex];

        for (int i = 0; i < orderedFrags.length; i++) {
            if (i >= fromIndex && i < toIndex && i + 1 < packageFrags.length) {
                orderedFrags[i] = packageFrags[i + 1];
            } else if (i == toIndex) {
                orderedFrags[i] = tmp;
            } else {
                orderedFrags[i] = packageFrags[i];
            }
        }
        return orderedFrags;
    }

    private Pair<String, Integer> changePackageFrag(int fragIndex, String packageFrag) {
        //
        Statement statement = find(fragIndex, packageFrag);
        if (statement == null) {
            return new Pair<>(packageFrag, 0);
        }
        return new Pair<>(statement.toPackage, statement.skipSize);
    }

    public boolean exist(int fromIndex, String fromPackage) {
        //
        for (Statement statement : statements) {
            if (statement.fromIndex == fromIndex && statement.fromPackage.equals(fromPackage)) {
                return true;
            }
        }
        return false;
    }

    private boolean exist(String fromClassNamePostFix) {
        //
        for (ClassNameStatement statement : classNameStatements) {
            if (statement.fromClassNamePostFix.equals(fromClassNamePostFix)) {
                return true;
            }
        }
        return false;
    }

    private Statement find(int fromIndex, String fromPackage) {
        //
        for (Statement statement : statements) {
            if (statement.fromIndex == fromIndex && statement.fromPackage.equals(fromPackage)) {
                return statement;
            }
        }
        return null;
    }

    private Statement findOrderStatement(String[] packageFrags) {
        //
        for (int i = 0; i < packageFrags.length; i++) {
            Statement statement = find(i, packageFrags[i]);
            if (statement != null && statement.isOrderStatement()) {
                return statement;
            }
        }
        return null;
    }

    public boolean containsChangeImport(String beforeName) {
        //
        for (ChangeImport changeImport : changeImports) {
            if (changeImport.before.equals(beforeName)) {
                return true;
            }
        }
        return false;
    }

    private class ClassNameStatement {
        //
        String fromClassNamePostFix;
        String additionalPackage;

        public ClassNameStatement(String fromClassNamePostFix, String additionalPackage) {
            this.fromClassNamePostFix = fromClassNamePostFix;
            this.additionalPackage = additionalPackage;
        }

        public ClassNameStatement(ClassNameStatement other) {
            this.fromClassNamePostFix = other.fromClassNamePostFix;
            this.additionalPackage = other.additionalPackage;
        }
    }

    @SuppressWarnings("unused")
    private class Statement {
        //
        int fromIndex;
        String fromPackage;
        String toPackage;
        int toIndex = -1;
        int skipSize;

        public boolean isOrderStatement() {
            //
            return toIndex >= 0;
        }

        public boolean isSkipable() {
            //
            return skipSize > 0;
        }

        public Statement(int fromIndex, String fromPackage, String toPackage) {
            //
            this(fromIndex, fromPackage, toPackage, 0);
        }

        public Statement(int fromIndex, String fromPackage, String toPackage, int skipSize) {
            //
            this.fromIndex = fromIndex;
            this.fromPackage = fromPackage;
            this.toPackage = toPackage;
            this.skipSize = skipSize;
        }

        public Statement(int fromIndex, String fromPackage, int toIndex) {
            //
            this.fromIndex = fromIndex;
            this.fromPackage = fromPackage;
            this.toIndex = toIndex;
        }

        public Statement(Statement other) {
            //
            this.fromIndex = other.fromIndex;
            this.fromPackage = other.fromPackage;
            this.toPackage = other.toPackage;
            this.toIndex = other.toIndex;
            this.skipSize = other.skipSize;
        }
    }

    public static class ChangeImport {
        //
        String before;
        String after;

        public ChangeImport(String before, String after) {
            this.before = before;
            this.after = after;
        }

        public ChangeImport(ChangeImport other) {
            this.before = other.before;
            this.after = other.after;
        }
    }
}
