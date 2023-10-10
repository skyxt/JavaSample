package personal.skyxt;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    /**
     * TODO
     * @param args
     */

    public final static String CLASS_PRINT = "test.ClassLoaderPrint";

    public static void main(String[] args) {
        ClassLoader originClassLoader = Thread.currentThread().getContextClassLoader();
        URL v1Url = null;
        URL v2Url = null;
        try {
            //TODO 添加判断，判断jar包是否存在，不存在要抛出异常警告
            //
            String basePath = new File("./ClassLoaderDemo/lib").getCanonicalFile().getPath();
            v1Url = new File(basePath + File.separator + "v1/ClassLoaderPrint.jar").toURI().toURL();
            v2Url = new File(basePath + File.separator + "v2/ClassLoaderPrint.jar").toURI().toURL();
            print(v1Url);
            print(v2Url);

            /*
            //以下方式可能会导致ClassCastException
            Class<?> aClass = Class.forName("test.ClassLoaderPrint", true, urlClassLoader);
            ClassLoaderPrint v1Print = (ClassLoaderPrint) aClass.newInstance();
            v1Print.print();
            */

            //设置当前上下文类加载器为自定义的类加载器(该加载器无父加载器)，破坏双亲委派模式，测试的原生双亲委派模式下的java类还能否获取（结论：无法获取）
            System.out.println("当前线程原始ClassLoader: " + originClassLoader.getClass().getName());
//            URLClassLoader urlClassLoader = new URLClassLoader(new URL[] {new File(basePath + File.separator + "v1/ClassLoaderPrint.jar").toURI().toURL()},null);
            NoParentClassLoader urlClassLoader = new NoParentClassLoader(new URL[] {new File(basePath + File.separator + "v1/ClassLoaderPrint.jar").toURI().toURL()});
            Thread.currentThread().setContextClassLoader(urlClassLoader);
            System.out.println("修改当前线程Classloader为：" + urlClassLoader.getClass().getName());
//            Class<?> aClass = urlClassLoader.loadClass("java.lang.Object");
//            Class<?> aClass = Class.forName("java.lang.Object", true, urlClassLoader);
//            Method toString = aClass.getMethod("toString", null);
//            Object invoke = toString.invoke(aClass.newInstance(), null);
//            System.out.println(invoke);
            Thread.currentThread().setContextClassLoader(originClassLoader);


            /**
             *设置当前上下文类加载器为自定义的类加载器(该加载器优先本加载器加载，父加载器仅在本加载器无法加载时加载)，破坏双亲委派模式，
             * 测试父加载器加载的java类还能否获取（结论：可以获取）
             * 设计一个与父加载器冲突的类，测试本加载器加载的该java类获取的是否是本加载器加载的类(结论：)
             */
            //设置当前上下文类加载器为自定义的类加载器(该加载器无父加载器)，破坏双亲委派模式，测试的原生双亲委派模式下的java类还能否获取（结论：无法获取）
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            System.out.println("当前线程原始ClassLoader: " + contextClassLoader.getClass().getName());
            File libDir = new File(basePath + File.separator + "v1");
            List<URL> urls = new ArrayList<>();
            if (libDir.listFiles() != null) {
                Arrays.stream(libDir.listFiles()).forEach(file -> {
                    if (file.isFile() && file.getName().endsWith(".jar")) {
                        try {
                            urls.add(file.toURI().toURL());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            WebAppClassloader webAppClassloader = new WebAppClassloader(urls.toArray(new URL[0]), contextClassLoader);
            try {
                //下面方法应该抛出类找不到异常
                Class<?> printClass = Class.forName(CLASS_PRINT, true, contextClassLoader);
            } catch (ClassNotFoundException e) {
                System.out.println("使用父类未找到" + CLASS_PRINT + "类");
            }
            Thread.currentThread().setContextClassLoader(webAppClassloader);
            Class<?> printClass = Class.forName(CLASS_PRINT, true, Thread.currentThread().getContextClassLoader());
            Method print = printClass.getMethod("print", null);
            System.out.println("==========第三次功能打印=============");
            print.invoke(printClass.newInstance(), null);
            System.out.println("===========第三次功能打印=============");
            System.out.println("===========开始测试自定义HashMap=============");
            Class<?> mapClass = Class.forName("java.util.HashMap", true, Thread.currentThread().getContextClassLoader());
            Method print1 = mapClass.getMethod("println", null);
            //JDK官方不允许自定义java开头包名的类，所以这个方法会报错
            try {
                print1.invoke(mapClass.newInstance(), null);
            } catch (SecurityException se) {
                System.out.println("自定义HashMap异常：" + se.getMessage());
            }
            System.out.println("===========结束测试自定义HashMap=============");
            Thread.currentThread().setContextClassLoader(originClassLoader);


        } catch (MalformedURLException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void print(URL url) throws ClassNotFoundException,
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {
        URLClassLoader v1ClassLoader = new URLClassLoader(new URL[]{ url }, null);
        Class<?> v1Class = Class.forName(CLASS_PRINT, true, v1ClassLoader);
        Method printv1 = v1Class.getMethod("print", null);
        System.out.println("---------------");
        printv1.invoke(v1Class.newInstance(), null);
        System.out.println("---------------");
    }

}
