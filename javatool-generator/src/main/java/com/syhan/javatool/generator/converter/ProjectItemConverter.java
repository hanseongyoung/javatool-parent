package com.syhan.javatool.generator.converter;

import com.syhan.javatool.share.config.ProjectConfiguration;

public abstract class ProjectItemConverter implements Converter {
    //
    protected ProjectConfiguration sourceConfiguration;
    protected ProjectConfiguration targetConfiguration;
    protected ProjectItemType projectItemType;

    public ProjectItemConverter(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetConfiguration, ProjectItemType projectItemType) {
        //
        this.sourceConfiguration = sourceConfiguration;
        this.targetConfiguration = targetConfiguration;
        this.projectItemType = projectItemType;
    }

    public String getItemExtension() {
        //
        return projectItemType.getExtension();
    }

    public ProjectConfiguration getSourceConfiguration() {
        return sourceConfiguration;
    }

    public ProjectConfiguration getTargetConfiguration() {
        return targetConfiguration;
    }

    public ProjectItemType getProjectItemType() {
        return projectItemType;
    }
}
