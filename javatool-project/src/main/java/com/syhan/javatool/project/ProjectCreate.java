package com.syhan.javatool.project;

import com.syhan.javatool.project.creator.ProjectCreator;
import com.syhan.javatool.project.model.ProjectModel;

public class ProjectCreate {
    //
    private static final String TARGET_PATH = "/Users/daniel/Documents/work/source_gen/javatool-parent";

    public static void main(String[] args) throws Exception {
        //
        ProjectModel model = new ProjectModel("sample-project", "com.sample", "1.0-SNAPSHOT", "pom");
        ProjectCreator creator = new ProjectCreator(TARGET_PATH);
        creator.create(model);
    }

}
