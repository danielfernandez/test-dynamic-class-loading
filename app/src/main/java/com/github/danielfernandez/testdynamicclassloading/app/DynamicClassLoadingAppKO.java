package com.github.danielfernandez.testdynamicclassloading.app;

import com.github.danielfernandez.testdynamicclassloading.libone.ParentClassFromLibOne;
import com.github.danielfernandez.testdynamicclassloading.libtwo.ChildClassFromLibTwo;

public class DynamicClassLoadingAppKO {

    /*
     * THIS VERSION DOES NOT WORK, A ClassNotFoundException IS THROWN BEFORE EVEN EXECUTING main()
     */


    // If this method received ChildClassFromLibTwo, everything would work OK!
    private static void showMessage(final ParentClassFromLibOne obj) {
        System.out.println(obj.message());
    }


    public static void main(final String[] args) throws Throwable {

        try {

            final ChildClassFromLibTwo obj = new ChildClassFromLibTwo();
            showMessage(obj);

        } catch (final Throwable ignored) {
            // ignored, we just wanted to use it if it was present
        }

        System.out.println("This should be displayed, but no :(");

    }

}
