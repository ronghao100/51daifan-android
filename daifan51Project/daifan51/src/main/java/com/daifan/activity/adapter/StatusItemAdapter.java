package com.daifan.activity.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.androidquery.AQuery;
import com.daifan.R;
import com.daifan.domain.Status;
import com.daifan.util.DateTimeUtils;
import com.daifan.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ronghao on 13-7-21.
 * feeds item list
 */
public class StatusItemAdapter extends BaseAdapter {
    private Context mContext;
    private List<Status> mStatuses;
    private final ImageLoader mImageLoader;

    public StatusItemAdapter(Context context, ImageLoader imageLoader) {
        mContext = context;
        mStatuses = new ArrayList<Status>();
        mImageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return mStatuses.size();
    }

    @Override
    public Object getItem(int position) {
        return mStatuses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView createDate;
        TextView statusType;
        TextView conmentOrText;
        NetworkImageView thumbnailPic;
        ImageView tweetAddressPic;
        TextView address;
        ImageView tweetBookPic;
        TextView tweetBook;
        ImageView tweetCommentPic;
        TextView tweetComment;

        TextView retweetedText;
        View subLayout;
        NetworkImageView tweetUploadPic2;
        TextView address2;
        ImageView tweetBookPic2;
        TextView tweetBook2;
        ImageView tweetCommentPic2;
        TextView tweetComment2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Status status = null;
        ViewHolder holder;

        //Log.d("getView:" + position);

        if (position < mStatuses.size()) {
            status = mStatuses.get(position);
        }

        convertView = Util.inflateView(R.layout.list_item_status, mContext);
        holder = new ViewHolder();
        holder.createDate = (TextView) convertView.findViewById(R.id.tvItemDate);
        holder.statusType = (TextView) convertView.findViewById(R.id.tvItemName);
        holder.conmentOrText = (TextView) convertView.findViewById(R.id.tvItemContent);
        holder.thumbnailPic = (NetworkImageView) convertView.findViewById(R.id.tweet_upload_pic1);

        holder.address = (TextView) convertView.findViewById(R.id.tweet_form);
        holder.tweetAddressPic = (ImageView) convertView.findViewById(R.id.tweet_address_pic);
        holder.tweetBookPic = (ImageView) convertView.findViewById(R.id.tweet_redirect_pic);
        holder.tweetBook = (TextView) convertView.findViewById(R.id.tweet_redirect);
        holder.tweetCommentPic = (ImageView) convertView.findViewById(R.id.tweet_comment_pic);
        holder.tweetComment = (TextView) convertView.findViewById(R.id.tweet_comment);

        holder.subLayout = convertView.findViewById(R.id.subLayout);
        holder.retweetedText = (TextView) convertView.findViewById(R.id.tvItemSubContent);
        holder.tweetUploadPic2 = (NetworkImageView) convertView.findViewById(R.id.tweet_upload_pic2);

        holder.address2 = (TextView) convertView.findViewById(R.id.tweet_form2);
        holder.tweetBookPic2 = (ImageView) convertView.findViewById(R.id.tweet_redirect_pic2);
        holder.tweetBook2 = (TextView) convertView.findViewById(R.id.tweet_redirect2);
        holder.tweetCommentPic2 = (ImageView) convertView.findViewById(R.id.tweet_comment_pic2);
        holder.tweetComment2 = (TextView) convertView.findViewById(R.id.tweet_comment2);

        AQuery aq = new AQuery(convertView);

        //Enable hardware acceleration if the device has API 11 or above
        aq.hardwareAccelerated11();

        holder.statusType.setText(status.type == Status.FEED_TYPE_CREATE ?
                R.string.feed_type_create : R.string.feed_type_book);

        String time = "";

        Date date = Util.parseDate(status.createdAt);
        if (date != null) {
            time = DateTimeUtils.getInstance(mContext).getMonthAndDay(date);
        }

        holder.createDate.setText(time);
        holder.conmentOrText.setText(status.type == Status.FEED_TYPE_CREATE ? status.text : status.comment,
                TextView.BufferType.SPANNABLE);

        if (status.type == Status.FEED_TYPE_CREATE) {
            holder.address.setText(status.address);
            holder.address.setVisibility(View.VISIBLE);
            holder.tweetAddressPic.setVisibility(View.VISIBLE);

            if (status.bookedCount > 0) {
                aq.id(R.id.tweet_redirect_pic).visible();
                holder.tweetBookPic.setVisibility(View.VISIBLE);
                holder.tweetBook.setText(String.valueOf(status.bookedCount));
                holder.tweetBook.setVisibility(View.VISIBLE);
            }

            if (status.commentCount > 0) {
                holder.tweetCommentPic.setVisibility(View.VISIBLE);
                holder.tweetComment.setText(String.valueOf(status.commentCount));
                holder.tweetComment.setVisibility(View.VISIBLE);
            }
        } else {
            holder.address2.setText(status.address);
            holder.address2.setVisibility(View.VISIBLE);

            if (status.bookedCount > 0) {
                aq.id(R.id.tweet_redirect_pic2).visible();
                holder.tweetBookPic2.setVisibility(View.VISIBLE);
                holder.tweetBook2.setText(String.valueOf(status.bookedCount));
                holder.tweetBook2.setVisibility(View.VISIBLE);
            }

            if (status.commentCount > 0) {
                holder.tweetCommentPic2.setVisibility(View.VISIBLE);
                holder.tweetComment2.setText(String.valueOf(status.commentCount));
                holder.tweetComment2.setVisibility(View.VISIBLE);
            }
        }

        if (status.type == Status.FEED_TYPE_CREATE && status.thumbnailPic != null) {
//            final String middleImageUrl = status.bmiddle_pic;
//            final String originalPicUrl = status.original_pic;

            holder.thumbnailPic.setImageUrl(status.thumbnailPic, mImageLoader);
            aq.id(holder.thumbnailPic).visible();


//            aq.id(holder.thumbnailPic).clicked(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Consts.ACTION_SHOW_IMAGE_VIWVER);
//                    intent.putExtra(Consts.MIDDLE_IMAGE_URL_KEY, middleImageUrl);
//                    intent.putExtra(Consts.ORIGINAL_PIC_URL_KEY, originalPicUrl);
//
//                    mContext.startActivity(intent);
//                }
//
//            });
        }


        if (status.type == Status.FEED_TYPE_BOOK) {
            holder.subLayout.setVisibility(View.VISIBLE);

            String text = "@" + status.userName + ":";
            text += status.text;

            holder.retweetedText.setText(text, TextView.BufferType.SPANNABLE);

            if (status.thumbnailPic != null) {
                holder.tweetUploadPic2.setImageUrl(status.thumbnailPic, mImageLoader);
                aq.id(holder.tweetUploadPic2).visible();
            }
        }

        return convertView;
    }

    public void addStatuses(Status status) {
        mStatuses.add(status);
    }

    public void addNewestStatuses(List<Status> statuses) {
        mStatuses.addAll(0, statuses);
    }

}
