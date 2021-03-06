package com.example.liangweiwu.downloadmanager.view.event;

import com.example.liangweiwu.downloadmanager.model.DownloadTaskController;

import de.greenrobot.event.EventBus;

/**
 * Created by Nol on 2016/7/11.
 */
public class MainUiEvent {
    public static final int EVENT_EMPTY = 0;
    public static final int EVENT_URL_VALID = 1;
    public static final int EVENT_URL_INVALID = 2;
    public static final int EVENT_TASK_START = 3;
    public static final int EVENT_TASK_UPDATE = 4;
    public static final int EVENT_TASK_UPDATE_FLOAT_ICON = 5;
    public static final int EVENT_TASK_DELETE = 6;
    public static final int EVENT_LAUNCH = 7;
    public static final int EVENT_DATA_LOAD_FINISH = 8;

    public static final int UPDATE_POSITION_UNDEFINED = -1;

    public int what = EVENT_EMPTY;
    public Object obj = null;
    public int arg1 = 0;
    public int arg2 = 0;

    public MainUiEvent(){
    }
    public MainUiEvent(int what){
        this.what = what;
    }
    public MainUiEvent(int what,Object obj){
        this.what = what;
        this.obj = obj;
    }
    public MainUiEvent(int what,Object obj,int arg1,int arg2){
        this.what = what;
        this.obj = obj;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }
    public static MainUiEvent createUrlCheckEvent(int what,Object url,int fileSize){
        MainUiEvent event = new MainUiEvent(what,url,fileSize,0);
        return event;
    }

    /**
     * what:    flag
     * obj:     controller
     * arg1:    id
     * arg2:    position
     */
    public static void postDownloadItemAdapterEvent(int what, DownloadTaskController controller,int position){
        MainUiEvent event;
        if(controller == null){
            event = new MainUiEvent(what);
            event.arg2 = position;
        }else{
            int id = controller.getInfo().getID();
            event = new MainUiEvent(what,controller,id,position);
        }
        EventBus.getDefault().post(event);
    }
    public static void postLaunchEvent(){
        MainUiEvent event = new MainUiEvent(EVENT_LAUNCH);
        EventBus.getDefault().post(event);
    }
}
