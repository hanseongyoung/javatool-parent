package com.syhan.javatool.project.complex;

import com.syhan.javatool.generator.converter.JavaAbstractParam;
import com.syhan.javatool.share.rule.NameRule;
import com.syhan.javatool.share.rule.PackageRule;
import com.syhan.javatool.share.test.BaseFileTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ComplexProjectConverterTest extends BaseFileTest {
    //
    private static final String SOURCE_PROJECT_HOME = "../source-project2";
    private static final String RESOURCE_PROJECT_HOME = "../resource-project2";

    // change package rule
    // amis3.mc.oo.od.controller -> kr.amc.amis.mc.order.od.rest
    // amis3.vo.mc.oo.od -> amis3.mc.oo.od.vo    ->    kr.amc.amis.mc.order.od.entity
    //  - 1, vo         -> 4
    //  - 0, amis3      -> kr.amc.amis
    //  - 2, oo         -> order
    //  - 4, controller -> rest

    // change name rule
    // amis3.vo.sp.ss.ge.SsgedwkmoVO -> kr.amc.amis.sp.speciments.ge.entity.SsgedwkmoDTO
    //   - postfix:VO   -> postfix:DTO
    @Test
    public void testConvert() throws Exception {
        //
        ConvertParameter parameter = new ConvertParameter();
        parameter.setNewProjectName0("amis");
        parameter.setNewProjectName1("mediclinic");
        parameter.setNewProjectName2("order");
        parameter.setNewBasePackage("kr.amc.amis");

        parameter.setSourcePackage("amis3.mc.oo");
        parameter.setSourceDtoPackage("amis3.vo.mc.oo");
        parameter.setSourceProjectHomePath(SOURCE_PROJECT_HOME);

        parameter.setSourceSqlMapProjectHomePath(RESOURCE_PROJECT_HOME);
        parameter.setSourceSqlMapResourceFolder("sqlmap/query");
        parameter.setSourceSqlMapPackage("mc.oo");

        parameter.setTargetWorkspace(super.testDirName);

        JavaAbstractParam javaAbstractParam = new JavaAbstractParam();
        javaAbstractParam.setSourceDtoPackage("amis3.vo.mc.oo");

        PackageRule javaAbstractPackageRule = PackageRule.newInstance()
                .add(0, "amis3"     , "kr.amc.amis")
                .add(2, "oo"        , "order")
                .add(1, "vo"        , 4)
                .add(4, "vo"        , "ext.spec.sdo", 1)
                .add("Service", "ext.spec")
                .add("Logic", "ext.logic");

        PackageRule javaConvertPackageRule = PackageRule.newInstance()
                .add(0, "amis3"     , "kr.amc.amis")
                .add(2, "oo"        , "order")
                .add(4, "controller", "rest")
                .add(4, "dao"       , "store")
                .add(4, "service"   , "logic")
                .add(1, "vo"        , 4)
                .add(4, "vo"        , "entity");

        NameRule javaConvertNameRule = NameRule.newInstance()
                .add("Service", "Logic")
                .add("VO", "DTO")
                .add("TO", "SSS")
                .addExceptionPattern("springframework");

        PackageRule namespaceRule = PackageRule.newInstance()
                .setPrefix("kr.amc.amis")
                .add(1, "oo", "order")
                .setPostfix("store.mapper");

        List<String> removeImports = new ArrayList<>();
        removeImports.add("com.foo.bar.DefaultVO");

        ComplexProjectConverter converter = new ComplexProjectConverter(parameter, javaAbstractParam,
                javaConvertNameRule, javaAbstractPackageRule, javaConvertPackageRule, namespaceRule, removeImports);
        converter.convert();
    }
}
