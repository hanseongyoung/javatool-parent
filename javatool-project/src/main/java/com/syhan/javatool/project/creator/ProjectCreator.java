package com.syhan.javatool.project.creator;

import com.syhan.javatool.project.model.ProjectModel;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.util.FolderUtil;

public class ProjectCreator {
    //
    private String targetHomePath;

    public ProjectCreator(String targetHomePath) {
        this.targetHomePath = targetHomePath;
    }

    public void create(ProjectModel model) {
        //
        ProjectConfiguration configuration = new ProjectConfiguration(ConfigurationType.Target, targetHomePath, model.getName());
        // make project home
        makeProjectHome(configuration);
        // make source folder
        makeSourceFolder(configuration);
        // make pom
        makePom(model);
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

    private void makePom(ProjectModel model) {

    }
}
