package com.example.picturesque;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class  FirebaseImages extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<Upload> mUploads;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3,RecyclerView.VERTICAL,false));
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle = findViewById(R.id.progress_circle);
        mUploads = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }
                mAdapter = new ImageAdapter(getApplicationContext(), mUploads);
                mRecyclerView.setAdapter(mAdapter);
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FirebaseImages.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }
    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }*/

}

/*
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class FirebaseImages extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference("images/0c12c938-f0dc-4009-a918-113eee81d0a1");
    private ImageView imageView;

    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
context = getApplicationContext();
       // View rootView = inflater.inflate(R.layout.cespite_card_view, container, false);
       // imageView = findViewById(R.id.image_view);
// Glide displays one image here!
        */
/*GlideApp.with(this)
                .load(storageReference)
                .into(imageView);*//*




    */
/*    RecyclerView recyclerView = findViewById(R.id.recyclerView);
       // recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));*//*


       */
/* MyMovieData[] myMovieData = new MyMovieData[]{
                new MyMovieData("Avengers","2019 film",R.drawable.avenger),
                new MyMovieData("Venom","2018 film",R.drawable.venom),
                new MyMovieData("Batman Begins","2005 film",R.drawable.batman),
                new MyMovieData("Jumanji","2019 film",R.drawable.jumanji),
                new MyMovieData("Good Deeds","2012 film",R.drawable.good_deeds),
                new MyMovieData("Hulk","2003 film",R.drawable.hulk),
                new MyMovieData("Avatar","2009 film",R.drawable.avatar),
        };

        MyMovieAdapter myMovieAdapter = new MyMovieAdapter(myMovieData,MainActivity.this);
        recyclerView.setAdapter(myMovieAdapter);*//*


    //}


        StorageReference listRef = storage.getReference().child("images");
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {

                        for (StorageReference item : listResult.getItems()) {
                           */
/* GlideApp.with(context)
                                    .load(item)
                                    .into(imageView);*//*

                            // All the items under listRef

                            // getting the download urls]
                            // we may not need this

                            StorageReference mImageStorage = FirebaseStorage.getInstance().getReference();
                            StorageReference filepath = mImageStorage.child("images");
                            String downloadurl = item.getDownloadUrl().toString();

                            String[] url_array = {downloadurl};

                   */
/*         filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //The download url
                                    final String downloadUrl = uri.toString();
                                    Log.d("tag", downloadUrl);
                                    if (!downloadUrl.equals("default")) {

                                        Glide.with(itemView.context).load(downloadUrl).into(blogImageView);

                                        //    Glide.with(getApplicationContext()).load(downloadUrl).into(mDisplayImage);


                                    }
                                }});}
*//*


                            String refNumber = item.toString();
                            StorageReference storageRef = storage.getReference();
                            String sub = stringGrab(refNumber);


                            String[] substringref = {sub};


                            // okay so all this does is loop through the references and has glide
                            // load them
                            // It works!! but. It doesnt populate any sort of view
                            // it goes too fast for the eye to see and
                            // lands on the last image in FB
                            for (int i = 0; i < substringref.length; i++) {

                                StorageReference picref = storage.getReference(substringref[i]);
                                GlideApp.with(context)

                                        .load(picref)
                                        .into(imageView);



                                //System.out.println("This is the URL " + url_array[i]);


                            }
                        }


                    }
                });


    }

    public String stringGrab(String Origin) {
        return Origin.substring(35);
    }
}
*/


