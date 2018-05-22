package com.syhan.javatool.generator.converter;

import com.syhan.javatool.share.config.ProjectSources;
import com.syhan.javatool.share.config.SourceFolders;
import com.syhan.javatool.share.util.file.PathUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MultiItemPackageConverter {
    //
    private List<ProjectItemConverter> converters;

    public MultiItemPackageConverter() {
        //
        this.converters = new ArrayList<>();
    }

    public MultiItemPackageConverter add(ProjectItemConverter converter) {
        //
        this.converters.add(converter);
        return this;
    }

    public void convert(String packageName) throws IOException {
        // packageName : com.foo.bar -> path : com/foo/bar
        if (converters == null || converters.isEmpty()) {
            System.err.println("Please add converter.");
            return;
        }

        ProjectItemConverter firstConverter = getFirstConverter();

        String packagePath = PathUtil.toPath(packageName);
        String physicalSourcePath = getPhysicalSourcePath(firstConverter, packagePath);

        try (Stream<Path> paths = Files.walk(Paths.get(physicalSourcePath))) {
            paths
                    .filter(p -> p.toString().endsWith("." + firstConverter.getItemExtension()))
                    .forEach(this::process);
        }
    }

    private void process(Path path) {
        String sourceFileName = null;
        try {
            SourceFolders sourceFolders = getFirstConverter().sourceConfiguration.getSourceFolders();
            String physicalPathName = path.toString();
            sourceFileName = ProjectSources.extractSourceFilePath(physicalPathName, sourceFolders);

            boolean converted = false;
            for(ProjectItemConverter converter : converters) {
                //
                if (!converted) {
                    converted = convert(converter, sourceFileName);
                }
            }

            if (!converted) {
                System.err.println("Couldn't convert --> " + sourceFileName);
            }

        } catch (Exception e) {
            // TODO : 파일 로깅 처리하고 계속 진행함.
            System.err.println("Couldn't convert --> " + sourceFileName + ", " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean convert(ProjectItemConverter converter, String sourceFileName) throws IOException {
        //
        if (converter.hasItemNamePostfix()) {
            String postfix = converter.getItemNamePostfix();
            if (!sourceFileName.endsWith(postfix)) {
                return false;
            }
        }

        converter.convert(sourceFileName);
        return true;
    }

    private ProjectItemConverter getFirstConverter() {
        //
        return converters.get(0);
    }


    private String getPhysicalSourcePath(ProjectItemConverter converter, String sourcePath) {
        //
        if (converter.projectItemType == ProjectItemType.Java) {
            return converter.sourceConfiguration.makePhysicalJavaSourceFilePath(sourcePath);
        } else {
            return converter.sourceConfiguration.makePhysicalResourceFilePath(sourcePath);
        }
    }
}
