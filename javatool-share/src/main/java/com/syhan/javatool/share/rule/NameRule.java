package com.syhan.javatool.share.rule;

import java.util.ArrayList;
import java.util.List;

// change name rule
// amis3.vo.sp.ss.ge.SsgedwkmoVO -> kr.amc.amis.sp.speciments.ge.entity.SsgedwkmoDTO
//   - postfix:VO   -> postfix:DTO
public class NameRule {
    //
    private List<PostfixStatement> postfixStatements;
    private List<String> exceptionPatterns;

    public static NameRule newInstance() {
        //
        return new NameRule();
    }

    public static NameRule copyOf(NameRule other) {
        //
        return new NameRule(other);
    }

    private NameRule() {
        //
        this.postfixStatements = new ArrayList<>();
        this.exceptionPatterns = new ArrayList<>();
    }

    private NameRule(NameRule other) {
        //
        this();
        for (PostfixStatement statement : other.postfixStatements) {
            this.postfixStatements.add(new PostfixStatement(statement));
        }
        for (String exceptionPattern : other.exceptionPatterns) {
            this.exceptionPatterns.add(exceptionPattern);
        }
    }

    public NameRule add(String fromPostfix, String toPostfix) {
        //
        if (exist(fromPostfix))
            throw new RuntimeException(String.format("rule already exists. -> %s", fromPostfix));
        this.postfixStatements.add(new PostfixStatement(fromPostfix, toPostfix));
        return this;
    }

    public NameRule addExceptionPattern(String exceptionPattern) {
        //
        this.exceptionPatterns.add(exceptionPattern);
        return this;
    }

    public String changeName(String name) {
        //
        if (isExceptionPattern(name)) {
            return name;
        }
        for (PostfixStatement statement : postfixStatements) {
            if (name.endsWith(statement.fromPostfix)) {
                int nameLength = name.length();
                int postfixLength = statement.fromPostfix.length();
                return name.substring(0, nameLength - postfixLength) + statement.toPostfix;
            }
        }
        return name;
    }

    private boolean isExceptionPattern(String name) {
        //
        for (String exceptionPattern : exceptionPatterns) {
            if (name.contains(exceptionPattern)) {
                return true;
            }
        }
        return false;
    }

    private boolean exist(String fromPostfix) {
        //
        for (PostfixStatement statement : postfixStatements) {
            if (statement.fromPostfix.equals(fromPostfix)) {
                return true;
            }
        }
        return false;
    }

    private static class PostfixStatement {
        //
        String fromPostfix;
        String toPostfix;

        public PostfixStatement(String fromPostfix, String toPostfix) {
            //
            this.fromPostfix = fromPostfix;
            this.toPostfix = toPostfix;
        }

        public PostfixStatement(PostfixStatement other) {
            //
            this.fromPostfix = other.fromPostfix;
            this.toPostfix = other.toPostfix;
        }
    }
}
