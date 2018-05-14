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
    private List<Statement> statements;
    private List<ClassNameStatement> classNameStatements;

    public static PackageRule newInstance() {
        //
        return new PackageRule();
    }

    private PackageRule() {
        //
        this.statements = new ArrayList<>();
        this.classNameStatements = new ArrayList<>();
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

    public String changePackage(String packageName, String className) {
        //
        String changed = changePackage(packageName);

        for (ClassNameStatement statement : classNameStatements) {
            if (className.endsWith(statement.fromClassNamePostFix)) {
                return changed + "." + statement.additionalPackage;
            }
        }

        return changed;
    }

    public String changePackage(String packageName) {
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
            if (i >= fromIndex && i < toIndex) {
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

    private boolean exist(int fromIndex, String fromPackage) {
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

    private static class ClassNameStatement {
        //
        String fromClassNamePostFix;
        String additionalPackage;

        public ClassNameStatement(String fromClassNamePostFix, String additionalPackage) {
            this.fromClassNamePostFix = fromClassNamePostFix;
            this.additionalPackage = additionalPackage;
        }
    }

    private static class Statement {
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
    }
}
