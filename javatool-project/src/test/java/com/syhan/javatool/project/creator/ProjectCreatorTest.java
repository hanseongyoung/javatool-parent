package com.syhan.javatool.project.creator;

import com.syhan.javatool.project.model.ProjectModel;
import com.syhan.javatool.share.test.BaseFileTest;
import org.junit.Test;

public class ProjectCreatorTest extends BaseFileTest {
    //
    @Test
    public void testCreate() {
        //
        String targetHomePath = super.testDirName;
        ProjectCreator projectCreator = new ProjectCreator(targetHomePath);

        ProjectModel model = new ProjectModel("SampleProject", "com.foo.bar");
        projectCreator.create(model);
    }
}
