package net.togogo.newsclient;

import android.app.Application;

import net.togogo.newsclient.bean.DaoMaster;
import net.togogo.newsclient.bean.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Lam on 2017/9/17.
 */

public class App extends Application {
    public static final boolean ENCRYPTED = false;

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "news-db-encrypted" : "news-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }


}
