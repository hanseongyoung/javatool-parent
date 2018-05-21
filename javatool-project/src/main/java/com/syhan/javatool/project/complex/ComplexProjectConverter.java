package com.syhan.javatool.project.complex;

import com.syhan.javatool.generator.converter.*;
import com.syhan.javatool.project.creator.NestedProjectCreator;
import com.syhan.javatool.project.model.ProjectModel;
import com.syhan.javatool.share.config.ConfigurationType;
import com.syhan.javatool.share.config.ProjectConfiguration;
import com.syhan.javatool.share.config.SourceFolders;
import com.syhan.javatool.share.rule.NameRule;
import com.syhan.javatool.share.rule.PackageRule;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

public class ComplexProjectConverter {
    //
    // 소스 프로젝트 홈, 소스 패키지, 프로젝트 명칭 1/2레벨, 대상 폴더
    private ConvertParameter param;
    private JavaAbstractParam javaAbstractParam;
    private NameRule javaConvertNameRule;
    private PackageRule javaConvertPackageRule;
    private PackageRule packageRuleForCheckStubDto;
    private PackageRule sqlMapNamespaceRule;

    public ComplexProjectConverter(ConvertParameter convertParameter, JavaAbstractParam javaAbstractParam,
                                   NameRule javaConvertNameRule,
                                   PackageRule javaConvertPackageRule, PackageRule packageRuleForCheckStubDto,
                                   PackageRule sqlMapNamespaceRule) {
        //
        this.param = convertParameter;
        this.javaAbstractParam = javaAbstractParam;
        this.javaConvertNameRule = javaConvertNameRule;
        this.javaConvertPackageRule = javaConvertPackageRule;
        this.packageRuleForCheckStubDto = packageRuleForCheckStubDto;
        this.sqlMapNamespaceRule = sqlMapNamespaceRule;
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
        moveSqlMap(model);
    }

    private void moveJavaSource(ProjectModel model) throws IOException {
        //
        ProjectConfiguration sourceConfig = new ProjectConfiguration(ConfigurationType.Source, param.getSourceProjectHomePath());
        ProjectConfiguration stubConfig = model.findBySuffix(PROJECT_SUFFIX_STUB).configuration(ConfigurationType.Target);
        ProjectConfiguration skeletonConfig = model.findBySuffix(PROJECT_SUFFIX_SKELETON).configuration(ConfigurationType.Target);
        ProjectConfiguration serviceConfig = model.findBySuffix(PROJECT_SUFFIX_SERVICE).configuration(ConfigurationType.Target);

        JavaInterfaceAbstracter abstracter = new JavaInterfaceAbstracter(sourceConfig, stubConfig, skeletonConfig,
                javaConvertNameRule, javaConvertPackageRule, javaAbstractParam);
        JavaConverter serviceJavaConverter = new JavaConverter(sourceConfig, serviceConfig, javaConvertNameRule, javaConvertPackageRule);
        JavaConverter stubJavaConverter = new JavaConverter(sourceConfig, stubConfig, javaConvertNameRule, javaConvertPackageRule);
        DtoManagingJavaConverter dtoConverter = new DtoManagingJavaConverter(javaConvertPackageRule, serviceJavaConverter, stubJavaConverter);

        // update packageRule for stub dto
        new PackageConverter(new ProjectItemConverter(sourceConfig, ProjectItemType.Java) {
            @Override
            public void convert(String sourceFileName) throws IOException {
                //
                updatePackageRuleUsingExtService(sourceFileName, abstracter);
            }
        }).convert(param.getSourcePackage());

        // convert sourcePackage
        new PackageConverter(new ProjectItemConverter(sourceConfig, ProjectItemType.Java) {
            @Override
            public void convert(String sourceFileName) throws IOException {
                //
                if (convertExtService(sourceFileName, abstracter)) return;
                if (convertJava(sourceFileName, serviceJavaConverter)) return;

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

    private void moveSqlMap(ProjectModel model) throws IOException {
        //
        String srcMainResources = param.getSourceSqlMapResourceFolder().replaceAll("/", Matcher.quoteReplacement(File.separator));
        SourceFolders sourceFolders = SourceFolders.newSourceFolders(null, srcMainResources);

        ProjectConfiguration sqlMapSourceConfig = new ProjectConfiguration(ConfigurationType.Source, param.getSourceSqlMapProjectHomePath(), sourceFolders);
        ProjectConfiguration serviceConfig = model.findBySuffix(PROJECT_SUFFIX_SERVICE).configuration(ConfigurationType.Target);

        MyBatisMapperCreator mapperCreator = new MyBatisMapperCreator(sqlMapSourceConfig, serviceConfig, serviceConfig,
                javaConvertNameRule, sqlMapNamespaceRule, javaConvertPackageRule);

        // convert sqlMap
        new PackageConverter(new ProjectItemConverter(sqlMapSourceConfig, ProjectItemType.MyBatisMapper) {
            @Override
            public void convert(String sourceFileName) throws IOException {
                mapperCreator.convert(sourceFileName);
            }
        }).convert(param.getSourceSqlMapPackage());
    }


    private void updatePackageRuleUsingExtService(String sourceFileName, JavaInterfaceAbstracter abstracter) throws IOException {
        //
        if (!sourceFileName.endsWith("ExtService.java")) {
            return;
        }
        List<PackageRule.ChangeImport> stubStubDto = abstracter.findUsingDtoChangeInfo(sourceFileName, packageRuleForCheckStubDto);
        for (PackageRule.ChangeImport changeImport : stubStubDto) {
            javaConvertPackageRule.putChangeImport(changeImport);
        }
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
        ProjectModel projectModelLevel1 = new ProjectModel(nameLevel1, groupId, version, "pom")
                .setWorkspacePath(param.getTargetWorkspace())
                .addDependency("junit", "junit")
                .addDependency("org.slf4j", "slf4j-api")
                .addDependency("kr.amc.amil", "amil-amcdatafilter", "1.0-SNAPSHOT")
                .addDependency("kr.amc.amil", "amil-resource", "1.0-SNAPSHOT");

        // level2 parent
        String nameLevel2 = param.getNewProjectName1() + "-" + param.getNewProjectName2();
        ProjectModel projectModelLevel2 = new ProjectModel(nameLevel2, groupId, version, "pom");
        projectModelLevel1.add(projectModelLevel2);

        // level2
        ProjectModel shareModel = new ProjectModel(nameLevel2 + PROJECT_SUFFIX_SHARE, groupId, version);
        projectModelLevel2.add(shareModel);

        ProjectModel stubModel = new ProjectModel(nameLevel2 + PROJECT_SUFFIX_STUB, groupId, version)
                .addDependency(shareModel)
                .addDependency("org.springframework.cloud", "spring-cloud-starter-openfeign");
        projectModelLevel2.add(stubModel);

        ProjectModel serviceModel = new ProjectModel(nameLevel2 + PROJECT_SUFFIX_SERVICE, groupId, version)
                .addDependency(shareModel)
                .addDependency("org.mybatis.spring.boot", "mybatis-spring-boot-starter", "1.3.1")
                .addDependency("org.springframework", "spring-web");
        projectModelLevel2.add(serviceModel);

        ProjectModel skeletonModel = new ProjectModel(nameLevel2 + PROJECT_SUFFIX_SKELETON, groupId, version)
                .addDependency(stubModel)
                .addDependency(serviceModel);
        projectModelLevel2.add(skeletonModel);

        ProjectModel bootModel = new ProjectModel(nameLevel2 + PROJECT_SUFFIX_BOOT, groupId, version)
                .addDependency(skeletonModel)
                .addDependency("org.springframework.boot", "spring-boot-starter-web")
                .addDependency("com.oracle", "ojdbc7", "12.1.0.2")
                .addDependency("org.bgee.log4jdbc-log4j2", "log4jdbc-log4j2-jdbc4.1", "1.16");
        projectModelLevel2.add(bootModel);

        ProjectModel warModel = new ProjectModel(nameLevel2 + PROJECT_SUFFIX_WAR, groupId, version);
        projectModelLevel2.add(warModel);

        return projectModelLevel1;
    }

}
