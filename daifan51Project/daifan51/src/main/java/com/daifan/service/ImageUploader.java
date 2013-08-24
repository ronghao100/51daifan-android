package com.daifan.service;

import android.util.Log;
import com.daifan.Singleton;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.File;

public class ImageUploader {

    public static final String CON_START_FILEID = "FILEID:";

    public String upload(String filename) {

        Log.d(Singleton.DAIFAN_TAG, "start writing filename:" + filename + " to server");

        HttpClient httpClient = new DefaultHttpClient();
        HttpContext httpContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost("http://51daifan.sinaapp.com/recipes/add_image");

        try {
            MultipartEntity multipartContent = new MultipartEntity();
            multipartContent.addPart("Filedata", new FileBody(new File(filename)));
            long totalSize = multipartContent.getContentLength();

            // Send it
            httpPost.setEntity(multipartContent);
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == 200) {
                String con = EntityUtils.toString(response.getEntity());
                if (con.startsWith(CON_START_FILEID)) {
                    return con.substring(CON_START_FILEID.length());
                } else {
                    Log.e(Singleton.DAIFAN_TAG, "response incorrect:" + con);
                }
            } else {
                Log.d(Singleton.DAIFAN_TAG, "uploading failed for status line:" + statusLine.toString());
            }
        } catch (Exception e) {
            Log.d(Singleton.DAIFAN_TAG, "Uploading filename" + filename + " failed", e);
        }
        return null;
    }
}
