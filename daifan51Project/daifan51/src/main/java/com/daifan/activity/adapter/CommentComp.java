package com.daifan.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

    private final RelativeLayout commentCont;
    private final EditText commentTxt;
    private final InputMethodManager imm;

    private PostAdapter postAdapter;
    private Post post;

    public CommentComp(final Activity ac) {
        commentCont = (RelativeLayout) ac.findViewById(R.id.post_comment_container);
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

                //appendComment(commentContainers, cm);
                //commentContainers.setVisibility(View.VISIBLE);

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
                if (TextUtils.getTrimmedLength(commentTxt.getText()) > 0)
                    postCommentBtn.setAlpha(1.0f);
                else
                    postCommentBtn.setAlpha(0.5f);
            }
        });
    }

    public void showForPost(Post post, PostAdapter postAdapter) {
        commentCont.setVisibility(View.VISIBLE);
        commentTxt.requestFocus();
        imm.showSoftInput(commentTxt, InputMethodManager.SHOW_IMPLICIT);
        this.post = post;
        this.postAdapter = postAdapter;
    }
}