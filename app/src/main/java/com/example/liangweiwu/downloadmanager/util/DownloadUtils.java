package com.example.liangweiwu.downloadmanager.util;



import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;


public class DownloadUtils {
    private static DownloadUtils mDownloadUtils;
    private Context mContext;

    private DownloadUtils(Context mContext){
        this.mContext = mContext;
    }
    public static void init(Context context){
        if(mDownloadUtils == null){
            mDownloadUtils = new DownloadUtils(context.getApplicationContext());
        }
    }
    public static DownloadUtils getInstance(){
        return mDownloadUtils;
    }










    public void download(String uri){
        DownloadManager manager = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri));
        /*
        addRequestHeader(String header,String value):添加网络下载请求的http头信息
        allowScanningByMediaScanner():用于设置是否允许本MediaScanner扫描。
        setAllowedNetworkTypes(int flags):设置用于下载时的网络类型，默认任何网络都可以下载，提供的网络常量有：NETWORK_BLUETOOTH、NETWORK_MOBILE、NETWORK_WIFI。
        setAllowedOverRoaming(Boolean allowed):用于设置漫游状态下是否可以下载
        setNotificationVisibility(int visibility):用于设置下载时时候在状态栏显示通知信息
        setTitle(CharSequence):设置Notification的title信息
        setDescription(CharSequence):设置Notification的message信息
        setDestinationInExternalFilesDir、setDestinationInExternalPublicDir、setDestinationUri
        等方法用于设置下载文件的存放路径，注意如果将下载文件存放在默认路径，那么在空间不足的情况下系统会将文件删除，所以使用上述方法设置文件存放目录是十分必要的。
        */
        manager.enqueue(request);
    }
    private void queryDownTask(DownloadManager downManager,int status) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(status);
        Cursor cursor= downManager.query(query);

        while(cursor.moveToNext()){
            String downId= cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
            String address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            //String statuss = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            String size= cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            String sizeTotal = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

        }
        cursor.close();
    }
}

