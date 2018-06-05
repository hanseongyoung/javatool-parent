package com.syhan.javatool.analyzer.viewer;

import com.syhan.javatool.analyzer.entity.JavaDependency;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DependencyViewerTest {
    //
    @Test
    public void test() {
        //
        List<JavaDependency> list = new ArrayList<>();
        list.add(new JavaDependency("com", 1, "net", 1));
        list.add(new JavaDependency("com", 1, "net", 1));
        DependencyTreeViewer dependencyViewer = new DependencyTreeViewer(list);
        System.out.println(dependencyViewer.show());
    }
}
