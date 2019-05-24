package com.project.studenthub.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.project.studenthub.Models.Image;
import com.project.studenthub.Models.Post;
import com.project.studenthub.R;

import java.util.Arrays;
import java.util.List;

public class PostAdapter extends BaseAdapter {

    private final static String TAG = PostAdapter.class.getSimpleName();
    private Context context;
    private List<Post> images;
    private static LayoutInflater inflater = null;
    private final RequestOptions options = new RequestOptions()
            .override(600, 200)
            .fitCenter()
            .priority(Priority.HIGH);

    public PostAdapter(Context context, List<Post> images){
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Post getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post currentImage = getItem(position);
        try{
            final ImageHolder holder;
            if (convertView == null){
                holder = new ImageHolder();
                inflater = LayoutInflater.from(this.context);
                convertView = inflater.inflate(R.layout.post_item, parent, false);
                holder.image = convertView.findViewById(R.id.pictureIV);
                holder.description = convertView.findViewById(R.id.pictureDescriptionTV);
                holder.postOwner = convertView.findViewById(R.id.postOwnerTV);
                convertView.setTag(holder);
            }
            else {
                holder = (ImageHolder) convertView.getTag();
            }

            Glide.with(context).load(Uri.parse(currentImage.getPictureUri())).apply(options).into(holder.image);
            holder.description.setText(currentImage.getDescription());
            holder.postOwner.setText("Posted by " + currentImage.getUserDisplayName());

        }
        catch (Exception ex){
            Log.e(TAG, "Eroarea : " + Arrays.toString(ex.getStackTrace()));
        }
        return convertView;
    }

    private class ImageHolder {
        ImageView image;
        TextView description;
        TextView postOwner;
    }
}
