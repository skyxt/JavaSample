package personal.skyxt;

import java.net.URL;
import java.net.URLClassLoader;

public class WebAppClassLoader extends URLClassLoader {

    public WebAppClassLoader(URL[] urls) {
        super(urls, null);
    }

    //TODO 先查本ClassLoader,如果没有再找父加载器
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();

                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    long t1 = System.nanoTime();
                    c = findClass(name);
                }
            }
            return c;
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }
}
