package com.syhan.javatool.generator.converter;

import com.syhan.javatool.share.config.ProjectSources;
import com.syhan.javatool.share.config.SourceFolders;
import com.syhan.javatool.share.util.file.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class PackageConverter {
    //
    private static final Logger logger = LoggerFactory.getLogger(PackageConverter.class);

    private ProjectItemConverter converter;

    public PackageConverter(ProjectItemConverter converter) {
        this.converter = converter;
    }

    public void convert(String packageName) throws IOException {
        // packageName : com.foo.bar -> path : com/foo/bar
        String packagePath = PathUtil.toPath(packageName);
        String physicalSourcePath = getPhysicalSourcePath(packagePath);

        try (Stream<Path> paths = Files.walk(Paths.get(physicalSourcePath))) {
            paths
                    .filter(p -> p.toString().endsWith("." + converter.getItemExtension()))
                    .forEach(this::process);
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

    private void process(Path path) {
        //
        String sourceFile = null;
        try {
            SourceFolders sourceFolders = converter.sourceConfiguration.getSourceFolders();
            String physicalPathName = path.toString();
            sourceFile = ProjectSources.extractSourceFilePath(physicalPathName, sourceFolders);

            logger.info("source file : {}", sourceFile);
            converter.convert(sourceFile);

        } catch (Throwable e) {
            logger.error("Couldn't convert --> {}, {}", sourceFile, e.getMessage());
            logger.error("Throwable", e);
        }
    }

}
