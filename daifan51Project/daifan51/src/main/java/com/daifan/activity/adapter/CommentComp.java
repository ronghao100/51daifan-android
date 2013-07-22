package com.daifan.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.daifan.R;
import com.daifan.Singleton;
import com.daifan.domain.Comment;
import com.daifan.domain.Post;

public class CommentComp {

    private RelativeLayout commentCont;
    private EditText commentTxt;
    private InputMethodManager imm;

    private PostAdapter postAdapter;
    private Post post;

    public CommentComp() {
    }

    public void onActive(final Activity ac) {
        View v = ac.findViewById(R.id.post_comment_container);
        if ( !(v instanceof  RelativeLayout)) {
            Log.e(Singleton.DAIFAN_TAG, "post_comment_container isn't instance of RelativeLayout.");
            return;
        }

        commentCont = (RelativeLayout) v;
        commentTxt = (EditText) ac.findViewById(R.id.post_comment_txt);
        imm = (InputMethodManager) ac.getSystemService(Context.INPUT_METHOD_SERVICE);

        commentTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentTxt.setHint("");
            }
        });

        commentTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                commentTxt.setHint(ac.getString(R.string.prompt_post_comment));
            }
        });

        final Button postCommentBtn = (Button) ac.findViewById(R.id.post_comment_btn);

        this.changePostCommentBtnState(postCommentBtn);

        postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.getTrimmedLength(commentTxt.getText()) == 0)
                    return;

                String comment = commentTxt.getText().toString().trim();
                String currUid = Singleton.getInstance().getCurrUid();
                int cUid = Integer.parseInt(currUid);
                Singleton.getInstance().getPostService().postComment(post, cUid, comment);
                if (postAdapter != null)
                    postAdapter.notifyDataSetChanged();

                commentCont.setVisibility(View.INVISIBLE);
                commentTxt.setText("");

                imm.hideSoftInputFromWindow(
                        ac.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        commentTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                changePostCommentBtnState(postCommentBtn);
            }
        });
    }

    private void changePostCommentBtnState(Button postCommentBtn) {
        if (TextUtils.getTrimmedLength(commentTxt.getText()) > 0)
            postCommentBtn.setEnabled(true);
        else
            postCommentBtn.setEnabled(false);
    }

    public void showForPost(Post post, PostAdapter postAdapter) {
        commentCont.setVisibility(View.VISIBLE);
        commentTxt.requestFocus();
        String myComment = post.myComment(Singleton.getInstance().getCurrUid());
        commentTxt.setText(myComment);
        commentTxt.setSelection(myComment.length());
        imm.showSoftInput(commentTxt, InputMethodManager.SHOW_IMPLICIT);
        this.post = post;
        this.postAdapter = postAdapter;
        Log.d(Singleton.DAIFAN_TAG, "commentCont measuredWidth=" + commentCont.getMeasuredWidth() +", measuredHeight="
            + commentCont.getMeasuredHeight() + ", width=" + commentCont.getWidth() + ", height=" + commentCont.getHeight());

        Log.d(Singleton.DAIFAN_TAG, "commentCont position left=" + commentCont.getLeft() +", top="
                + commentCont.getTop());
    }
}
