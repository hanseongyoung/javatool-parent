package com.syhan.javatool.generator.converter;

import com.syhan.javatool.share.config.ToolConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PackageConverter implements Converter {
    //
    private ToolConfiguration configuration;

    public PackageConverter(ToolConfiguration configuration) {
        //
        this.configuration = configuration;
    }

    @Override
    public void convert(String packageName) throws IOException {
        // packageName : com.foo.bar
        String packagePath = convertPath(packageName);
        String physicalSourcePath = configuration.getPhysicalSourceFilePath(packagePath);

        try (Stream<Path> paths = Files.walk(Paths.get(physicalSourcePath))) {
            paths
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(javaConvert());
        }
    }

    private Consumer<Path> javaConvert() {
        return path -> {
            try {
                String physicalPathName = path.toString();
                String sourceFile = configuration.getSourceFilePath(physicalPathName);

                new JavaConverter(configuration).convert(sourceFile);
            } catch (IOException e) {
                // TODO : 파일 로깅 처리하고 계속 진행함.
                e.printStackTrace();
            }
        };
    }

    private String convertPath(String packageName) {
        //
        return packageName.replace(".", File.separator);
    }
}
