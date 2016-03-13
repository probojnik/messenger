package tellit.com.tellit.controller;

import android.os.AsyncTask;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by probojnik on 11.08.15.
 */
public class SearchThreadController {

//    ExecutorService executorService;
    private static SearchThreadController ourInstance = new SearchThreadController();
    ThreadPoolExecutor threadPoolExecutor;
    public static SearchThreadController getInstance() {
        return ourInstance;
    }

    private SearchThreadController() {
//        executorService = Executors.newSingleThreadExecutor();
        threadPoolExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1));

    }

    public void execute(AsyncTask task){
//            task.executeOnExecutor(executorService);
        try {
            task.executeOnExecutor(threadPoolExecutor);
        }catch (RejectedExecutionException ex){}


    }
    public void execute(AsyncTask task,Object... param){
//            task.executeOnExecutor(executorService,param);
        try {
            task.executeOnExecutor(threadPoolExecutor, param);
        }catch (RejectedExecutionException ex){}
    }

    public boolean isRun(){
        return threadPoolExecutor.getActiveCount() > 0 ;
    }
}
