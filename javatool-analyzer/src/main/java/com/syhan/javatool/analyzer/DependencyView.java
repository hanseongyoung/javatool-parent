package com.syhan.javatool.analyzer;

import com.syhan.javatool.analyzer.entity.JavaDependency;
import com.syhan.javatool.analyzer.store.JavaDependencyStore;
import com.syhan.javatool.analyzer.store.StoreConfig;
import com.syhan.javatool.analyzer.viewer.DependencyViewer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class DependencyView {
    //
    public static void main(String[] args) {
        //
        ApplicationContext ctx = new AnnotationConfigApplicationContext(StoreConfig.class);
        JavaDependencyStore store = ctx.getBean(JavaDependencyStore.class);

        // 특정 모듈이 참조하는 전체 모듈 조회
        //List<JavaDependency> list = store.findByFromModule("nara.pavilion");

        // 특정 모듈이 참조하는 1단계 모듈 조회
        //List<JavaDependency> list = store.findByFromModule("nara.pavilion", 2);

        // 특정 모듈이 참조하는 일부 모듈 조회
        List<JavaDependency> list = store.findByFromModuleAndStartWithToModule("nara.pavilion", "nara", 2);

        System.out.println(new DependencyViewer(list).show());
        System.out.println("total : " + list.size());
    }
}
