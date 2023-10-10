package personal.skyxt;

import java.net.URL;
import java.net.URLClassLoader;

public class WebAppClassloader extends URLClassLoader {

    public WebAppClassloader(URL[] urls) {
        super(urls);
    }

    public WebAppClassloader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        //先查本ClassLoader,如果没有再找父加载器
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    c = findClass(name);
                } catch (ClassNotFoundException e) {
                    System.out.println("本加载器未找到类"+ e.getCause());
                }

                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    long t1 = System.nanoTime();
                    try {
                        if (this.getParent() != null) {
                            c = super.loadClass(name, false);
                        }
                    } catch (ClassNotFoundException e) {
                        // ClassNotFoundException thrown if class not found
                        // from the non-null parent class loader
                    }

                    // this is the defining class loader; record the stats
                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
            }
            return c;
        }
    }

//    @Override
//    protected Class<?> findClass(String name) throws ClassNotFoundException {
//        return super.findClass(name);
//    }

}
