package com.example.liangweiwu.downloadmanager.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
/**
 *  Created by xinxin.li
 */
public class MyAccessibilityService extends AccessibilityService {
    public MyAccessibilityService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        onAccessibilityInstall(accessibilityEvent);
    }

    public void onAccessibilityInstall(AccessibilityEvent event){
        AccessibilityNodeInfo nodeInfo = event.getSource();
        if(nodeInfo != null){
            int eventType = event.getEventType();
            if(eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
                    eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
                iterateNodeAndHandle(nodeInfo);
            }
        }
    }

    private void iterateNodeAndHandle(AccessibilityNodeInfo nodeInfo){
        if(nodeInfo != null){
            if("android.widget.Button".equals(nodeInfo.getClassName())){
                String nodeContent = nodeInfo.getText().toString();
                String[] btnStrs = new String[]{"确定","完成","安装","OK","FINISH","INSTALL","NEXT","下一步"};
                for(String str:btnStrs){
                    if(nodeContent.equalsIgnoreCase(str)){
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }

            }else if("android.widget.ScrollView".equals(nodeInfo.getClassName())){
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
            int childCount = nodeInfo.getChildCount();
            for(int i=0;i<childCount;i++){
                AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
                iterateNodeAndHandle(childNodeInfo);
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
