package com.daifan.activity.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

/**
* Created by liuzhr on 7/16/13.
*/
public class CommentEditText extends EditText {
    public CommentEditText(Context context) {
        super(context);
    }

    public CommentEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP) {
            this.setVisibility(View.INVISIBLE);
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
}
