package personal.skyxt;

import test.ClassLoaderPrint;

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
            //添加判断，判断jar包是否存在，不存在要抛出异常警告
            v1Url = new URL("file:C:\\Users\\skyxt\\IdeaProjects\\JavaSample\\ClassLoaderDemo\\lib\\v1\\ClassLoaderPrint.jar");
            v2Url = new URL("file:C:\\Users\\skyxt\\IdeaProjects\\JavaSample\\ClassLoaderDemo\\lib\\v2\\ClassLoaderPrint.jar");
            URLClassLoader v1ClassLoader = new URLClassLoader(new URL[]{v1Url}, null);
            Class<?> v1Class = Class.forName("test.ClassLoaderPrint", true, v1ClassLoader);
            Method printv1 = v1Class.getMethod("print", null);
            printv1.invoke(v1Class.newInstance(), null);

            URLClassLoader v2ClassLoader = new URLClassLoader(new URL[]{v2Url}, null);
            Class<?> v2Class = Class.forName("test.ClassLoaderPrint", true, v2ClassLoader);
            Method printv2 = v2Class.getMethod("print", null);
            printv2.invoke(v1Class.newInstance(), null);
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
        }


    }
}
