package com.example.nishagupta.gcstickers.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.bumptech.glide.Glide;
import com.example.nishagupta.gcstickers.R;

import java.util.ArrayList;

import butterknife.BindView;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by nishagupta on 03/10/18.
 */

public class Adapter_Image extends RecyclerView.Adapter<Adapter_Image.MyViewHolder> {

    private ArrayList<String> mImagesList;
    private Context mContext;

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private boolean isLoadingAdded = false;

    public Adapter_Image(Context context, ArrayList<String> imageList) {
        mContext = context;
        mImagesList = new ArrayList<String>();
        this.mImagesList = imageList;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setmImagesList(ArrayList<String>imagesList)
    {
       this.mImagesList = imagesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        MyViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new MyViewHolder(v2,1);
                break;
        }
        return viewHolder;

        /*View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gif_animation_image, parent, false);

        return new MyViewHolder(itemView);*/
    }

    @NonNull
    private MyViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        MyViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.gif_animation_image, parent, false);
        viewHolder = new MyViewHolder(v1,0);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        switch (getItemViewType(position))
        {
            case ITEM:
                String imageUrl = mImagesList.get(position);
                if(imageUrl.length() > 0)
                {
                    Glide.with(mContext)
                            .load("https://media.giphy.com/media/" + imageUrl + "/giphy.gif").asGif()//("file://" + imageUrl)
                            .centerCrop()
                            .override(800, 500)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(holder.imageView);
                }
                break;
            case LOADING:
                break;

        }


    }

    @Override
    public int getItemCount() {
        return mImagesList==null ?0 : mImagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mImagesList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(String mc) {
        mImagesList.add(mc);
        notifyItemInserted(mImagesList.size() - 1);
    }

    public void addAll(ArrayList<String> mcList) {
        for (String mc : mcList) {
            add(mc);
        }
    }

    public void remove(String city) {
        int position = mImagesList.indexOf(city);
        if (position > -1) {
            mImagesList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new String());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mImagesList.size() - 1;
        String item = getItem(position);

        if (item != null) {
            mImagesList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public String getItem(int position) {
        return mImagesList.get(position);
    }


    /*
    View Holders
    _________________________________________________________________________________________________
     */

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public GifImageView imageView;
        public ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            imageView = (GifImageView) view.findViewById(R.id.gifImageView);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }

        public MyViewHolder(View view, int type) {
            super(view);

            switch (type)
            {
                case ITEM:
                    imageView = (GifImageView) view.findViewById(R.id.gifImageView);
                    break;
                case LOADING:
                    progressBar = (ProgressBar)view.findViewById(R.id.loadmore_progress);
                    break;
            }

        }
    }



}

