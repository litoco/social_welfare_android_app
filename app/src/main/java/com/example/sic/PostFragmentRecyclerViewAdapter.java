package com.example.sic;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;

import java.io.IOException;
import java.util.ArrayList;

public class PostFragmentRecyclerViewAdapter extends RecyclerView.Adapter<PostFragmentRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Uri> fileUriArrayList;
    private Fragment fragment;
    private ArrayList<String> fileUrls;

    public PostFragmentRecyclerViewAdapter(Context context, ArrayList<Uri> fileUriArrayList, Fragment fragment, ArrayList<String> fileUrls) {
        this.context = context;
        this.fileUriArrayList = fileUriArrayList;
        this.fragment = fragment;
        this.fileUrls = fileUrls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.post_fragment_selected_item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        try {
            if(fragment instanceof PostFragment) {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), fileUriArrayList.get(position));
                holder.itemImageView.setImageBitmap(imageBitmap);
                holder.cancelImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fileUriArrayList.remove(position);
                        PostFragmentRecyclerViewAdapter.this.notifyDataSetChanged();
                        ((PostFragment)fragment).updateArrayLists(position);
                    }
                });
            }else{
                Glide.with(fragment.getContext()).load(fileUrls.get(position))
                        .override(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                        .placeholder(R.drawable.ic_broken_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(holder.itemImageView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(fragment instanceof PostFragment)
            return fileUriArrayList.size();
        else
            return  fileUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImageView, cancelImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.post_fragment_selected_item_image_view);
            cancelImageView = itemView.findViewById(R.id.post_fragment_selected_item_cancel_image_view);
            if(! (fragment instanceof PostFragment))
                cancelImageView.setVisibility(View.GONE);
        }
    }

}
