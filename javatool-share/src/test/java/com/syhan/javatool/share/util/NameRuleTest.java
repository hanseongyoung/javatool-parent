package com.syhan.javatool.share.util;

import com.syhan.javatool.share.rule.NameRule;
import org.junit.Assert;
import org.junit.Test;

public class NameRuleTest {

    @Test
    public void testChangeName() {
        //
        NameRule nameRule = NameRule.newInstance()
                .add("VO", "DTO");

        Assert.assertEquals("SsgedwkmoDTO", nameRule.changeName("SsgedwkmoVO"));
        Assert.assertEquals("Ssgedwkmo", nameRule.changeName("Ssgedwkmo"));

        Assert.assertEquals("amis3.vo.sp.ss.ge.SsgedwkmoDTO", nameRule.changeName("amis3.vo.sp.ss.ge.SsgedwkmoVO"));
    }
}
