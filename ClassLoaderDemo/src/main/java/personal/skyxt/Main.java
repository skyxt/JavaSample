package personal.skyxt;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class Main {
    /**
     * TODO
     * @param args
     */

    public static void main(String[] args) {
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

            //设置当前上下文类加载器未自定义的类加载器，破坏双亲委派模式，测试的原生双亲委派模式下的java类还能否获取（结论：无法获取）
            System.out.println("当前线程原始ClassLoader: " + Thread.currentThread().getContextClassLoader().getClass().getName());
//            URLClassLoader urlClassLoader = new URLClassLoader(new URL[] {new File(basePath + File.separator + "v1/ClassLoaderPrint.jar").toURI().toURL()},null);
            WebAppClassLoader urlClassLoader = new WebAppClassLoader(new URL[] {new File(basePath + File.separator + "v1/ClassLoaderPrint.jar").toURI().toURL()});
            Thread.currentThread().setContextClassLoader(urlClassLoader);
            System.out.println("修改当前线程Classloader为：" + urlClassLoader.getClass().getName());
//            Class<?> aClass = urlClassLoader.loadClass("java.lang.Object");
            Class<?> aClass = Class.forName("java.lang.Object", true, urlClassLoader);
            Method toString = aClass.getMethod("toString", null);
            Object invoke = toString.invoke(aClass.newInstance(), null);
            System.out.println(invoke);
            Thread.currentThread().setContextClassLoader(null);

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
        Class<?> v1Class = Class.forName("test.ClassLoaderPrint", true, v1ClassLoader);
        Method printv1 = v1Class.getMethod("print", null);
        System.out.println("---------------");
        printv1.invoke(v1Class.newInstance(), null);
        System.out.println("---------------");
    }

}
