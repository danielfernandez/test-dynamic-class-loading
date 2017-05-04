package com.github.danielfernandez.testdynamicclassloading.app;

import com.github.danielfernandez.testdynamicclassloading.libtwo.ChildClassFromLibTwo;

public class DynamicClassLoadingAppOK {

    /*
     * THIS VERSION WORKS OK, BECAUSE showMessage RECEIVES THE CHILD CLASS AS A PARAMETER
     */


    // If this method received ParentClassFromLibOne, we would have a ClassNotFoundException
    private static void showMessage(final ChildClassFromLibTwo obj) {
        System.out.println(obj.message());
    }


    public static void main(final String[] args) throws Throwable {

        try {

            final ChildClassFromLibTwo obj = new ChildClassFromLibTwo();
            showMessage(obj);

        } catch (final Throwable ignored) {
            // ignored, we just wanted to use it if it was present
        }

        System.out.println("This is displayed perfecty fine... as expected.");

    }

}
