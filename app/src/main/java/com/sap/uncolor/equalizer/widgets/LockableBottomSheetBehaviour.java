package com.sap.uncolor.equalizer.widgets;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LockableBottomSheetBehaviour<V extends View> extends BottomSheetBehavior<V> {
    private boolean enable = true;

    /**
     * Default constructor for instantiating BottomSheetBehaviors.
     */
    public LockableBottomSheetBehaviour() {
        super();
    }

    /**
     * Default constructor for inflating BottomSheetBehaviors from layout.
     *
     * @param context The {@link Context}.
     * @param attrs   The {@link AttributeSet}.
     */
    public LockableBottomSheetBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEnable(boolean enable){
        this.enable = enable;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!enable && event.getAction() == MotionEvent.ACTION_MOVE){
            return false;
        }
        return super.onInterceptTouchEvent(parent, child, event);
    }
}