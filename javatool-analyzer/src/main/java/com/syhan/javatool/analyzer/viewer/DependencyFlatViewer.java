package com.syhan.javatool.analyzer.viewer;

import com.syhan.javatool.analyzer.entity.JavaDependency;

import java.util.List;

public class DependencyFlatViewer {
    //
    private List<JavaDependency> dependencies;

    public DependencyFlatViewer(List<JavaDependency> dependencies) {
        //
        this.dependencies = dependencies;
    }

    public String show() {
        //
        StringBuffer sb = new StringBuffer();
        for (JavaDependency dependency : dependencies) {
            sb.append(dependency.getToModule()).append("\n");
        }
        return sb.toString();
    }

}
