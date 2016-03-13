package tellit.com.tellit.controller;

import android.os.AsyncTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import tellit.com.tellit.MyApplication;

/**
 * Created by probojnik on 17.07.15.
 */
public class MultiThreadController {
    final static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();
    ThreadPoolExecutor threadPoolExecutor;
    private static MultiThreadController ourInstance = new MultiThreadController();

    public static MultiThreadController getInstance() {
        return ourInstance;
    }

    private MultiThreadController() {
        threadPoolExecutor = new ThreadPoolExecutor(NUMBER_OF_CORES*2,NUMBER_OF_CORES*2,2, TimeUnit.SECONDS,blockingQueue);
    }

    public void execute(AsyncTask task){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(threadPoolExecutor);
        }else{
            task.execute();
        }

    }
    public void execute(AsyncTask task,Object... param){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(threadPoolExecutor,param);
        }else{
            task.execute(param);
        }
    }
    public boolean isRun(){
        return threadPoolExecutor.getActiveCount() > 0 ;
    }
}
