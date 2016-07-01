package com.example.liangweiwu.downloadmanager.Model;


import com.example.liangweiwu.downloadmanager.Utils.GameInformationUtils;

public class DownloadParam {
    private int ID = -1;
    private String url;
    private int beginOffset = -1;
    private int endOffset = -1;
    public DownloadParam(String url,int beginOffset,int endOffset){
        this.url = url;
        this.beginOffset = beginOffset;
        this.endOffset = endOffset;
    }
    public DownloadParam(int id,String url,int beginOffset,int endOffset){
        this.ID = id;
        this.url = url;
        this.beginOffset = beginOffset;
        this.endOffset = endOffset;
    }
    public String getUrl(){
        return url;
    }
    public int getBeginOffset(){
        return beginOffset;
    }
    public int getEndOffset(){
        return endOffset;
    }
    public String getFilename(){
        String[] s = url.split("/");
        if(s.length == 0){
            return "unnamed.apk";
        }
        return s[s.length-1];
    }
    public GameInformation getInformation(){
        return GameInformationUtils.getInstance().getGameInfoByID(ID);
    }
}
