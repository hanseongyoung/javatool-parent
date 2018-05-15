package com.syhan.javatool.project.complex;

import com.syhan.javatool.generator.converter.*;
import com.syhan.javatool.project.creator.NestedProjectCreator;
import com.syhan.javatool.project.model.ProjectModel;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.rule.PackageRule;

import java.io.IOException;

public class ComplexProjectConverter {
    //
    // 소스 프로젝트 홈, 소스 패키지, 프로젝트 명칭 1/2레벨, 대상 폴더
    private ConvertParameter param;
    private JavaAbstractParam javaAbstractParam;
    private PackageRule javaAbstractPackageRule;
    private PackageRule javaConvertPackageRule;

    public ComplexProjectConverter(ConvertParameter convertParameter, JavaAbstractParam javaAbstractParam, PackageRule javaAbstractPackageRule, PackageRule javaConvertPackageRule) {
        //
        this.param = convertParameter;
        this.javaAbstractParam = javaAbstractParam;
        this.javaAbstractPackageRule = javaAbstractPackageRule;
        this.javaConvertPackageRule = javaConvertPackageRule;
    }

    public void convert() throws IOException {
        // 1. 1레벨 프로젝트 구조를 만든다.
        // 2. 2레벨 프로젝트 구조를 만든다.(2레벨 parent, stub, skeleton, service, share)
        // 3. extService 존재하는 경우 인터페이스 분리하여 stup, skeleton 나누어 담는다.
        //    - interface -> stub:spec
        //    - vo, to -> stup:spec.sdo (단 sourcePackage 범위를 벗어나는 경우 이동하지 않는다.)
        //    - logic class -> skeleton:logic
        // 4. 패키지 내의 내용물을 이동한다.
        //    - controller -> rest
        //    - vo -> entity(stub:spec.sdo 에 이미 이동한 경우 제외)
        //    - service -> logic
        //    - dao -> store
        // 5. Proxy, RemoteProxy, LocalProxy 생성 / 업데이트
        ProjectModel model = createProjectModel();
        createProject(model);
        moveJavaSource(model);
    }

    private void moveJavaSource(ProjectModel model) throws IOException {
        //
        ProjectConfiguration sourceConfig = new ProjectConfiguration(ConfigurationType.Source, param.getSourceProjectHomePath());
        ProjectConfiguration stubConfig = model.findBySuffix(PROJECT_SUFFIX_STUB).configuration(ConfigurationType.Target);
        ProjectConfiguration skeletonConfig = model.findBySuffix(PROJECT_SUFFIX_SKELETON).configuration(ConfigurationType.Target);
        ProjectConfiguration serviceConfig = model.findBySuffix(PROJECT_SUFFIX_SERVICE).configuration(ConfigurationType.Target);

        JavaInterfaceAbstracter abstracter = new JavaInterfaceAbstracter(sourceConfig, stubConfig, skeletonConfig,
                javaAbstractPackageRule, javaAbstractParam);
        JavaConverter javaConverter = new JavaConverter(sourceConfig, serviceConfig, javaConvertPackageRule);
        DtoManagingJavaConverter dtoConverter = new DtoManagingJavaConverter(javaConverter);

        // convert sourcePackage
        new PackageConverter(new ProjectItemConverter(sourceConfig, ProjectItemType.Java) {
            @Override
            public void convert(String sourceFileName) throws IOException {
                //
                System.out.println("sourcFile:"+sourceFileName);
                if (convertExtService(sourceFileName, abstracter)) return;
                if (convertJava(sourceFileName, javaConverter)) return;

                System.err.println("Couldn't convert --> " + sourceFileName);
            }
        }).convert(param.getSourcePackage());

        // convert sourceDtoPackage
        new PackageConverter(new ProjectItemConverter(sourceConfig, ProjectItemType.Java) {
            @Override
            public void convert(String sourceFileName) throws IOException {
                //
                dtoConverter.convert(sourceFileName);
            }
        }).convert(param.getSourceDtoPackage());
    }

    private boolean convertExtService(String sourceFileName, JavaInterfaceAbstracter abstracter) throws IOException {
        //
        if (!sourceFileName.endsWith("ExtService.java")) {
            return false;
        }

        abstracter.convert(sourceFileName);
        return true;
    }

    private boolean convertJava(String sourceFileName, JavaConverter javaConverter) throws IOException {
        //
        javaConverter.convert(sourceFileName);
        return true;
    }

    private void createProject(ProjectModel model) {
        //
        NestedProjectCreator projectCreator = new NestedProjectCreator(param.getTargetWorkspace());
        projectCreator.create(model);
    }

    private static String PROJECT_SUFFIX_BOOT = "-boot";
    private static String PROJECT_SUFFIX_WAR = "-war";
    private static String PROJECT_SUFFIX_STUB = "-ext-stub";
    private static String PROJECT_SUFFIX_SKELETON = "-ext-skeleton";
    private static String PROJECT_SUFFIX_SERVICE = "-service";
    private static String PROJECT_SUFFIX_SHARE = "-share";

    private ProjectModel createProjectModel() {
        // level1
        String nameLevel1 = param.getNewProjectName0() + "-" + param.getNewProjectName1();
        String groupId = param.getNewBasePackage();
        String version = "0.0.1-SNAPSHOT";
        ProjectModel projectModelLevel1 = new ProjectModel(nameLevel1, groupId, version, "pom");
        projectModelLevel1.setWorkspacePath(param.getTargetWorkspace());

        // level2 parent
        String nameLevel2 = param.getNewProjectName1() + "-" + param.getNewProjectName2();
        ProjectModel projectModelLevel2 = new ProjectModel(nameLevel2, groupId, version, "pom");
        projectModelLevel1.add(projectModelLevel2);

        // level2
        ProjectModel bootModel = new ProjectModel(nameLevel2 + PROJECT_SUFFIX_BOOT, groupId, version);
        projectModelLevel2.add(bootModel);

        ProjectModel warModel = new ProjectModel(nameLevel2 + PROJECT_SUFFIX_WAR, groupId, version);
        projectModelLevel2.add(warModel);

        ProjectModel stubModel = new ProjectModel(nameLevel2 + PROJECT_SUFFIX_STUB, groupId, version);
        projectModelLevel2.add(stubModel);

        ProjectModel skeletonModel = new ProjectModel(nameLevel2 + PROJECT_SUFFIX_SKELETON, groupId, version);
        projectModelLevel2.add(skeletonModel);

        ProjectModel serviceModel = new ProjectModel(nameLevel2 + PROJECT_SUFFIX_SERVICE, groupId, version);
        projectModelLevel2.add(serviceModel);

        ProjectModel shareModel = new ProjectModel(nameLevel2 + PROJECT_SUFFIX_SHARE, groupId, version);
        projectModelLevel2.add(shareModel);

        return projectModelLevel1;
    }

}
