package com.polopoly.ps.hotdeploy.client;

public class NoSuchMajorException extends Exception {

    public NoSuchMajorException(String majorName) {
        super("The major \"" + majorName + "\" was unknown.");
    }

}
