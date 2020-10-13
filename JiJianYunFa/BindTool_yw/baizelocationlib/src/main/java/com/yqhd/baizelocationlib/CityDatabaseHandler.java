package com.yqhd.baizelocationlib;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lzkj.baize_android.utils.LogUtils;
import com.yqhd.baizelocationlib.entity.CityBo;
import com.yqhd.baizelocationlib.entity.ProvinceBo;

import java.util.ArrayList;
import java.util.List;


public class CityDatabaseHandler {
	public static final String CHINA_PROVINC_CITY_DB_NAME = "china_province_city_zone.db";
	
	public static final String TABLE_NAME_PROVINCE = "tbl_province";
	
	public static final String TABLE_NAME_CITY = "tbl_city";
	public static final String COLUMN_PROVINCE_NAME = "province_name";
	public static final String COLUMN_PROVINCE_ID = "province_id";
	public static final String COLUMN_PROVINCE_REMARK = "pro_remark";
	public static final String COLUMN_CITY_NAME = "city_name";
	public static final String COLUMN_CITY_ID = "city_id";
	private AssetsDatabaseManager assetsDatabaseManager;
	
	public CityDatabaseHandler(Context context){
		 assetsDatabaseManager = AssetsDatabaseManager.getInstance(context);
	}
	/** 
     * 获取省份集合 
     */  
    public List<ProvinceBo> getProSet(){
    	List<ProvinceBo> proSet = new ArrayList<ProvinceBo>();
    	if(assetsDatabaseManager != null){
    		//打开数据库   
    		SQLiteDatabase db = assetsDatabaseManager.getDatabase(CHINA_PROVINC_CITY_DB_NAME);
    		if(null != db){
				LogUtils.e(LogUtils.getStackTraceElement(),"not null database");
    			Cursor cursor=db.query(TABLE_NAME_PROVINCE, null, null, null, null, null, null);  
    			while(cursor.moveToNext()){  
    				String proName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROVINCE_NAME));  
    				Integer proId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROVINCE_ID));
    				String proRemark = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROVINCE_REMARK));
    				proSet.add(new ProvinceBo(proName, proId, proRemark));  
    			}  
    			cursor.close();  
    		}else{
				LogUtils.e(LogUtils.getStackTraceElement(),"null database");
			}
    	}
        return proSet;
    } 
    
    /**
     * @Title: findCitysByProvinceId
     * @Description: TODO 根据省份ID获取城市集合
     * @param  proId 省份ID
     * @return List<String> 城市集合
     * @throws
     */
   public List<CityBo> findCitysByProvinceId(int proId){
      List<CityBo> citySet = new ArrayList<CityBo>();
      if(assetsDatabaseManager != null){
    	  //打开数据库   
    	  SQLiteDatabase db = assetsDatabaseManager.getDatabase(CHINA_PROVINC_CITY_DB_NAME);
    	  if(null != db){
    		  Cursor cursor=db.query(TABLE_NAME_CITY, null, COLUMN_PROVINCE_ID +"="+proId, null, null, null, null);  
    		  while(cursor.moveToNext()){  
    			  String cityName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY_NAME)); 
    			  Integer cityId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CITY_ID)); 
    			  citySet.add(new CityBo(cityName, proId, cityId));  
    		  }  
    		  cursor.close(); 
    	  }
  	  }
       return citySet;
   }  
   
   /**
	  * @Title: findProvinceByCity
	  * @Description: TODO 通过城市名称找出省份
	  * @param  cityId   城市id
	  * @return String  省份
	  * @throws
	  */
	public ProvinceBo findProvinceByCityId(Integer cityId){
		ProvinceBo provinceBo = null;
		if(null != cityId){
			if(assetsDatabaseManager != null){
				//找出该城市的省份
				SQLiteDatabase db = assetsDatabaseManager.getDatabase(CHINA_PROVINC_CITY_DB_NAME);
				if(null != db){
					String sql = "SELECT * FROM " + TABLE_NAME_PROVINCE + " p "
							+ "WHERE p." + COLUMN_PROVINCE_ID + " = "
							+ "(SELECT c." + COLUMN_PROVINCE_ID + " FROM " + TABLE_NAME_CITY + " c "
							+ "WHERE c." + COLUMN_CITY_ID + " = ?)";
					Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(cityId)});
					while(cursor.moveToNext()){  
						String proName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROVINCE_NAME));  
						Integer proId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROVINCE_ID));
						String proRemark = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROVINCE_REMARK));
						provinceBo = new ProvinceBo(proName, proId, proRemark);  
					}  
					cursor.close(); 
				}
		  	}
		}
		return provinceBo;
	}
	
	/**
	  * @Title: findCityByCityName
	  * @Description: TODO 根据城市名称查询城市
	  * @param  cityName 城市名称
	  * @return CityBo 城市对象
	  * @throws
	  */
	public CityBo findCityByCityName(String cityName){
		CityBo cityBo = null;
		if(null!= cityName && !"".equals(cityName)){
			if(assetsDatabaseManager != null){
				//找出该城市的省份
				SQLiteDatabase db = assetsDatabaseManager.getDatabase(CHINA_PROVINC_CITY_DB_NAME);
				if(null != db){
					String sql = "SELECT * FROM " + TABLE_NAME_CITY + " c "
							+ "WHERE c." + COLUMN_CITY_NAME + " LIKE ?";
					Cursor cursor = db.rawQuery(sql, new String[]{"%"+cityName+"%"});
					while(cursor.moveToNext()){  
						Integer cityId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CITY_ID)); 
						Integer provinceID =  cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROVINCE_ID)); 
						cityName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY_NAME));
						cityBo = new CityBo(cityName, provinceID, cityId);  
					}  
					cursor.close(); 
				}
		  	}
		}
		return cityBo;
	}

	public CityBo findCityByCityId(Integer cityId){
		CityBo cityBo = null;
		if(null!= cityId){
			if(assetsDatabaseManager != null){
				//找出该城市的省份
				SQLiteDatabase db = assetsDatabaseManager.getDatabase(CHINA_PROVINC_CITY_DB_NAME);
				if(null != db){
					String sql = "SELECT * FROM " + TABLE_NAME_CITY + " c "
							+ "WHERE c." + COLUMN_CITY_ID + " = ?";
					Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(cityId)});
					while(cursor.moveToNext()){
						Integer provinceID =  cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROVINCE_ID));
						String cityName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY_NAME));
						cityBo = new CityBo(cityName, provinceID, cityId);
					}
					cursor.close();
				}
			}
		}
		return cityBo;
	}
}
