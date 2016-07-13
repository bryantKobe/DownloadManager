package com.example.liangweiwu.downloadmanager.model;

import android.util.Log;

import com.example.liangweiwu.downloadmanager.view.controller.ViewController;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by liangwei.wu on 16/7/13.
 */

public class IndexedArrayList<T> extends ArrayList<T>{
    public IndexedArrayList(){
        super();
    }
    @Override
    public boolean add(T e){
        if(e instanceof ViewController){
            ((ViewController)e).setPosition(size());
        }
        return super.add(e);
    }
    @Override
    public void add(int position,T e){
        super.add(position,e);
        if(!(e instanceof ViewController)){
            return;
        }
        for(T t :this){
            ViewController temp = ((ViewController)t);
            int pos = temp.getPosition();
            if(pos >= position){
                temp.setPosition(pos+1);
            }
        }
        ((ViewController) e).setPosition(position);
    }
    public void removeByID(int id){
        Iterator<T> it = iterator();
        int delPos = -1;
        while(it.hasNext()){
            T controller = it.next();
            if(controller instanceof ViewController){
                ViewController temp = ((ViewController)controller);
                int infoID = temp.getInfoID();
                if(infoID == id){
                    delPos = temp.getPosition();
                    it.remove();
                    break;
                }
            }
        }
        if(delPos != -1){
            for(T e : this){
                ViewController temp = ((ViewController) e);
                int pos = temp.getPosition();
                if(pos > delPos){
                    temp.setPosition(pos-1);
                }
            }
        }
    }
    public void swap(int src,int tar){
        T e1 = get(src);
        T e2 = get(tar);
        if(e1 instanceof ViewController && e2 instanceof ViewController){
            ((ViewController) e1).setPosition(tar);
            ((ViewController) e2).setPosition(src);
        }
        set(src,e2);
        set(tar,e1);
    }
    public void debug(){
        for(T e : this){
            if(e instanceof ViewController){
                Log.d("list pos",""+((ViewController) e).getPosition());
            }
        }
    }
}
