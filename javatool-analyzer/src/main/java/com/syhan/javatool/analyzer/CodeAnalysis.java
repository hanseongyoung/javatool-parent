package com.syhan.javatool.analyzer;

import com.syhan.javatool.analyzer.analyzer.Analyzer;
import com.syhan.javatool.analyzer.analyzer.PackageAnalyzer;
import com.syhan.javatool.analyzer.store.JavaDependencyStore;
import com.syhan.javatool.analyzer.store.StoreConfig;
import com.syhan.javatool.share.config.ToolConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CodeAnalysis {
    //private static final String SOURCE_PATH = "/Users/daniel/Documents/work/source_gen/source-gen-work/source-project";
    private static final String SOURCE_PATH = "/Users/daniel/Documents/work/namooio/git_temp/nara-platform/pavilion/pavilion-service";
// /Users/daniel/Documents/work/namooio/git_temp/nara-platform/pavilion/pavilion-service/src/main/java/nara
    public static void main(String[] args) throws Exception {
        //
        ApplicationContext ctx = new AnnotationConfigApplicationContext(StoreConfig.class);
        JavaDependencyStore store = ctx.getBean(JavaDependencyStore.class);

        ToolConfiguration configuration = new ToolConfiguration(SOURCE_PATH);

        // 1. java file analyze
        //Analyzer analyzer = new JavaAnalyzer(configuration, store);
        //analyzer.analyze("com/foo/bar/service/SampleService.java");

        // 2. package analyze
        Analyzer analyzer = new PackageAnalyzer(configuration, store);
        analyzer.analyze("nara.pavilion");
        System.out.println("### Complete analyze ###");

        //
        //List<JavaDependency> list = store.findByFromModule("com.foo.bar.service.SampleService");
        //List<JavaDependency> list = store.findAll();
        //list.forEach(System.out::println);
    }
}
