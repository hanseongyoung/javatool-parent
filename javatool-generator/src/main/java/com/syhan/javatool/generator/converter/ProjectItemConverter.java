package com.syhan.javatool.generator.converter;

import com.syhan.javatool.share.config.ProjectConfiguration;

public abstract class ProjectItemConverter implements Converter {
    //
    protected ProjectConfiguration sourceConfiguration;
    protected ProjectConfiguration targetConfiguration;
    protected ProjectItemType projectItemType;
    protected String itemNamePostfix;

    public ProjectItemConverter(ProjectConfiguration sourceConfiguration, ProjectItemType projectItemType) {
        //
        this(sourceConfiguration, null, projectItemType, null);
    }

    public ProjectItemConverter(ProjectConfiguration sourceConfiguration, ProjectItemType projectItemType, String itemNamePostfix) {
        //
        this(sourceConfiguration, null, projectItemType, itemNamePostfix);
    }

    public ProjectItemConverter(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetConfiguration, ProjectItemType projectItemType) {
        //
        this(sourceConfiguration, targetConfiguration, projectItemType, null);
    }

    public ProjectItemConverter(ProjectConfiguration sourceConfiguration, ProjectConfiguration targetConfiguration, ProjectItemType projectItemType, String itemNamePostfix) {
        this.sourceConfiguration = sourceConfiguration;
        this.targetConfiguration = targetConfiguration;
        this.projectItemType = projectItemType;
        this.itemNamePostfix = itemNamePostfix;
    }

    public boolean hasItemNamePostfix() {
        //
        return itemNamePostfix != null;
    }

    public String getItemExtension() {
        //
        return projectItemType.getExtension();
    }

    public String getItemNamePostfix() {
        //
        return itemNamePostfix;
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
