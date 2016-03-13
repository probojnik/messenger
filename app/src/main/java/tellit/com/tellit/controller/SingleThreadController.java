package tellit.com.tellit.controller;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import tellit.com.tellit.controller.operations.OperationCalback;

/**
 * Created by probojnik on 11.08.15.
 */
public class SingleThreadController {

//    ExecutorService executorService;
    private static SingleThreadController ourInstance = new SingleThreadController();
    ThreadPoolExecutor threadPoolExecutor;
    public static SingleThreadController getInstance() {
        return ourInstance;
    }

    private SingleThreadController() {
//        executorService = Executors.newSingleThreadExecutor();

        threadPoolExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());


    }

    public void execute(AsyncTask task,OperationCalback calback) {


    }


    public void execute(AsyncTask task){

        try {
            task.executeOnExecutor(threadPoolExecutor);
        }catch (RejectedExecutionException ex){ex.printStackTrace();}



    }
    public void execute(AsyncTask task,Object... param){
        try {
            task.executeOnExecutor(threadPoolExecutor,param);
        }catch (RejectedExecutionException ex){ex.printStackTrace();}

    }

    public boolean isRun(){
        return threadPoolExecutor.getActiveCount() > 0 ;
    }
}
