package com.example.liangweiwu.downloadmanager.view.controller;

import android.support.annotation.NonNull;
import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinxin.li on 16/7/11.
 */

// totally copied from DefaultItemAnimator.java =_=

public class DeleteAnimator extends SimpleItemAnimator {

    private ArrayList<RecyclerView.ViewHolder> mPendingRemovals = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList<>();
    private ArrayList<MoveInfo> mPendingMoves = new ArrayList<>();
    private ArrayList<ChangeInfo> mPendingChanges = new ArrayList<>();

    private ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionList = new ArrayList<>();
    private ArrayList<ArrayList<MoveInfo>> mMoveList = new ArrayList<>();
    private ArrayList<ArrayList<ChangeInfo>> mChangeList = new ArrayList<>();

    private ArrayList<RecyclerView.ViewHolder> mAddAnimations = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mMoveAnimations = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mChangeAnimations = new ArrayList<>();


    private static class MoveInfo{
        public RecyclerView.ViewHolder holder;
        public int fromX,fromY,toX,toY;

        private MoveInfo(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }

    private static class ChangeInfo{
        public RecyclerView.ViewHolder oldHolder,newHolder;
        public int fromX,fromY,toX,toY;

        public ChangeInfo(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder) {
            this.oldHolder = oldHolder;
            this.newHolder = newHolder;
        }

        public ChangeInfo(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
            this.oldHolder = oldHolder;
            this.newHolder = newHolder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }

        @Override
        public String toString() {
            return "ChangeInfo{" +
                    "oldHolder=" + oldHolder +
                    ", newHolder=" + newHolder +
                    ", fromX=" + fromX +
                    ", fromY=" + fromY +
                    ", toX=" + toX +
                    ", toY=" + toY +
                    '}';
        }
    }


    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        resetAnimation(holder);
        mPendingRemovals.add(holder);
        return true;
    }

    private void animateRemoveImpl(final RecyclerView.ViewHolder holder){
        final View view = holder.itemView;
        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
        mRemoveAnimations.add(holder);
        animation.setDuration(getRemoveDuration())
                .translationX(-view.getWidth()).setListener(new VpaListenerAdapter(){
            @Override
            public void onAnimationStart(View view) {
                dispatchRemoveStarting(holder);
            }

            @Override
            public void onAnimationEnd(View view) {
                animation.setListener(null);
                ViewCompat.setTranslationX(view,0);
                dispatchRemoveFinished(holder);
                mRemoveAnimations.remove(holder);
                dispatchFinishWhenDown();
            }
        }).start();
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        resetAnimation(holder);
        ViewCompat.setAlpha(holder.itemView,0);
        mPendingAdditions.add(holder);
        return true;
    }

    private void animateAddImpl(final RecyclerView.ViewHolder holder){
        final View view = holder.itemView;
        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
        mAddAnimations.add(holder);
        animation.alpha(1).setDuration(getAddDuration())
                .setListener(new VpaListenerAdapter(){
                    @Override
                    public void onAnimationStart(View view) {
                        dispatchAddStarting(holder);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        animation.setListener(null);
                        dispatchAddFinished(holder);
                        mAddAnimations.remove(holder);
                        dispatchFinishWhenDown();
                    }
                    @Override
                    public void onAnimationCancel(View view) {
                        ViewCompat.setAlpha(view,1);
                    }
                }).start();
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        final View view = holder.itemView;
        fromX += ViewCompat.getTranslationX(holder.itemView);
        fromY += ViewCompat.getTranslationY(holder.itemView);
        resetAnimation(holder);
        int deltaX = toX - fromX;
        int deltaY = toY - fromY;
        if(deltaX == 0 && deltaY == 0){
            dispatchMoveFinished(holder);
            return false;
        }
        if(deltaX != 0){
            ViewCompat.setTranslationX(view,-deltaX);
        }
        if(deltaY != 0){
            ViewCompat.setTranslationY(view,-deltaY);
        }
        mPendingMoves.add(new MoveInfo(holder,fromX,fromY,toX,toY));
        return true;
    }

    private void animateMoveImpl(final RecyclerView.ViewHolder holder, int fromX,int fromY,int toX,int toY){
        final View view = holder.itemView;
        final int deltaX = toX - fromX;
        final int deltaY = toY - fromY;
        if(deltaX != 0){
            ViewCompat.animate(view).translationX(0);
        }
        if(deltaY != 0){
            ViewCompat.animate(view).translationY(0);
        }

        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
        mMoveAnimations.add(holder);
        animation.setDuration(getMoveDuration()).setListener(new VpaListenerAdapter(){
            @Override
            public void onAnimationStart(View view) {
                dispatchMoveStarting(holder);
            }

            @Override
            public void onAnimationEnd(View view) {
                animation.setListener(null);
                dispatchMoveFinished(holder);
                mMoveAnimations.remove(holder);
                dispatchFinishWhenDown();
            }

            @Override
            public void onAnimationCancel(View view) {
                if(deltaX != 0){
                    ViewCompat.setTranslationX(view,0);
                }
                if(deltaY != 0){
                    ViewCompat.setTranslationY(view,0);
                }
            }
        }).start();

    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        if(oldHolder == newHolder){
            return animateMove(oldHolder,fromLeft,fromTop,toLeft,toTop);
        }

        final float prevTranslationX = ViewCompat.getTranslationX(oldHolder.itemView);
        final float prevTranslationY = ViewCompat.getTranslationY(oldHolder.itemView);
        final float prevAlpha = ViewCompat.getAlpha(oldHolder.itemView);
        resetAnimation(oldHolder);
        int deltaX = (int)(toLeft - fromLeft - prevTranslationX);
        int deltaY = (int)(toTop - fromTop - prevTranslationY);

        ViewCompat.setTranslationX(oldHolder.itemView,prevTranslationX);
        ViewCompat.setTranslationY(oldHolder.itemView,prevTranslationY);
        ViewCompat.setAlpha(oldHolder.itemView,prevAlpha);

        if(newHolder != null){
            resetAnimation(newHolder);
            ViewCompat.setTranslationX(newHolder.itemView,-deltaX);
            ViewCompat.setTranslationY(newHolder.itemView,-deltaY);
            ViewCompat.setAlpha(newHolder.itemView,0);
        }
        mPendingChanges.add(new ChangeInfo(oldHolder,newHolder,fromLeft,fromTop,toLeft,toTop));
        return true;
    }

    private void animateChangeImpl(final ChangeInfo changeInfo){
        final RecyclerView.ViewHolder holder = changeInfo.oldHolder;
        final View view = holder == null? null : holder.itemView;
        final RecyclerView.ViewHolder newHolder = changeInfo.newHolder;
        final View newView = newHolder == null? null : newHolder.itemView;
        if(view != null){
            final ViewPropertyAnimatorCompat oldViewAnim = ViewCompat.animate(view)
                    .setDuration(getChangeDuration());
            mChangeAnimations.add(changeInfo.oldHolder);
            oldViewAnim.translationX(changeInfo.toX - changeInfo.fromX);
            oldViewAnim.translationY(changeInfo.toY - changeInfo.fromY);
            oldViewAnim.alpha(0).setListener(new VpaListenerAdapter(){
                @Override
                public void onAnimationStart(View view) {
                    dispatchChangeStarting(changeInfo.oldHolder,true);
                }

                @Override
                public void onAnimationEnd(View view) {
                    oldViewAnim.setListener(null);
                    ViewCompat.setAlpha(view,1);
                    ViewCompat.setTranslationX(view,0);
                    ViewCompat.setTranslationY(view,0);
                    dispatchChangeFinished(changeInfo.oldHolder,true);
                    mChangeAnimations.remove(changeInfo.oldHolder);
                    dispatchFinishWhenDown();
                }
            }).start();
        }

        if(newView != null){
            final ViewPropertyAnimatorCompat newViewAnimation = ViewCompat.animate(newView);
            mChangeAnimations.add(changeInfo.newHolder);
            newViewAnimation.translationX(0).translationY(0).setDuration(getChangeDuration())
                    .alpha(1).setListener(new VpaListenerAdapter(){
                @Override
                public void onAnimationStart(View view) {
                    dispatchChangeStarting(changeInfo.newHolder,false);
                }

                @Override
                public void onAnimationEnd(View view) {
                    newViewAnimation.setListener(null);
                    ViewCompat.setAlpha(newView,1);
                    ViewCompat.setTranslationX(newView,0);
                    ViewCompat.setTranslationY(newView,0);
                    dispatchChangeFinished(changeInfo.newHolder,false);
                    mChangeAnimations.remove(changeInfo.newHolder);
                    dispatchFinishWhenDown();
                }
            }).start();
        }
    }


    private void endChangeAnimation(List<ChangeInfo> infoList, RecyclerView.ViewHolder item){
        for(int i = infoList.size() - 1;i >= 0; i--){
            ChangeInfo changeInfo = infoList.get(i);
            if(endChangeAnimationIfNecessary(changeInfo,item)){
                if(changeInfo.oldHolder == null && changeInfo.newHolder == null){
                    infoList.remove(changeInfo);
                }
            }
        }
    }

    private void endChangeAnimationIfNecessary(ChangeInfo changeInfo){
        if(changeInfo.oldHolder != null){
            endChangeAnimationIfNecessary(changeInfo,changeInfo.oldHolder);
        }
        if(changeInfo.newHolder != null){
            endChangeAnimationIfNecessary(changeInfo,changeInfo.newHolder);
        }
    }

    private boolean endChangeAnimationIfNecessary(ChangeInfo changeInfo, RecyclerView.ViewHolder item){
        boolean oldItem = false;
        if(changeInfo.newHolder == item){
            changeInfo.newHolder = null;
        }else if(changeInfo.oldHolder == item){
            changeInfo.oldHolder = null;
            oldItem = true;
        }else{
            return false;
        }
        ViewCompat.setAlpha(item.itemView,1);
        ViewCompat.setTranslationX(item.itemView,0);
        ViewCompat.setTranslationY(item.itemView,0);
        dispatchChangeFinished(item,oldItem);
        return true;
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        final View view = item.itemView;
        ViewCompat.animate(view).cancel();
        for(int i=mPendingMoves.size() - 1;i >= 0;i--){
            MoveInfo moveInfo = mPendingMoves.get(i);
            if(moveInfo.holder == item){
                ViewCompat.setTranslationY(view,0);
                ViewCompat.setTranslationX(view,0);
                dispatchMoveFinished(item);
                mPendingMoves.remove(i);
            }
        }
        endChangeAnimation(mPendingChanges,item);
        if(mPendingRemovals.remove(item)){
            ViewCompat.setAlpha(view,1);
            dispatchRemoveFinished(item);
        }
        if(mPendingAdditions.remove(item)){
            ViewCompat.setAlpha(view,1);
            dispatchAddFinished(item);
        }

        for(int i = mChangeList.size() - 1;i >= 0;i--){
            ArrayList<ChangeInfo> changes = mChangeList.get(i);
            endChangeAnimation(changes,item);
            if(changes.isEmpty()){
                mChangeList.remove(i);
            }
        }

        for(int i = mMoveList.size() - 1;i >= 0;i--){
            ArrayList<MoveInfo> moves = mMoveList.get(i);
            for(int j = moves.size() - 1;j >= 0; j--){
                MoveInfo moveInfo = moves.get(j);
                if(moveInfo.holder == item){
                    ViewCompat.setTranslationX(view,0);
                    ViewCompat.setTranslationY(view,0);
                    dispatchMoveFinished(item);
                    moves.remove(j);
                    if(moves.isEmpty()){
                        mMoveList.remove(i);
                    }
                    break;
                }
            }
        }

        for(int i =mAdditionList.size() - 1;i >= 0;i--){
            ArrayList<RecyclerView.ViewHolder> additions = mAdditionList.get(i);
            if(additions.remove(item)){
                ViewCompat.setAlpha(view, 1);
                dispatchAddFinished(item);
                if(additions.isEmpty()){
                    mAdditionList.remove(i);
                }
            }
        }

        dispatchFinishWhenDown();
    }

    private void resetAnimation(RecyclerView.ViewHolder holder){
        AnimatorCompatHelper.clearInterpolator(holder.itemView);
        endAnimation(holder);
    }



    @Override
    public void endAnimations() {
        int count = mPendingMoves.size();
        for(int i =count - 1;i >= 0;i--){
            MoveInfo item = mPendingMoves.get(i);
            View view = item.holder.itemView;
            ViewCompat.setTranslationY(view,0);
            ViewCompat.setTranslationX(view,0);
            dispatchMoveFinished(item.holder);
            mPendingMoves.remove(i);
        }
        count = mPendingRemovals.size();
        for(int i = count - 1;i >= 0;i--){
            RecyclerView.ViewHolder item = mPendingRemovals.get(i);
            dispatchRemoveFinished(item);
            mPendingRemovals.remove(i);
        }
        count = mPendingAdditions.size();
        for(int i = count - 1;i >= 0;i--){
            RecyclerView.ViewHolder item = mPendingAdditions.get(i);
            View view = item.itemView;
            ViewCompat.setAlpha(view,1);
            dispatchAddFinished(item);
            mPendingAdditions.remove(i);
        }
        count = mPendingChanges.size();
        for(int i =count - 1;i >= 0;i--){
            endChangeAnimationIfNecessary(mPendingChanges.get(i));
        }
        mPendingChanges.clear();
        if(!isRunning()){
            return ;
        }
        int listCount = mMoveList.size();
        for(int i =listCount - 1;i >= 0;i--){
            ArrayList<MoveInfo> moves = mMoveList.get(i);
            count = moves.size();
            for(int j = count-1;j >=0;j--){
                MoveInfo moveInfo = moves.get(j);
                RecyclerView.ViewHolder item = moveInfo.holder;
                View view = item.itemView;
                ViewCompat.setTranslationX(view,0);
                ViewCompat.setTranslationY(view,0);
                dispatchMoveFinished(moveInfo.holder);
                moves.remove(j);
                if(moves.isEmpty()){
                    mMoveList.remove(moves);
                }
            }
        }
        listCount = mAdditionList.size();
        for(int i = listCount - 1;i >= 0; i--){
            ArrayList<RecyclerView.ViewHolder> additions = mAdditionList.get(i);
            count = additions.size();
            for(int j = count - 1;j >= 0;j--){
                RecyclerView.ViewHolder item = additions.get(j);
                View view = item.itemView;
                ViewCompat.setAlpha(view,1);
                dispatchAddFinished(item);
                additions.remove(j);
                if(additions.isEmpty()){
                    mAdditionList.remove(additions);
                }
            }
        }
        listCount = mChangeList.size();
        for(int i = listCount - 1;i >= 0;i--){
            ArrayList<ChangeInfo> changes = mChangeList.get(i);
            count = changes.size();
            for(int j = count - 1;j >= 0;j--){
                endChangeAnimationIfNecessary(changes.get(j));
                if(changes.isEmpty()){
                    mChangeList.remove(changes);
                }
            }
        }
        cancelAll(mRemoveAnimations);
        cancelAll(mMoveAnimations);
        cancelAll(mAddAnimations);
        cancelAll(mChangeAnimations);

        dispatchAnimationsFinished();

    }
    private void cancelAll(List<RecyclerView.ViewHolder> viewHolders){
        for(int i = viewHolders.size() - 1;i >= 0;i--){
            ViewCompat.animate(viewHolders.get(i).itemView).cancel();
        }
    }

    @Override
    public boolean isRunning() {
        return (!mPendingAdditions.isEmpty() ||
                !mPendingChanges.isEmpty() ||
                !mPendingMoves.isEmpty() ||
                !mPendingRemovals.isEmpty() ||
                !mMoveAnimations.isEmpty() ||
                !mRemoveAnimations.isEmpty() ||
                !mAddAnimations.isEmpty() ||
                !mChangeAnimations.isEmpty() ||
                !mMoveList.isEmpty() ||
                !mAdditionList.isEmpty() ||
                !mChangeList.isEmpty());
    }

    @Override
    public void runPendingAnimations() {
        boolean removalsPending = !mPendingRemovals.isEmpty();
        boolean movesPending = !mPendingMoves.isEmpty();
        boolean changesPending = !mPendingChanges.isEmpty();
        boolean additionsPending = !mPendingAdditions.isEmpty();

        if(!removalsPending && !movesPending && !additionsPending && !changesPending){
            return ;
        }

        for(RecyclerView.ViewHolder holder : mPendingRemovals){
            animateRemoveImpl(holder);
        }
        mPendingRemovals.clear();

        if(movesPending){
            final ArrayList<MoveInfo> moves = new ArrayList<>();
            moves.addAll(mPendingMoves);
            mPendingMoves.clear();
            Runnable mover = new Runnable() {
                @Override
                public void run() {
                    for(MoveInfo moveInfo : moves){
                        animateMoveImpl(moveInfo.holder,moveInfo.fromX,moveInfo.fromY,
                                moveInfo.toX,moveInfo.toY);
                    }
                    moves.clear();
                    mMoveList.remove(moves);
                }
            };
            if(removalsPending){
                View view = moves.get(0).holder.itemView;
                ViewCompat.postOnAnimationDelayed(view,mover,getRemoveDuration());
            }else{
                mover.run();
            }
        }

        if(changesPending){
            final ArrayList<ChangeInfo> changes = new ArrayList<>();
            changes.addAll(mPendingChanges);
            mChangeList.add(changes);
            mPendingChanges.clear();
            Runnable changer = new Runnable() {
                @Override
                public void run() {
                    for(ChangeInfo change : changes){
                        animateChangeImpl(change);
                    }
                    changes.clear();
                    mChangeList.remove(changes);
                }
            };
            if(removalsPending){
                RecyclerView.ViewHolder holder = changes.get(0).oldHolder;
                ViewCompat.postOnAnimationDelayed(holder.itemView,changer,getRemoveDuration());
            }else{
                changer.run();
            }
        }

        if(additionsPending){
            final ArrayList<RecyclerView.ViewHolder> additions = new ArrayList<>();
            additions.addAll(mPendingAdditions);
            mAdditionList.add(additions);
            mPendingAdditions.clear();
            Runnable adder = new Runnable() {
                @Override
                public void run() {
                    for(RecyclerView.ViewHolder holder : additions){
                        animateAddImpl(holder);
                    }
                    additions.clear();;
                    mAdditionList.remove(additions);
                }
            };
            if(removalsPending || movesPending || changesPending){
                long removeDuration = removalsPending ? getRemoveDuration() : 0;
                long moveDuration = movesPending ? getMoveDuration() : 0;
                long changeDuretion = changesPending ? getChangeDuration() : 0;
                long totalDelay = removeDuration + Math.max(moveDuration , changeDuretion);
                View view = additions.get(0).itemView;
                ViewCompat.postOnAnimationDelayed(view,adder,totalDelay);
            }else{
                adder.run();
            }
        }



    }

    @Override
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder,
                                             @NonNull List<Object> payloads) {
        return !payloads.isEmpty() || super.canReuseUpdatedViewHolder(viewHolder,payloads);
    }

    private static class VpaListenerAdapter implements ViewPropertyAnimatorListener{
        @Override
        public void onAnimationStart(View view) {}

        @Override
        public void onAnimationEnd(View view) {}

        @Override
        public void onAnimationCancel(View view) {}
    }

    private void dispatchFinishWhenDown(){
        if(!isRunning()){
            dispatchAnimationsFinished();
        }
    }

}