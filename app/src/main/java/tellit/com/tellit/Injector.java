package tellit.com.tellit;

import android.util.Log;

import dagger.ObjectGraph;

/**
 * Created by probojnik on 19.07.15.
 */
public class Injector {
    public static ObjectGraph graph;
    public static void init(Object... modules)
    {
        if(graph == null) {
            graph = ObjectGraph.create(modules);
            graph.injectStatics();
        }
        else
            graph = graph.plus(modules);
    }

    public static void inject(Object target)
    {
        graph.inject(target);
    }
}
