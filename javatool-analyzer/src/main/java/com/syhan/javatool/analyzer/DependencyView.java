package com.syhan.javatool.analyzer;

import com.syhan.javatool.analyzer.entity.JavaDependency;
import com.syhan.javatool.analyzer.store.JavaDependencyStore;
import com.syhan.javatool.analyzer.store.StoreConfig;
import com.syhan.javatool.analyzer.viewer.DependencyViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class DependencyView {
    //
    private static final Logger logger = LoggerFactory.getLogger(DependencyView.class);

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

        logger.info(new DependencyViewer(list).show());
        logger.info("total : {}", list.size());
    }
}
