package com.syhan.javatool.project.complex;

import java.util.ArrayList;
import java.util.List;

public class ConvertParameter {
    //
    // 소스 프로젝트 홈, 소스 패키지, 프로젝트 명칭 1/2레벨, 대상 폴더
    private String sourceProjectHomePath;
    private String sourcePackage;       // com.foo.bar
    private String sourceDtoPackage;

    private String sourceSqlMapProjectHomePath;
    private String sourceSqlMapResourceFolder;  // sqlmap/query
    private String sourceSqlMapPackage;         // mc.oo

    private String newProjectName0;
    private String newProjectName1;
    private String newProjectName2;
    private String newBasePackage;
    private String targetWorkspace;

    private List<DependencyParameter> dependencyParameters;

    public ConvertParameter() {
        //
        this.dependencyParameters = new ArrayList<>();
    }

    public void addDependency(String projectName1, String projectName2) {
        //
        this.dependencyParameters.add(new DependencyParameter(projectName1, projectName2));
    }

    public String getSourceProjectHomePath() {
        return sourceProjectHomePath;
    }

    public void setSourceProjectHomePath(String sourceProjectHomePath) {
        this.sourceProjectHomePath = sourceProjectHomePath;
    }

    public String getSourcePackage() {
        return sourcePackage;
    }

    public void setSourcePackage(String sourcePackage) {
        this.sourcePackage = sourcePackage;
    }

    public String getSourceDtoPackage() {
        return sourceDtoPackage;
    }

    public void setSourceDtoPackage(String sourceDtoPackage) {
        this.sourceDtoPackage = sourceDtoPackage;
    }

    public String getSourceSqlMapProjectHomePath() {
        return sourceSqlMapProjectHomePath;
    }

    public void setSourceSqlMapProjectHomePath(String sourceSqlMapProjectHomePath) {
        this.sourceSqlMapProjectHomePath = sourceSqlMapProjectHomePath;
    }

    public String getSourceSqlMapResourceFolder() {
        return sourceSqlMapResourceFolder;
    }

    public void setSourceSqlMapResourceFolder(String sourceSqlMapResourceFolder) {
        this.sourceSqlMapResourceFolder = sourceSqlMapResourceFolder;
    }

    public String getSourceSqlMapPackage() {
        return sourceSqlMapPackage;
    }

    public void setSourceSqlMapPackage(String sourceSqlMapPackage) {
        this.sourceSqlMapPackage = sourceSqlMapPackage;
    }

    public String getNewProjectName0() {
        return newProjectName0;
    }

    public void setNewProjectName0(String newProjectName0) {
        this.newProjectName0 = newProjectName0;
    }

    public String getNewProjectName1() {
        return newProjectName1;
    }

    public void setNewProjectName1(String newProjectName1) {
        this.newProjectName1 = newProjectName1;
    }

    public String getNewProjectName2() {
        return newProjectName2;
    }

    public void setNewProjectName2(String newProjectName2) {
        this.newProjectName2 = newProjectName2;
    }

    public String getNewBasePackage() {
        return newBasePackage;
    }

    public void setNewBasePackage(String newBasePackage) {
        this.newBasePackage = newBasePackage;
    }

    public String getTargetWorkspace() {
        return targetWorkspace;
    }

    public void setTargetWorkspace(String targetWorkspace) {
        this.targetWorkspace = targetWorkspace;
    }

    public List<DependencyParameter> getDependencyParameters() {
        return dependencyParameters;
    }

    public void setDependencyParameters(List<DependencyParameter> dependencyParameters) {
        this.dependencyParameters = dependencyParameters;
    }

    public class DependencyParameter {
        //
        private String projectName1;
        private String projectName2;

        public DependencyParameter(String projectName1, String projectName2) {
            this.projectName1 = projectName1;
            this.projectName2 = projectName2;
        }

        public String getProjectName1() {
            return projectName1;
        }

        public void setProjectName1(String projectName1) {
            this.projectName1 = projectName1;
        }

        public String getProjectName2() {
            return projectName2;
        }

        public void setProjectName2(String projectName2) {
            this.projectName2 = projectName2;
        }
    }
}
