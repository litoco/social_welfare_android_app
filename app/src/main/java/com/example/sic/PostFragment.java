package com.example.sic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class PostFragment extends Fragment implements View.OnClickListener {

    private EditText caption;
    private Button addFiles, shareButton;
    private RecyclerView filePreviewImage;
    private FireBaseHelperClass fireBaseHelperClass;
    private static final int PICK_IMAGE=2, REQUESTING_PERMISSION=3;
    private ArrayList<Uri> uriArrayList=new ArrayList<>();
    private ArrayList<String> fileNameArrayList = new ArrayList<>();
    private PostFragmentRecyclerViewAdapter recyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post, container, false);
        caption = v.findViewById(R.id.post_caption);
        addFiles = v.findViewById(R.id.post_add_files_button);
        shareButton = v.findViewById(R.id.post_share_post);
        filePreviewImage = v.findViewById(R.id.post_file_preview_recycler_view);
        fireBaseHelperClass = FireBaseHelperClass.getInstance();
        shareButton.setOnClickListener(this);
        addFiles.setOnClickListener(this);
        recyclerViewAdapter = new PostFragmentRecyclerViewAdapter(this.getContext(), uriArrayList, this, null);
        filePreviewImage.setLayoutManager(new LinearLayoutManager(this.getContext(),LinearLayoutManager.HORIZONTAL, false));
        filePreviewImage.setAdapter(recyclerViewAdapter);
        return v;
    }

    @Override
    public void onClick(View v) {
        if(v==addFiles){
            getImagesFromGallery();
        }else if(v==shareButton){
            if(!caption.getText().toString().trim().equals("")||uriArrayList.size()>0)
                prepareToSharePost();
            else
                Toast.makeText(this.getContext(), "Empty post cannot be shared!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void prepareToSharePost() {
        Toast.makeText(this.getContext(), "Posting...", Toast.LENGTH_SHORT).show();
        addFilesToFireBase(uriArrayList, fileNameArrayList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUESTING_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImagesFromGallery();

                } else {
                    checkRequestedPermission();
                }
                return;
            }
        }
    }

    private void getImagesFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && null!=data) {
            Uri imageUri;
            if(data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for(int i = 0; i < count; i++) {
                    imageUri = data.getClipData().getItemAt(i).getUri();
                    DocumentFile df = DocumentFile.fromSingleUri(this.getContext(),imageUri);
                    df.getName();
//                    Log.e("====>","filename: "+df.getName()+" extension:"+df.getName().substring(df.getName().lastIndexOf(".")));
                    if(df.getName().substring(df.getName().lastIndexOf(".")).equals(".jpg")
                    ||df.getName().substring(df.getName().lastIndexOf(".")).equals(".png")
                    ||df.getName().substring(df.getName().lastIndexOf(".")).equals(".gif")) {
                        fileNameArrayList.add(df.getName());
                        uriArrayList.add(imageUri);
//                        Log.e("===>","added");
                    }else{
                        Toast.makeText(this.getContext(), "File not an image", Toast.LENGTH_SHORT).show();
                    }
                }
            }else if(data.getData() != null) {
                imageUri = data.getData();
                uriArrayList.add(imageUri);
                Cursor returnCursor =
                        this.getContext().getContentResolver().query(data.getData(), null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
//                Log.e("====>","filename: "+returnCursor.getString(nameIndex));
                fileNameArrayList.add(returnCursor.getString(nameIndex));
                returnCursor.close();
            }
            showSelectedItems();
        }
    }

    private void showSelectedItems() {
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void checkRequestedPermission() {
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUESTING_PERMISSION);
        } else {
            // Permission has already been granted

        }
    }

    public String getPostCaption() {
        return caption.getText().toString();
    }

    private void addFilesToFireBase(final ArrayList<Uri> fileUriArrayList, final ArrayList<String> fileNameArrayList) {
//        Log.e("====>","ready to send file for uploading");
        FirebaseAuth.getInstance().signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
//                Log.e("====>","sending file to be uploaded");
                if(fileUriArrayList.size()>0) {
                    fireBaseHelperClass
                            .addFilesToFireBase(fileUriArrayList, fileNameArrayList,
                                    null, getContext(), PostFragment.this);
                }else{
                    fireBaseHelperClass
                            .addPostToFireBase(new ArrayList<Uri>(), null, PostFragment.this.getContext());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Log.e("====>","Login failed");
            }
        });
    }

    public void showConfirmationMessage(){
        Toast.makeText(this.getContext(), "Posted!!!", Toast.LENGTH_SHORT).show();
    }

    public void updateArrayLists(int position) {
        fileNameArrayList.remove(position);
    }
}
