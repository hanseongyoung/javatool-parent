package com.syhan.javatool.analyzer.analyzer;

import com.syhan.javatool.analyzer.store.JavaDependencyStore;
import com.syhan.javatool.share.config.ProjectConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PackageAnalyzer implements Analyzer {
    //
    private ProjectConfiguration configuration;
    private JavaDependencyStore store;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public PackageAnalyzer(ProjectConfiguration configuration, JavaDependencyStore store) {
        //
        this.configuration = configuration;
        this.store = store;
    }

    @Override
    public void analyze(String packageName) throws IOException {
        // packageName : com.foo.bar
        String packagePath = convertPath(packageName);
        String physicalSourcePath = configuration.makePhysicalJavaSourceFilePath(packagePath);

        try (Stream<Path> paths = Files.walk(Paths.get(physicalSourcePath))) {
            paths
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(javaAnalyze());
        }
    }

    private Consumer<Path> javaAnalyze() {
        return path -> {
            try {
                String physicalPathName = path.toString();
                String sourceFile = configuration.extractSourceFilePath(physicalPathName);

                new JavaAnalyzer(configuration, store).analyze(sourceFile);
            } catch (IOException e) {
                // TODO : 파일 로깅 처리하고 계속 진행함.
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        };
    }

    private String convertPath(String packageName) {
        //
        return packageName.replace(".", File.separator);
    }
}
