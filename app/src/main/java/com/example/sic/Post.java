package com.example.sic;

import java.util.ArrayList;

public class Post {
    private String name;
    private String captionAssociatedWithPost;
    private ArrayList<String> urlOfMedia;
    private int currentUserReactionToThisPost;
    private int numberOfThreeHeartReactions;
    private int numberOfTwoHeartReactions;
    private int numberOfOneHeartReactions;
    private String postPath;

    public Post(String name,String captionAssociatedWithPost, ArrayList<String> urlOfMedia,
                int currentUserReactionToThisPost, int numberOfThreeHeartReactions,
                int numberOfTwoHeartReactions, int numberOfOneHeartReactions, String postPath) {
        this.name=name;
        this.captionAssociatedWithPost = captionAssociatedWithPost;
        this.urlOfMedia = urlOfMedia;
        this.currentUserReactionToThisPost = currentUserReactionToThisPost;
        this.numberOfThreeHeartReactions = numberOfThreeHeartReactions;
        this.numberOfTwoHeartReactions = numberOfTwoHeartReactions;
        this.numberOfOneHeartReactions = numberOfOneHeartReactions;
        this.postPath = postPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaptionAssociatedWithPost() {
        return captionAssociatedWithPost;
    }

    public void setCaptionAssociatedWithPost(String captionAssociatedWithPost) {
        this.captionAssociatedWithPost = captionAssociatedWithPost;
    }

    public ArrayList<String> getUrlOfMedia() {
        return urlOfMedia;
    }

    public void setUrlOfMedia(ArrayList<String> urlOfMedia) {
        this.urlOfMedia = urlOfMedia;
    }

    public int getCurrentUserReactionToThisPost() {
        return currentUserReactionToThisPost;
    }

    public void setCurrentUserReactionToThisPost(int currentUserReactionToThisPost) {
        this.currentUserReactionToThisPost = currentUserReactionToThisPost;
    }

    public int getNumberOfThreeHeartReactions() {
        return numberOfThreeHeartReactions;
    }

    public void setNumberOfThreeHeartReactions(int numberOfThreeHeartReactions) {
        this.numberOfThreeHeartReactions = numberOfThreeHeartReactions;
    }

    public int getNumberOfTwoHeartReactions() {
        return numberOfTwoHeartReactions;
    }

    public void setNumberOfTwoHeartReactions(int numberOfTwoHeartReactions) {
        this.numberOfTwoHeartReactions = numberOfTwoHeartReactions;
    }

    public int getNumberOfOneHeartReactions() {
        return numberOfOneHeartReactions;
    }

    public void setNumberOfOneHeartReactions(int numberOfOneHeartReactions) {
        this.numberOfOneHeartReactions = numberOfOneHeartReactions;
    }

    public String getPostPath() {
        return postPath;
    }

    public void setPostPath(String postPath) {
        this.postPath = postPath;
    }
}
