package com.example.sic;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class NewsFeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Post> al = new ArrayList<>();
    private NewsFeedRecyclerViewAdapter rv;
    private FireBaseHelperClass fireBaseHelperClass;
    private String fireBaseUserId="test_user_id";

    public NewsFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        recyclerView = v.findViewById(R.id.news_feed_recycler_view);
        fireBaseHelperClass = FireBaseHelperClass.getInstance();
        getDataFromFireBase("test_user_id");
        rv = new NewsFeedRecyclerViewAdapter(this, al);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(rv);
        return v;
    }

    private void getDataFromFireBase(String fireBaseUserId) {
        Log.e("===>","inside method");
        fireBaseHelperClass.getPostsFromFireBase(fireBaseUserId, this);
    }

    public void updatePostArrayList(ArrayList<Post> posts){
        Log.e("===>","inside method update post arraylist");
        al.clear();
        al.addAll(posts);
        Collections.reverse(al);
        rv.notifyDataSetChanged();
    }

    public String getFireBaseUserId() {
        return fireBaseUserId;
    }
}
