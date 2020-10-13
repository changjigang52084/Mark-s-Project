package com.yqhd.baizelocationlib;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
  * @ClassName: AssetsDatabaseManager
  * @Description: TODO Assets数据库管理器
  * @author longyihuang
  * @date 2016年9月23日 下午4:45:48
  *
  */
public class AssetsDatabaseManager {
	private static final String TAG = AssetsDatabaseManager.class.getSimpleName();
	/**
	  * 存储数据库路劲
	  */
	private static String databasepath = "/data/data/%s/database"; 
	
	/**
	  * 数据库集合，可存储多个数据库对象
	  */
	private Map<String, SQLiteDatabase> databases = new HashMap<String, SQLiteDatabase>();
	
	private Context context = null;
	
	private static AssetsDatabaseManager instance = null;

	private AssetsDatabaseManager(Context context) {
		this.context = context;  
	}

	public static AssetsDatabaseManager getInstance(Context context) {
		if (null == instance) {
			synchronized (AssetsDatabaseManager.class) {
				if (null == instance) {
					instance = new AssetsDatabaseManager(context);
				}
			}

		}
		return instance;
	}
	
	/**
	  * @Title: getDatabase
	  * @Description: TODO 根据Assets中的数据库文件名获取数据库对象
	  * @param  dbfile Assets中的数据库文件名
	  * @return SQLiteDatabase   数据库对象
	  * @throws
	  */
	public SQLiteDatabase getDatabase(String dbfile) {  
        if(databases.get(dbfile) != null){  
        	Log.d(TAG, String.format("Return a database copy of %s", dbfile));
            return (SQLiteDatabase) databases.get(dbfile);  
        }  
        if(context==null)  
            return null;  
          
        Log.d(TAG, String.format("Create database %s", dbfile));  
        String spath = getDatabaseFilepath();  
        String sfile = getDatabaseFile(dbfile);  
          
        File file = new File(sfile);  
        SharedPreferences dbs = context.getSharedPreferences(AssetsDatabaseManager.class.toString(), 0);  
        boolean flag = dbs.getBoolean(dbfile, false); // Get Database file flag, if true means this database file was copied and valid  
        if(!flag || !file.exists()){  
            file = new File(spath);  
            if(!file.exists() && !file.mkdirs()){  
            	Log.e(TAG,  "Create \""+spath+"\" fail!");  
                return null;  
            }  
            if(!copyAssetsToFilesystem(dbfile, sfile)){  
            	Log.d(TAG, String.format("Copy %s to %s fail!", dbfile, sfile));  
                return null;  
            }  
              
            dbs.edit().putBoolean(dbfile, true).commit();  
        }  
          
        SQLiteDatabase db = SQLiteDatabase.openDatabase(sfile, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);  
        if(db != null){  
            databases.put(dbfile, db);  
        }  
        return db;  
    } 
	
	 /**
	  * @Title: copyAssetsToFilesystem
	  * @Description: TODO 拷贝Assets的数据库文件到应用数据库目录
	  * @param @param assetsSrc 数据库文件名
	  * @param @param des 应用数据库目录
	  * @return boolean    返回类型
	  * @throws
	  */
	private boolean copyAssetsToFilesystem(String assetsSrc, String des){  
			Log.d(TAG,  "Copy "+assetsSrc+" to "+des);  
	        InputStream istream = null;  
	        OutputStream ostream = null;  
	        try{  
	            AssetManager am = context.getAssets();  
	            istream = am.open(assetsSrc);  
	            ostream = new FileOutputStream(des);  
	            byte[] buffer = new byte[1024];  
	            int length;  
	            while ((length = istream.read(buffer))>0){  
	                ostream.write(buffer, 0, length);  
	            }  
	            istream.close();  
	            ostream.close();  
	        }  
	        catch(Exception e){  
	            e.printStackTrace();  
	            try{  
	                if(istream!=null)  
	                    istream.close();  
	                if(ostream!=null)  
	                    ostream.close();  
	            }  
	            catch(Exception ee){  
	                ee.printStackTrace();  
	            }  
	            return false;  
	        }  
	        return true;  
	    }  
	
	private String getDatabaseFilepath(){  
	    return String.format(databasepath, context.getApplicationInfo().packageName);  
	}  
	      
    private String getDatabaseFile(String dbfile){  
        return getDatabaseFilepath() + File.separator + dbfile;  
    }  
    
    /** 
     * Close assets database 
     * @param dbfile, the assets file which will be closed soon 
     * @return, the status of this operating 
     */  
    public boolean closeDatabase(String dbfile){  
        if(databases.get(dbfile) != null){  
            SQLiteDatabase db = (SQLiteDatabase) databases.get(dbfile);  
            db.close();  
            databases.remove(dbfile);  
            return true;  
        }  
        return false;  
    }  
      
    /** 
     * Close all assets database 
     */  
    static public void closeAllDatabase(){  
        if(instance != null){  
            for(int i=0; i<instance.databases.size(); ++i){  
                if(instance.databases.get(i)!=null){  
                	instance.databases.get(i).close();  
                }  
            }  
            instance.databases.clear();  
        }  
    }  
}
