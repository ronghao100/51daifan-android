package com.daifan.service;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @SuppressWarnings("ConstantConditions")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void swithLoadingView(final boolean on, final View loadingView, final View normalView, int animTime) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            loadingView.setVisibility(View.VISIBLE);
            loadingView.animate()
                    .setDuration(animTime)
                    .alpha(on ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            loadingView.setVisibility(on ? View.VISIBLE : View.GONE);
                        }
                    });

            normalView.setVisibility(View.VISIBLE);
            normalView.animate()
                    .setDuration(animTime)
                    .alpha(on ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            normalView.setVisibility(on ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            loadingView.setVisibility(on ? View.VISIBLE : View.GONE);
            normalView.setVisibility(on ? View.GONE : View.VISIBLE);
        }
    }
}
