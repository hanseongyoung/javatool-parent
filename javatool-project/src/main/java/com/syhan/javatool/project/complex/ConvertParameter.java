package com.syhan.javatool.project.complex;

public class ConvertParameter {
    //
    // 소스 프로젝트 홈, 소스 패키지, 프로젝트 명칭 1/2레벨, 대상 폴더
    private String sourceProjectHomePath;
    private String sourcePackage;       // com.foo.bar
    private String sourceBasePackage;   // com
    private String newProjectName0;
    private String newProjectName1;
    private String newProjectName2;
    private String newBasePackage;
    private String targetWorkspace;

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

    public String getTargetWorkspace() {
        return targetWorkspace;
    }

    public void setTargetWorkspace(String targetWorkspace) {
        this.targetWorkspace = targetWorkspace;
    }

    public String getNewProjectName0() {
        return newProjectName0;
    }

    public void setNewProjectName0(String newProjectName0) {
        this.newProjectName0 = newProjectName0;
    }

    public String getNewBasePackage() {
        return newBasePackage;
    }

    public void setNewBasePackage(String newBasePackage) {
        this.newBasePackage = newBasePackage;
    }

    public String getSourceBasePackage() {
        return sourceBasePackage;
    }

    public void setSourceBasePackage(String sourceBasePackage) {
        this.sourceBasePackage = sourceBasePackage;
    }
}
