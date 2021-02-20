package com.changhong.nativeplayer;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class CHLM_SDListView extends ListView {

    public CHLM_SDListView(Context context) {
        super(context);
    }

    public CHLM_SDListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CHLM_SDListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        int lastSelectItem = getSelectedItemPosition();
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            //setSelection(lastSelectItem);
            // save index and top position
            int index1 = getFirstVisiblePosition();
            int index2 = lastSelectItem - index1;
            int top = 0;
            View v = null;

            if (index2 < 0)
                index2 = 0;
            v = getChildAt(index2);
            top = (v == null) ? 0 : v.getTop();

            // restore
            setSelectionFromTop(lastSelectItem, top);
        }
    }

}
