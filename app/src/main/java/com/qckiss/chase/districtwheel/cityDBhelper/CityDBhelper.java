package com.qckiss.chase.districtwheel.cityDBhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.qckiss.chase.districtwheel.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by qckiss on 2016/11/23.
 */
public class CityDBhelper extends SQLiteOpenHelper {
    private final String DB_HELP_TAG = "DBhelp";
    public static final String DB_NAME = "city.db3"; // 数据库名称
    private static final int version = 1; // 数据库版本

    public static String PACKAGE_NAME = "com.qckiss.chase.districtwheel"; //
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME;

    private Context context;

    public CityDBhelper(Context context) {
        /**
         * 第三个参数：CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类
         * 第四个参数：是数据库的版本号 数据库只能升级,不能降级,版本号只能变大不能变小
         */
        super(context, DB_NAME, null, version);
        this.context = context;
    }

    /**
     * onCreate是在数据库创建的时候调用的，主要用来初始化数据表结构和插入数据初始化的记录
     * <p/>
     * 当数据库第一次被创建的时候调用的方法,适合在这个方法里面把数据库的表结构定义出来. 所以只有程序第一次运行的时候才会执行
     * 如果想再看到这个函数执行，必须卸载程序然后重新安装这个app
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(DB_HELP_TAG, "创建数据库SQLite");
//        SQLiteDatabase database = SQLiteDatabase.openDatabase(filepath.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * 当数据库更新的时候调用的方法 这个要版本号发生改变时才会执行
     * 注意，数据库的版本号只可以变大，不能变小，假设我们当前写的版本号是3，运行，然后又改成1，运行则报错。不能变小
     *
     * @param db
     * @param oldVersion 旧的版本号
     * @param newVersion 新
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DB_HELP_TAG, "SQLite数据库更新");
        for (int version = oldVersion + 1; version <= newVersion; version++) {
            upgradeTo(db, version);
        }
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        if (copyRawData(R.raw.city, DB_PATH + "/" + DB_NAME)) {
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/" + DB_NAME, null);
            return database;
        } else {
            return null;
        }
    }

    /**
     * 复制raw文件
     *
     * @param rawId
     * @param dbfile
     * @return
     */
    public boolean copyRawData(int rawId, String dbfile) {
        try {
            File file = new File(dbfile);
            if (!file.exists()) {
                Log.d(DB_HELP_TAG, "开始插入数据raw文件");
                InputStream is = context.getResources().openRawResource(rawId);
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.flush();
                fos.close();
                is.close();
            } else {
                Log.d(DB_HELP_TAG, "已存在文件");
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Upgrade database from (version - 1) to version.
     */
    private void upgradeTo(SQLiteDatabase db, int version) {
        switch (version) {
            case 1:
                break;
            case 2:
                db.execSQL("alter table users add account varchar(20)");
                break;
            default:
                throw new IllegalStateException("Don't know how to upgrade to "
                        + version);
        }
    }
}
