package com.syhan.javatool.generator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ListTest {
    @Test
    public void testContains() {
        //
        List<String> list = new ArrayList<>();
        list.add("hello");
        list.add("bye");
        System.out.println(list.contains("hello"));
        System.out.println(list.contains("hello1"));
        System.out.println(list.contains("bye"));
    }
}
