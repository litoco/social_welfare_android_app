package com.example.sic;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireBaseHelperClass {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private PostFragment postFragment;
    private static FireBaseHelperClass instance = new FireBaseHelperClass();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static FireBaseHelperClass getInstance(){
        return instance;
    }

    private FireBaseHelperClass(){}

    public void addFilesToFireBase(final ArrayList<Uri> fileUriArrayList,
                                   final ArrayList<String> fileNameArrayList,
                                   final FirebaseUser firebaseUser, final Context context,
                                   PostFragment postFragment){
        this.postFragment=postFragment;
        final NotificationClass notificationClass = new NotificationClass();
        String storagePath = "users_file/";
        storagePath+=firebaseUser.getEmail();
        final ArrayList<Uri> fileUri=new ArrayList<>();
        final int maxCount=fileNameArrayList.size();
        final int[] currCount = {0};
        notificationClass.build(context, maxCount, currCount[0]+"/"+maxCount,"posting...");
        if(HelperConstantClass.APP_TYPE.equals("test")){
            for(int i=0;i<fileUriArrayList.size();i++){
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpg")
                        .build();
                String pathForFile=storagePath;
                pathForFile+=fileNameArrayList.get(i);
                UploadTask uploadTask = storage.getReference(pathForFile).putFile(fileUriArrayList.get(i), metadata);
                final String finalStoragePath = pathForFile;
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return storage.getReference(finalStoragePath).getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
//                            Log.e("====>","got url for==>"+fileNameArrayList.get(finalI)+": "+downloadUri);
                            fileUri.add(downloadUri);
                            if(fileUriArrayList.size()==fileUri.size())
                                addPostToFireBase(fileUri, firebaseUser, context);
                        } else {
//                            Log.e("====>","url task failed for==>"+fileUriArrayList.get(finalI));
                        }
                    }
                });

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        int val=(int)(100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        //val may be needed in future
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot snapshot) {
//                        Log.e("====>","successfully uploaded");
                        currCount[0]++;
                        notificationClass.updateNotificationBar(maxCount, currCount[0]);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Posting failed!!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    public void addPostToFireBase(ArrayList<Uri> uploadedFileUri, FirebaseUser firebaseUser, final Context context){
        final String userId = firebaseUser.getEmail();
        String postNum="post";
        SharedPreferences sp = context.getSharedPreferences("SiC", Context.MODE_PRIVATE);
        int postNumber = sp.getInt("postNumber",0);
        postNum+=String.valueOf(postNumber);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("postNumber", ++postNumber);
        editor.apply();
        final String postCaption = postFragment.getPostCaption();
        ArrayList<String> al = new ArrayList<>();
        for(Uri u : uploadedFileUri)
            al.add(u.toString());
        final Map<String, Object> hm = new HashMap<>();
        hm.put("name", HelperConstantClass.USER_NAME);
        hm.put("caption", postCaption);
        hm.put("fileUrls", al);
        hm.put("3star",0);
        hm.put("2star",0);
        hm.put("1star",0);
        hm.put("postReactors",new HashMap<String, Object>());
        hm.put("path", userId+"/"+postNum);
        final Map<String, Object> publicHM = new HashMap<>();
        publicHM.put(userId+"/"+postNum,1);
        final String finalPostNum = postNum;
        db.collection("root").document("public_post")
                .set(publicHM, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db.collection("root").document("users_post").collection(userId).document(finalPostNum)
                        .set(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        postFragment.showConfirmationMessage();
                        Toast.makeText(context, "Post shared successfully", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        postFragment.showConfirmationMessage();
                        Toast.makeText(context, "Post share unsuccessful", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                postFragment.showConfirmationMessage();
                Toast.makeText(context, "Post share unsuccessful", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getPostsFromFireBase(final String fireBaseUserId, final NewsFeedFragment newsFeedFragment){
//        Log.e("===>","Inside firebaseHelperclass");
        final ArrayList<Post> publicPostArrayList = new ArrayList<>();
        db.collection("root").document("public_post")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final Map<String, Object> map = documentSnapshot.getData();
                if(map!=null && map.size()>0) {
                    for (final Map.Entry<String, Object> entry : map.entrySet()) {
//                        Log.e("===>","path is:"+entry.getKey());
                        db.collection("root").document("users_post/"+entry.getKey())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Map<String, Object> newMap = documentSnapshot.getData();
                                        if(newMap!=null && newMap.size()>0){
                                            Post post = new Post((String)newMap.get("name"),
                                                    (String)newMap.get("caption"),
                                                    (ArrayList<String>)newMap.get("fileUrls"),
                                                    0,
                                                    ((Long)newMap.get("3star")).intValue(),
                                                    ((Long)newMap.get("2star")).intValue(),
                                                    ((Long)newMap.get("1star")).intValue(),
                                                    (String)newMap.get("path"));
                                            Map<String, Object> postReactorsMap = (Map<String, Object>) newMap.get("postReactors");
                                            if(postReactorsMap!=null && postReactorsMap.containsKey(fireBaseUserId)){
//                                                Log.e("===>","not able to retrieve data of:");
                                                post.setCurrentUserReactionToThisPost(((Long)postReactorsMap.get(fireBaseUserId)).intValue());
                                            }
                                            publicPostArrayList.add(post);

//                                            Log.e("===>","map:"+map.size()+" post:"+publicPostArrayList.size());
                                            if(map.size()==publicPostArrayList.size())
                                                newsFeedFragment.updatePostArrayList(publicPostArrayList);
                                        }else{
//                                            Toast.makeText(newsFeedFragment.getContext(), "Nothing to show", Toast.LENGTH_SHORT).show();
//                                            Log.e("===>","not able to retrieve data of:"+entry.getKey());
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                        db.collection("root").document("users_post/"+entry.getKey()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                            }
                        });
                    }
                }else{
                    Toast.makeText(newsFeedFragment.getContext(), "Nothing to show", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public List<Post> getUsersPostFromFireBase(){
        return null;
    }

    public void addReactionToPost(String fireBaseUserId, int i, String postPath, int[] preVal) {
        HashMap<String, Object> hm = new HashMap<>();
        hm.put(fireBaseUserId, i);
        HashMap<String, Object> newHM = new HashMap<>();
        newHM.put("postReactors", hm);
        newHM.put("1star",preVal[2]);
        newHM.put("2star",preVal[1]);
        newHM.put("3star",preVal[0]);
        db.collection("root").document("users_post/"+postPath).set(newHM, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Log.e("====>","Yes");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Log.e("====>",":( failed to add like");
            }
        });
    }
}
