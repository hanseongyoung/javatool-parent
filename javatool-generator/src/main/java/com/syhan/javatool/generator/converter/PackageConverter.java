package com.syhan.javatool.generator.converter;

import com.syhan.javatool.share.config.ProjectSources;
import com.syhan.javatool.share.config.SourceFolders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PackageConverter {
    //
    private ProjectItemConverter converter;

    public PackageConverter(ProjectItemConverter converter) {
        this.converter = converter;
    }

    public void convert(String packageName) throws IOException {
        // packageName : com.foo.bar -> path : com/foo/bar
        String packagePath = convertPackageNameToPath(packageName);
        String physicalSourcePath = getPhysicalSourcePath(packagePath);

        try (Stream<Path> paths = Files.walk(Paths.get(physicalSourcePath))) {
            paths
                    .filter(p -> p.toString().endsWith("." + converter.getItemExtension()))
                    .forEach(pathConsumer());
        }
    }

    private String getPhysicalSourcePath(String sourcePath) {
        //
        if (converter.projectItemType == ProjectItemType.Java) {
            return converter.sourceConfiguration.makePhysicalJavaSourceFilePath(sourcePath);
        } else {
            return converter.sourceConfiguration.makePhysicalResourceFilePath(sourcePath);
        }
    }

    private Consumer<Path> pathConsumer() {
        return path -> {
            String sourceFile = null;
            try {
                SourceFolders sourceFolders = converter.sourceConfiguration.getSourceFolders();
                String physicalPathName = path.toString();
                sourceFile = ProjectSources.extractSourceFilePath(physicalPathName, sourceFolders);

                System.out.println("sourcFile:" + sourceFile);
                converter.convert(sourceFile);

            } catch (Exception e) {
                // TODO : 파일 로깅 처리하고 계속 진행함.
                System.err.println("Couldn't convert --> " + sourceFile + ", " + e.getMessage());
            }
        };
    }

    private String convertPackageNameToPath(String packageName) {
        //
        return packageName.replace(".", File.separator);
    }
}
