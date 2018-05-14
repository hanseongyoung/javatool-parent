package com.syhan.javatool.project.complex;

import com.syhan.javatool.share.rule.PackageRule;
import com.syhan.javatool.share.test.BaseFileTest;
import org.junit.Test;

public class ComplexProjectConverterTest extends BaseFileTest {
    //
    private static final String SOURCE_PROJECT_HOME = "../source-project2";

    // change package rule
    // amis3.mc.oo.od.controller -> kr.amc.amis.mc.order.od.rest
    // amis3.vo.mc.oo.od -> amis3.mc.oo.od.vo    ->    kr.amc.amis.mc.order.od.entity
    //  - 1, vo         -> 4
    //  - 0, amis3      -> kr.amc.amis
    //  - 2, oo         -> order
    //  - 4, controller -> rest
    @Test
    public void testConvert() throws Exception {
        //
        ConvertParameter parameter = new ConvertParameter();
        parameter.setNewProjectName0("amis");
        parameter.setNewProjectName1("mediclinic");
        parameter.setNewProjectName2("order");
        parameter.setNewBasePackage("kr.amc.amis");
        parameter.setSourcePackage("amis3.mc.oo");
        parameter.setSourceBasePackage("amis3");
        parameter.setSourceProjectHomePath(SOURCE_PROJECT_HOME);
        parameter.setTargetWorkspace(super.testDirName);

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

        ComplexProjectConverter converter = new ComplexProjectConverter(parameter, javaAbstractPackageRule, javaConvertPackageRule);
        converter.convert();
    }
}
