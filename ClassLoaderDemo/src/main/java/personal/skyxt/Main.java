package personal.skyxt;

import test.ClassLoaderPrint;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class Main {

    public static void main(String[] args) {
        URL v1Url = null;
        URL v2Url = null;
        try {
            //TODO 添加判断，判断jar包是否存在，不存在要抛出异常警告
            String basePath = new File("./ClassLoaderDemo/lib").getCanonicalFile().getPath();
            v1Url = new File(basePath + File.separator + "v1/ClassLoaderPrint.jar").toURI().toURL();
            v2Url = new File(basePath + File.separator + "v2/ClassLoaderPrint.jar").toURI().toURL();
            print(v1Url);
            print(v2Url);

            /*
            //以下方式会导致ClassCastException
            Class<?> aClass = Class.forName("test.ClassLoaderPrint", true, urlClassLoader);
            ClassLoaderPrint v1Print = (ClassLoaderPrint) aClass.newInstance();
            v1Print.print();
            */
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
