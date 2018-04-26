package com.syhan.javatool.analyzer.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(indexes = @Index(name = "IDX_FROM_TO_JAVA_DEP", unique = true, columnList = "fromModule,toModule"))
public class JavaDependency {
    //
    @Id
    private String id;
    private String fromModule;
    private int fromLevel;
    private String toModule;
    private int toLevel;

    public JavaDependency() {
    }

    public JavaDependency(String fromModule, int fromLevel, String toModule, int toLevel) {
        //
        this.id = UUID.randomUUID().toString();
        this.fromModule = fromModule;
        this.fromLevel = fromLevel;
        this.toModule = toModule;
        this.toLevel = toLevel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromModule() {
        return fromModule;
    }

    public void setFromModule(String fromModule) {
        this.fromModule = fromModule;
    }

    public String getToModule() {
        return toModule;
    }

    public void setToModule(String toModule) {
        this.toModule = toModule;
    }

    @Override
    public String toString() {
        return "JavaDependency{" +
                "id='" + id + '\'' +
                ", fromModule='" + fromModule + '\'' +
                ", toModule='" + toModule + '\'' +
                '}';
    }
}
