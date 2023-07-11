package com.smileframework.bullet.rpc;

import org.junit.Test;

import java.lang.reflect.Type;

public class ClassTest {


    @Test
    public void test() throws ClassNotFoundException {
        Object test = 2;
        String typeName = test.getClass().getTypeName();
        Type type = Class.forName(typeName);
        System.out.println(type);
    }
}
