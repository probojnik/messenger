package tellit.com.tellit.model.database;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;


/**
 */
public class HelperFactory<T> {

    private static DataBaseHelper databaseHelper;
    private static HelperFactory instance = null;

    private HelperFactory() {
    }

    public static DataBaseHelper getHelper(){
        return databaseHelper;
    }
    public static void setHelper(Context context){
        databaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
    }
    public static void releaseHelper(){
        OpenHelperManager.releaseHelper();
        databaseHelper = null;
    }
    public static HelperFactory getInstans(){
        if(instance == null) instance = new HelperFactory();
        return instance;
    }

    public  Dao<T,Integer> getDao(Class<T> clazz){
        try {
            Dao<T,Integer> dao = DaoManager.lookupDao(databaseHelper.getConnectionSource(), clazz);
            if(dao == null) {
                dao = DaoManager.createDao(databaseHelper.getConnectionSource(), clazz);
                dao.setAutoCommit(databaseHelper.getConnectionSource().getReadWriteConnection(), true);
            }
            return dao;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
