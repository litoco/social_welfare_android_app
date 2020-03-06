package com.example.sic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NewsFeedRecyclerViewAdapter extends RecyclerView.Adapter<NewsFeedRecyclerViewAdapter.ViewHolder> {
    private NewsFeedFragment newsFeedFragment;
    private ArrayList<Post> al;

    public NewsFeedRecyclerViewAdapter(NewsFeedFragment newsFeedFragment, ArrayList<Post> al) {
        this.newsFeedFragment = newsFeedFragment;
        this.al = al;
    }

    @NonNull
    @Override
    public NewsFeedRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(newsFeedFragment.getContext()).inflate(R.layout.layout_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsFeedRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.postOwner.setText(al.get(position).getName());
        holder.postCaption.setText(al.get(position).getCaptionAssociatedWithPost());
        PostFragmentRecyclerViewAdapter recyclerViewAdapter = new PostFragmentRecyclerViewAdapter(
                newsFeedFragment.getContext(), null,
                newsFeedFragment, al.get(position).getUrlOfMedia());
        holder.postMedia.setLayoutManager(new LinearLayoutManager(newsFeedFragment.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        holder.postMedia.setAdapter(recyclerViewAdapter);
        holder.postNumberOfClicks.setText(String.valueOf(al.get(position).getNumberOfThreeHeartReactions()!=0?al.get(position).getNumberOfThreeHeartReactions():""));
        holder.postNumberOfClicks2.setText(String.valueOf(al.get(position).getNumberOfTwoHeartReactions()!=0?al.get(position).getNumberOfTwoHeartReactions():""));
        holder.postNumberOfClicks3.setText(String.valueOf(al.get(position).getNumberOfOneHeartReactions()!=0?al.get(position).getNumberOfOneHeartReactions():""));
//        Log.e("===>","Value is :"+al.get(position).getCurrentUserReactionToThisPost());
        if(al.get(position).getCurrentUserReactionToThisPost()==3){
            holder.updateReactionHeart(R.drawable.ic_love, R.drawable.ic_love, R.drawable.ic_love,
                    R.drawable.ic_love_empty, R.drawable.ic_love_empty,
                    R.drawable.ic_love_empty);
        }else if (al.get(position).getCurrentUserReactionToThisPost()==2){
            holder.updateReactionHeart(R.drawable.ic_love_empty, R.drawable.ic_love_empty, R.drawable.ic_love_empty,
                    R.drawable.ic_love, R.drawable.ic_love,
                    R.drawable.ic_love_empty);
        } else if (al.get(position).getCurrentUserReactionToThisPost()==1){
            holder.updateReactionHeart(R.drawable.ic_love_empty, R.drawable.ic_love_empty, R.drawable.ic_love_empty,
                    R.drawable.ic_love_empty, R.drawable.ic_love_empty,
                    R.drawable.ic_love);
        }
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView postCaption, postOwner, postNumberOfClicks, postNumberOfClicks2, postNumberOfClicks3;
        private RecyclerView postMedia;
        private RelativeLayout postReactionContainer1,postReactionContainer2,postReactionContainer3;
        private ImageView prc1h1,prc1h2,prc1h3,prc2h1,prc2h2,prc3h1;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postOwner = itemView.findViewById(R.id.post_owner_name);
            postCaption = itemView.findViewById(R.id.post_caption);
            postMedia = itemView.findViewById(R.id.post_media);
            postReactionContainer1 = itemView.findViewById(R.id.post_reaction_container_1);
            postReactionContainer2 = itemView.findViewById(R.id.post_reaction_container_2);
            postReactionContainer3 = itemView.findViewById(R.id.post_reaction_container_3);
            prc1h1 = itemView.findViewById(R.id.post_heart_1);
            prc1h2 = itemView.findViewById(R.id.post_heart_2);
            prc1h3 = itemView.findViewById(R.id.post_heart_3);
            prc2h1 = itemView.findViewById(R.id.post_heart_1_2);
            prc2h2 = itemView.findViewById(R.id.post_heart_2_2);
            prc3h1 = itemView.findViewById(R.id.post_heart_1_3);
            postNumberOfClicks = itemView.findViewById(R.id.post_num_of_clicks);
            postNumberOfClicks2 = itemView.findViewById(R.id.post_num_of_clicks_2);
            postNumberOfClicks3 = itemView.findViewById(R.id.post_num_of_clicks_3);
            postReactionContainer1.setOnClickListener(this);
            postReactionContainer2.setOnClickListener(this);
            postReactionContainer3.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int count;
            int[] reactionCounts={0,0,0};
            reactionCounts[0]=al.get(this.getAdapterPosition()).getNumberOfThreeHeartReactions();
            reactionCounts[1]=al.get(this.getAdapterPosition()).getNumberOfTwoHeartReactions();
            reactionCounts[2]=al.get(this.getAdapterPosition()).getNumberOfOneHeartReactions();
            int prevLike=al.get(this.getAdapterPosition()).getCurrentUserReactionToThisPost();
            if(v==postReactionContainer1){
                updateReactionHeart(R.drawable.ic_love, R.drawable.ic_love, R.drawable.ic_love,
                                    R.drawable.ic_love_empty, R.drawable.ic_love_empty,
                                    R.drawable.ic_love_empty);
                count=3;
            }else if(v==postReactionContainer2){
                updateReactionHeart(R.drawable.ic_love_empty, R.drawable.ic_love_empty, R.drawable.ic_love_empty,
                                    R.drawable.ic_love, R.drawable.ic_love,
                                    R.drawable.ic_love_empty);
                count=2;
            }else{
                updateReactionHeart(R.drawable.ic_love_empty, R.drawable.ic_love_empty, R.drawable.ic_love_empty,
                            R.drawable.ic_love_empty, R.drawable.ic_love_empty,
                            R.drawable.ic_love);
                count=1;
            }
//
//            Log.e("===>","count: "+count);
//            Log.e("===>","1 heart count: "+reactionCounts[2]);
//            Log.e("===>","2 heart count: "+reactionCounts[1]);
//            Log.e("===>","3 heart count: "+reactionCounts[0]);
            if(prevLike>=0 && prevLike!=count){
                if(prevLike>0) {
                    reactionCounts[3 - prevLike] -= 1;
//                    Log.e("===>","prev heart count: "+reactionCounts[3-prevLike]);
                }
                reactionCounts[3-count]+=1;
//                Log.e("===>","curr heart count: "+reactionCounts[3-count]);
                al.get(this.getAdapterPosition()).setCurrentUserReactionToThisPost(count);
            }
//            Log.e("===>","1 heart count: "+reactionCounts[2]);
//            Log.e("===>","2 heart count: "+reactionCounts[1]);
//            Log.e("===>","3 heart count: "+reactionCounts[0]);
            al.get(this.getAdapterPosition()).setNumberOfThreeHeartReactions(reactionCounts[0]);
            al.get(this.getAdapterPosition()).setNumberOfTwoHeartReactions(reactionCounts[1]);
            al.get(this.getAdapterPosition()).setNumberOfOneHeartReactions(reactionCounts[2]);
            this.postNumberOfClicks.setText(String.valueOf((reactionCounts[0])==0?"":(reactionCounts[0])));
            this.postNumberOfClicks2.setText(String.valueOf((reactionCounts[1])==0?"":(reactionCounts[1])));
            this.postNumberOfClicks3.setText(String.valueOf((reactionCounts[2])==0?"":(reactionCounts[2])));
            FireBaseHelperClass.getInstance().addReactionToPost(newsFeedFragment.getFireBaseUserId(), count, al.get(this.getAdapterPosition()).getPostPath(), reactionCounts);
        }

        private void updateReactionHeart(int ic_love, int ic_love1, int ic_love2, int ic_love_empty, int ic_love_empty1, int ic_love_empty2) {
            prc1h1.setImageDrawable(newsFeedFragment.getContext().getDrawable(ic_love));
            prc1h2.setImageDrawable(newsFeedFragment.getContext().getDrawable(ic_love1));
            prc1h3.setImageDrawable(newsFeedFragment.getContext().getDrawable(ic_love2));
            prc2h1.setImageDrawable(newsFeedFragment.getContext().getDrawable(ic_love_empty));
            prc2h2.setImageDrawable(newsFeedFragment.getContext().getDrawable(ic_love_empty1));
            prc3h1.setImageDrawable(newsFeedFragment.getContext().getDrawable(ic_love_empty2));
        }
    }
}
