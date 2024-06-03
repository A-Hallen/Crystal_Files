package com.hallen.rfilemanager.infraestructure.utils;

public class Test {
    private static Test instance = null;

    private Test() {
    }

    public static Test getInstance() {
        if (Test.instance == null) {
            Test.instance = new Test();
        }
        return Test.instance;
    }
}
