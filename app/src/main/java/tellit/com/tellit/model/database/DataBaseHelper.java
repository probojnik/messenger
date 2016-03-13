package tellit.com.tellit.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import tellit.com.tellit.model.chat.ChatData;
import tellit.com.tellit.model.chat.MessageData;
import tellit.com.tellit.model.contacts.ContactData;
import tellit.com.tellit.model.review.LikeData;
import tellit.com.tellit.model.review.ReviewData;


public class DataBaseHelper extends OrmLiteSqliteOpenHelper {
    public static final String DATABASE_NAME = "db_im_client";
    private static final int DATABASE_VERSION = 2;
    ConnectionSource connectionSource;

    public DataBaseHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ContactData.class);
            TableUtils.createTable(connectionSource, MessageData.class);
            TableUtils.createTable(connectionSource, ChatData.class);
            TableUtils.createTable(connectionSource, ReviewData.class);
            TableUtils.createTable(connectionSource, LikeData.class);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, ContactData.class, true);
            TableUtils.dropTable(connectionSource, MessageData.class, true);
            TableUtils.dropTable(connectionSource, ChatData.class, true);
            TableUtils.dropTable(connectionSource, ReviewData.class, true);
            TableUtils.dropTable(connectionSource, LikeData.class, true);

            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }





    @Override
    public void close() {
        super.close();

    }


}
