package com.example.liangweiwu.downloadmanager.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.liangweiwu.downloadmanager.model.DownloadParam;
import com.example.liangweiwu.downloadmanager.model.GameInformation;

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
            sb.append("url VARCHAR(200),");
            sb.append("package VARCHAR(100),");
            sb.append("versionCode VARCHAR(100),");
            sb.append("versionName VARCHAR(100),");
            sb.append("size VARCHAR(20),");
            sb.append("category VARCHAR(20),");
            sb.append("detail VARCHAR(200),");
            sb.append("status numeric(0,1),");
            sb.append("thread_number INTEGER DEFAULT 1 )");
            db.execSQL(sb.toString());

            sb = new StringBuilder();
            sb.append("CREATE TABLE ThreadDetail(");
            sb.append("ID INTEGER REFERENCES DownloadManager,");
            sb.append("thread_id INTEGER,");
            sb.append("thread_status numeric(0,1),");
            sb.append("thread_blockSize VARCHAR(50),");
            sb.append("thread_startOffset VARCHAR(50),");
            sb.append("PRIMARY KEY(ID,thread_id) )");
            System.out.println(sb.toString());
            db.execSQL(sb.toString());
        }catch (Exception e){
            e.printStackTrace();
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
                String[] column_filed = {"ID","name","url","package","versionCode",
                        "versionName","size","category","detail","status","thread_number"};
                for(String filed:column_filed){
                    if(filed.equals("ID") || filed.equals("status") || filed.equals("thread_number")){
                        info.setAttribute(filed,cursor.getInt(cursor.getColumnIndex(filed)));
                    }else{
                        info.setAttribute(filed,cursor.getString(cursor.getColumnIndex(filed)));
                    }
                }
                //info.debug();
                list.put(info.getID(),info);
            }
            Log.i("Query Size",String.valueOf(cursor.getCount()));
        }catch (SQLException e){
            e.printStackTrace();
            Log.e("Query",e.getMessage());
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
                String[] column_filed = {"ID","name","url","package","versionCode",
                        "versionName","size","category","detail","status","thread_number"};
                for(String filed:column_filed){
                    if(filed.equals("ID") || filed.equals("status") || filed.equals("thread_number")){
                        info.setAttribute(filed,cursor.getInt(cursor.getColumnIndex(filed)));
                    }else{
                        info.setAttribute(filed,cursor.getString(cursor.getColumnIndex(filed)));
                    }
                }
                //info.debug();
            }
        }catch (SQLException e){
            e.printStackTrace();
            Log.e("Query",e.getMessage());
        }
        cursor.close();
        db.close();
        return info;
    }
    public void insert(GameInformation info){
        if(info == null){
            return;
        }
        if(checkExistence(info.getID())){
            update(info);
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            String sql = "insert into DownloadManager(ID,name,url,package,versionCode,versionName,size,category,detail,status,thread_number) values(?,?,?,?,?,?,?,?,?,?,?)";
            db.execSQL(sql,new Object[]{
                    info.getID(),
                    info.getName(),
                    info.getAttribution("url"),
                    info.getAttribution("package"),
                    info.getAttribution("versionCode"),
                    info.getAttribution("versionName"),
                    info.getAttribution("size"),
                    info.getAttribution("category"),
                    info.getAttribution("detail"),
                    info.getAttribution("status"),
                    info.getAttribution("thread_number")
            });
        }catch (SQLException e){
            e.printStackTrace();
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
        if(info == null){
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            String sql = "update DownloadManager set name=?,url=?,package=?," +
                    "versionCode=?,versionName=?,size=?,category=?," +
                    "detail=?,status=?,thread_number=? where ID=?";
            db.execSQL(sql,new Object[]{
                    info.getName(),
                    info.getAttribution("url"),
                    info.getAttribution("package"),
                    info.getAttribution("versionCode"),
                    info.getAttribution("versionName"),
                    info.getAttribution("size"),
                    info.getAttribution("category"),
                    info.getAttribution("detail"),
                    info.getAttribution("status"),
                    info.getAttribution("thread_number"),
                    info.getID()
            });
        }catch (SQLException e){
            e.printStackTrace();
            Log.e("update",e.getMessage());
        }
        db.close();
    }
    public void delete(int id){
        System.out.println("delete info");
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            String sql = "delete from DownloadManager where ID = ?";
            db.execSQL(sql,new Object[]{id});
        }catch (SQLException e){
            e.printStackTrace();
            Log.e("delete",e.getMessage());
        }
        db.close();
    }
    public boolean checkExistence(int id){
        boolean isExist = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from DownloadManager where ID = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(id)});
        try {
            if(cursor.moveToFirst()){
                isExist = true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        cursor.close();
        db.close();
        return isExist;
    }

    public void insertParam(){

    }
    public DownloadParam[] query_param(int id){
        return null;
    }
    public HashMap<Integer,DownloadParam[]> query_param(){
        HashMap<Integer,DownloadParam[] > map = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from ThreadDetail";
        Cursor cursor = db.rawQuery(sql,new String[]{});
        // String[] column_filed = {"ID","thread_id","thread_status","thread_blockSize","thread_startOffset"};
        try{
            while(cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex("ID"));
                int thread_id = cursor.getInt(cursor.getColumnIndex("thread_id"));
                int status = cursor.getInt(cursor.getColumnIndex("thread_status"));
                int blockSize = cursor.getInt(cursor.getColumnIndex("thread_blockSize"));
                int downloadedSize = cursor.getInt(cursor.getColumnIndex("thread_startOffset"));
                DownloadParam param = new DownloadParam(id,thread_id,status,blockSize,downloadedSize);
                if(map.get(id) == null){
                    GameInformation info = query(id);
                    if(info == null){
                        delete_param(id);
                    }else{
                        int thread_num = (Integer)info.getAttribution("thread_number");
                        DownloadParam[] params = new DownloadParam[thread_num];
                        params[thread_id] = param;
                        map.put(id,params);
                    }
                }else{
                    map.get(id)[thread_id] = param;
                }
                //param.debug();
            }
        }catch (SQLException e){
            e.printStackTrace();
            Log.e("query params",e.getMessage());
        }
        cursor.close();
        db.close();
        return map;
    }
    private void insert_param(DownloadParam param){
        if(param == null){
            return;
        }
        if(checkParamExistence(param.getID(),param.getThread_id())){
            update_param(param);
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        try {
            String sql = "insert into ThreadDetail(ID,thread_id,thread_status,thread_blockSize,thread_startOffset) values(?,?,?,?,?)";
            db.execSQL(sql,new Object[]{
                    param.getID(),
                    param.getThread_id(),
                    param.getThread_status(),
                    param.getThread_blockSize(),
                    param.getThread_downloadedLength()
            });
        }catch (SQLException e){
            e.printStackTrace();
            Log.e("insert param",e.getMessage());
        }
        db.close();
    }
    public void insert_params(DownloadParam[] params){
        if(params == null){
            return;
        }
        for(DownloadParam param : params){
            insert_param(param);
        }
    }
    public void insert_params(Collection<DownloadParam[]> list){
        if(list == null){
            return;
        }
        for(DownloadParam[] params : list){
            insert_params(params);
        }
    }
    public void update_param(DownloadParam param){
        if(param == null){
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        try {
            String sql = "update ThreadDetail set thread_status = ?,thread_blockSize = ?," +
                    "thread_startOffset = ? where ID = ? and thread_id = ?";
            db.execSQL(sql,new Object[]{
                    param.getThread_status(),
                    param.getThread_blockSize(),
                    param.getThread_downloadedLength(),
                    param.getID(),
                    param.getThread_id()
            });
        }catch (SQLException e){
            e.printStackTrace();
            Log.e("update param",e.getMessage());
        }
        db.close();
    }
    public void delete_param(int id){
        SQLiteDatabase db = getWritableDatabase();
        try {
            String sql = "delete from ThreadDetail where ID = ?";
            db.execSQL(sql,new Object[]{id});
        }catch (SQLException e){
            e.printStackTrace();
            Log.e("delete param",e.getMessage());
        }
        db.close();
    }
    public void delete_all(){
        SQLiteDatabase db = getWritableDatabase();
        try {
            String sql = "delete from ThreadDetail";
            db.execSQL(sql,new Object[]{});
            String sql1 = "delete from DownloadManager";
            db.execSQL(sql1,new Object[]{});
        }catch (SQLException e){
            e.printStackTrace();
            Log.e("delete",e.getMessage());
        }
        db.close();
    }
    private boolean checkParamExistence(int id,int thread_id){
        boolean isExist = false;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from ThreadDetail where ID = ? and thread_id = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(id),String.valueOf(thread_id)});
        try {
            if(cursor.moveToFirst()){
                isExist = true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        cursor.close();
        db.close();
        return isExist;
    }
}
