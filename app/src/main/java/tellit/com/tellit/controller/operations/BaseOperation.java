package tellit.com.tellit.controller.operations;

import android.os.AsyncTask;

/**
 * Created by probojnik on 04.09.15.
 */
public abstract class BaseOperation extends AsyncTask {
    OperationCalback operationCalback;

    public BaseOperation(OperationCalback operationCalback) {
        this.operationCalback = operationCalback;
    }


}
