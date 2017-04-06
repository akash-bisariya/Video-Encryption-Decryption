package com.example.akash.videoencrptionexa;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by akash on 31/5/16.
 */
public class VideoRecyclerAdapter extends RecyclerView.Adapter<VideoRecyclerAdapter.CustomViewHolder> {
    ArrayList<HashMap<String, String>> listofVideos;
    Context context;
    VideoDialogFragment dialogReference;
    public VideoRecyclerAdapter(ArrayList<HashMap<String, String>> arrayList, VideoDialogFragment context) {
        listofVideos=new ArrayList<>();
        listofVideos=arrayList;
        this.context=context.getActivity();
        dialogReference =context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_recycler_items,null);
        final CustomViewHolder viewHolder =new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.textView.setText(listofVideos.get(position).get("displayName"));

    }

    @Override
    public int getItemCount() {
        return (null!=listofVideos? listofVideos.size():0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        protected ImageView imageView;
        protected TextView textView;
        public CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView=(ImageView)itemView.findViewById(R.id.ivVideoImage);
            textView=(TextView)itemView.findViewById(R.id.tvVideoName);

        }


        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.putExtra("data",listofVideos.get(getAdapterPosition()).get("data"));
            intent.putExtra("displayName",listofVideos.get(getAdapterPosition()).get("displayName"));
            //dialogReference.getTargetFragment().onActivityResult(dialogReference.getTargetRequestCode(),100,intent);
            VideoDialogFragment.DialogFragmentToActivity dialogFragmentToActivity =(VideoDialogFragment.DialogFragmentToActivity)context;
            dialogFragmentToActivity.onReturningResult(intent);
            dialogReference.dismiss();

        }
    }
}
