package com.example.akash.videoencrptionexa;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by akash on 31/5/16.
 */
public class VideoDialogFragment extends DialogFragment {
    @InjectView(R.id.rvVideoDialogFragment)
    RecyclerView rvVideoDialogFragment;

    static VideoDialogFragment newInstance(ArrayList<HashMap<String, String>> arrayList) {
        VideoDialogFragment f = new VideoDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable("videoList", arrayList);
        f.setArguments(args);

        return f;
    }
    public interface DialogFragmentToActivity
    {
        public void onReturningResult(Intent data);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.video_dialog_fragment,container,false);
        ButterKnife.inject(this,v);
        getDialog().setTitle("Select Video to Encrypt");
        rvVideoDialogFragment.setLayoutManager(new LinearLayoutManager(getActivity()));
        VideoRecyclerAdapter videoRecyclerAdapter = new VideoRecyclerAdapter((ArrayList<HashMap<String, String>>)getArguments().getSerializable("videoList"),VideoDialogFragment.this);
        rvVideoDialogFragment.setAdapter(videoRecyclerAdapter);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
