package com.syhan.javatool.project.creator;

import com.syhan.javatool.project.model.ProjectModel;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.util.file.FolderUtil;

import java.io.IOException;

public class ProjectCreator {
    //
    private String targetHomePath;

    public ProjectCreator(String targetHomePath) {
        //
        this.targetHomePath = targetHomePath;
    }

    public void create(ProjectModel model) {
        //
        ProjectConfiguration configuration = new ProjectConfiguration(ConfigurationType.Target, targetHomePath, model.getName());

        // make project home
        makeProjectHome(configuration);

        // make source folder
        if (!model.isPom()) {
            makeSourceFolder(configuration);
        }

        // make pom
        makePom(model, configuration);
    }

    private void makeProjectHome(ProjectConfiguration configuration) {
        //
        FolderUtil.mkdir(configuration.getProjectHomePath());
    }

    private void makeSourceFolder(ProjectConfiguration configuration) {
        //
        FolderUtil.mkdir(configuration.getPhysicalJavaPath());
        FolderUtil.mkdir(configuration.getPhysicalResourcesPath());
        FolderUtil.mkdir(configuration.getPhysicalTestPath());
        FolderUtil.mkdir(configuration.getPhysicalTestResourcesPath());
    }

    private void makePom(ProjectModel model, ProjectConfiguration configuration) {
        //
        PomCreator pomCreator = new PomCreator(configuration);
        try {
            pomCreator.create(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
