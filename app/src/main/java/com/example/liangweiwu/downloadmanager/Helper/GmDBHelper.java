package com.example.liangweiwu.downloadmanager.Helper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.liangweiwu.downloadmanager.Model.GameInformation;

import java.util.Collection;
import java.util.HashMap;


public class GmDBHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "dm.db";
    public static int DATABASE_VERSION = 1;
    private Context mContext;
    private GmDBHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        mContext = context;
    }
    public static GmDBHelper getGmDBhelper(Context context){
        return new GmDBHelper(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        try{
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE DownloadManager(");
            sb.append("ID INTEGER PRIMARY KEY AUTOINCREMENT,");
            sb.append("name VARCHAR(50),");
            sb.append("icon VARCHAR(50),");
            sb.append("url VARCHAR(200),");
            sb.append("package VARCHAR(100),");
            sb.append("versionCode VARCHAR(100),");
            sb.append("versionName VARCHAR(100),");
            sb.append("size VARCHAR(20),");
            sb.append("category VARCHAR(20),");
            sb.append("detail VARCHAR(200),");
            sb.append("status numeric(0,1))");
            System.out.println(sb.toString());
            db.execSQL(sb.toString());
        }catch (Exception e){
            Log.e("create database",e.getMessage());
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

    }
    public HashMap<Integer,GameInformation> query(){
        HashMap<Integer,GameInformation> list = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from DownloadManager";
        Cursor cursor = db.rawQuery(sql,new String[]{});
        try{
            while(cursor.moveToNext()){
                GameInformation info = new GameInformation();
                String[] column_filed = {"ID","name","icon","url","package","versionCode",
                        "versionName","size","category","detail","status"};
                for(String filed:column_filed){
                    if(filed.equals("ID")||filed.equals("status")){
                        info.setAttribute(filed,cursor.getInt(cursor.getColumnIndex(filed)));
                    }else{
                        info.setAttribute(filed,cursor.getString(cursor.getColumnIndex(filed)));
                    }
                }
                info.debug();
                list.put(info.getID(),info);
            }
            Log.i("query game number",String.valueOf(cursor.getCount()));
        }catch (SQLException e){
            Log.e("query",e.getMessage());
        }
        cursor.close();
        db.close();
        return list;
    }
    public GameInformation query(int id){
        GameInformation info = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from DownloadManager where ID = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(id)});
        try{
            if(cursor.moveToFirst()){
                info = new GameInformation();
                String[] column_filed = {"ID","name","icon","url","package","versionCode",
                        "versionName","size","category","detail","status"};
                for(String filed:column_filed){
                    if(filed.equals("ID") || filed.equals("status")){
                        info.setAttribute(filed,cursor.getInt(cursor.getColumnIndex(filed)));
                    }else{
                        info.setAttribute(filed,cursor.getString(cursor.getColumnIndex(filed)));
                    }
                }
                info.debug();
            }
            Log.i("query game number",String.valueOf(cursor.getCount()));
        }catch (SQLException e){
            Log.e("query",e.getMessage());
        }
        cursor.close();
        db.close();
        return info;
    }
    public void insert(GameInformation info){
        SQLiteDatabase db = this.getWritableDatabase();
        if(info == null){
            return;
        }
        try{
            String sql = "insert into DownloadManager(ID,name,icon,url,package,versionCode,versionName,size,category,detail,status) values(?,?,?,?,?,?,?,?,?,?,?)";
            db.execSQL(sql,new Object[]{
                    info.getID(),
                    info.getName(),
                    info.getIcon(),
                    info.getAttribution("url"),
                    info.getAttribution("package"),
                    info.getAttribution("versionCode"),
                    info.getAttribution("versionName"),
                    info.getAttribution("size"),
                    info.getAttribution("category"),
                    info.getAttribution("detail"),
                    info.getAttribution("status")
            });
        }catch (SQLException e){
            Log.e("insert",e.getMessage());
        }
        db.close();
    }
    public void insert(Collection<GameInformation> list){
        if(list == null){
            return;
        }
        for(GameInformation info : list){
            insert(info);
        }
    }
    public void update(GameInformation info){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            String sql = "update DownloadManager set name=?,icon=?,url=?,package=?," +
                    "versionCode=?,versionName=?,size=?,category=?," +
                    "detail=?,status=? where ID=?";
            db.execSQL(sql,new Object[]{
                    info.getName(),
                    info.getIcon(),
                    info.getAttribution("url"),
                    info.getAttribution("package"),
                    info.getAttribution("versionCode"),
                    info.getAttribution("versionName"),
                    info.getAttribution("size"),
                    info.getAttribution("category"),
                    info.getAttribution("detail"),
                    info.getAttribution("status"),
                    info.getID()
            });
        }catch (SQLException e){
            Log.e("update",e.getMessage());
        }
        db.close();
    }
    public void delete(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            String sql = "delete from DownloadManager where ID = ?";
            db.execSQL(sql,new Object[]{id});
        }catch (SQLException e){
            Log.e("delete",e.getMessage());
        }
        db.close();
    }
}
