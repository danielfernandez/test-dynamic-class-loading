package com.github.danielfernandez.testdynamicclassloading.libtwo;

import com.github.danielfernandez.testdynamicclassloading.libone.ParentClassFromLibOne;

public class ChildClassFromLibTwo extends ParentClassFromLibOne {


    @Override
    public String message() {
        return "Message from Child class at LibTwo";
    }

}
