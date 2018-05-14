package com.syhan.javatool.share.util;

import com.syhan.javatool.share.rule.PackageRule;
import com.syhan.javatool.share.util.json.JsonUtil;
import org.junit.Assert;
import org.junit.Test;

public class PackageRuleTest {

    @Test
    public void testSkipRule() {
        PackageRule packageRule = PackageRule.newInstance()
                .add(1, "vo", 4)
                .add(4, "vo", "entity")
                .add(0, "amis3", "kr.amc.amis")
                .add(2, "oo", "order")
                .add(4, "controller", "rest");

        String changedName = packageRule.changePackage("amis3.mc.oo.od.controller");
        System.out.println(changedName);
        Assert.assertEquals("kr.amc.amis.mc.order.od.rest", changedName);

        String changedName2 = packageRule.changePackage("amis3.vo.mc.oo.od");
        System.out.println(changedName2);
        Assert.assertEquals("kr.amc.amis.mc.order.od.entity", changedName2);

        packageRule.set(4, "vo", "ext.spec.sdo", 1);
        String changedName3 = packageRule.changePackage("amis3.vo.mc.oo.od");
        System.out.println(changedName3);
        Assert.assertEquals("kr.amc.amis.mc.order.ext.spec.sdo", changedName3);
    }

    @Test
    public void testRuleWithClassName() {
        //
        PackageRule packageRule = PackageRule.newInstance()
                .add(1, "vo", 4)
                .add(4, "vo", "entity")
                .add(0, "amis3", "kr.amc.amis")
                .add(2, "oo", "order")
                .add(4, "controller", "rest")
                .add("Service", "ext.spec")
                .add("Logic", "ext.logic");

        // amis3.mc.oo, TestService -> kr.amc.amis.mc.order.ext.spec
        // amis3.mc.oo, TestLogic   -> kr.amc.amis.mc.order.ext.logic

        String changedName1 = packageRule.changePackage("amis3.mc.oo", "TestService");
        System.out.println(changedName1);
        Assert.assertEquals("kr.amc.amis.mc.order.ext.spec", changedName1);

        String changedName2 = packageRule.changePackage("amis3.mc.oo", "TestLogic");
        System.out.println(changedName2);
        Assert.assertEquals("kr.amc.amis.mc.order.ext.logic", changedName2);
    }

    @Test
    public void testMovePosition() {
        String[] arr = {"a", "b", "c", "d", "e"};

//        int fromIndex = 4;
//        int toIndex = 0;

        int fromIndex = 1;
        int toIndex = 4;

        //int fromIndex = 1;
        //int toIndex = 3;

        String[] result = new String[arr.length];
        String tmp = arr[fromIndex];

        if (fromIndex > toIndex) {
            //
            throw new RuntimeException("toIndex must be bigger");
        }

        for (int i = 0; i < result.length; i++) {
            if (i >= fromIndex && i < toIndex) {
                result[i] = arr[i + 1];
            } else if (i == toIndex) {
                result[i] = tmp;
            } else {
                result[i] = arr[i];
            }
        }

        System.out.println(JsonUtil.toJson(result));
    }
}
