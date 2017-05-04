# Test for unexpected behaviour in JVMs dynamic class loading

Tested with **Java 1.8.0_131**.

This test consists of three artifacts:

   * A library called `libone` containing a class called `ParentClassFromLibOne`.
   * A library called `libtwo` containing a class called `ParentClassFromLibTwo`, which extends from 
   `ParentClassFromLibOne`. This library has a (`compile`-scope) dependency on `libone`.
   * An application module, with classes `DynamicClassLoadingAppOK` and
   `DynamicClassLoadingAppKO` showing how a change in the type of a method
   parameter changes the result from the expected behaviour for dynamic
   class loading to an unexpected one. This module has a (`compile`-scope)
   dependency on `libone` and a (`provided`-scope) dependency on `libtwo`.

Code of `ParentClassFromLibOne`:

```java
public class ParentClassFromLibOne {

    public String message() {
        return "Message from Parent class at LibOne";
    }

}
```

Code of `ChildClassFromLibTwo`:

```java
public class ChildClassFromLibTwo extends ParentClassFromLibOne {

    @Override
    public String message() {
        return "Message from Child class at LibTwo";
    }

}

```

Code of the *app* class in which the behaviour is as expected
and the `System.out.println` call at the end is executed:

```java
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
```

Code of the *app* class in which the behaviour obtained is
unexpected and the `System.out.println` call at the end is
never executed (a `ClassNotFoundException` is thrown instead).
Note the only difference is the type of the parameter of the
`showMessage(...)` method.

```java
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
```

### How to test

First install the two libraries:

```
$ cd libone
$ mvn clean compile install
$ cd ..

$ cd libtwo
$ mvn clean compile install
$ cd ..
```

Then compile and execute the app:

```
$ cd app
$ mvn clean compile
$ mvn exec:java -Dexec.mainClass=com.github.danielfernandez.testdynamicclassloading.app.DynamicClassLoadingAppOK
$ mvn exec:java -Dexec.mainClass=com.github.danielfernandez.testdynamicclassloading.app.DynamicClassLoadingAppKO
```

The first app class executes fine, the second one throws:

```
java.lang.NoClassDefFoundError: com/github/danielfernandez/testdynamicclassloading/libtwo/ChildClassFromLibTwo
	at java.lang.Class.getDeclaredMethods0(Native Method)
	at java.lang.Class.privateGetDeclaredMethods(Class.java:2701)
	at java.lang.Class.privateGetMethodRecursive(Class.java:3048)
	at java.lang.Class.getMethod0(Class.java:3018)
	at java.lang.Class.getMethod(Class.java:1784)
	at org.codehaus.mojo.exec.ExecJavaMojo$1.run(ExecJavaMojo.java:281)
	at java.lang.Thread.run(Thread.java:745)
Caused by: java.lang.ClassNotFoundException: com.github.danielfernandez.testdynamicclassloading.libtwo.ChildClassFromLibTwo
	at java.net.URLClassLoader.findClass(URLClassLoader.java:381)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
	... 7 more
```

Note a `ClassNotFoundException` is actually thrown in both cases, but in the `OK` one it happens when the
`ChildClassFromLibTwo` is loaded when a new instance of such class is created, so it is correctly caught
by the `catch (Throwable t)` block and execution can go on.

Nevertheless, in the `KO` class the `ClassNotFoundException` is thrown **before executing `main()`**, apparently when the
`DynamicClassLoadingAppKO` class itself is loaded, and therefore outside of the `try...catch`, so no code is
executed at all.

