package com.example.picturesque;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Set;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;
    public ImageAdapter(Context context, List<Upload> uploads) {
        mContext = context;
        mUploads = uploads;
    }
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new ImageViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference ImageRef = storageRef.child("images/"+uploadCurrent.getName());

        ImageRef.getMetadata().addOnSuccessListener(
                new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {

                        String faceNumber = storageMetadata.getCustomMetadata("Face Count");
                        String labels = storageMetadata.getCustomMetadata("Contents");
                        int i = Integer.parseInt(faceNumber);
                        if(i==0){
                            holder.textViewName.setText(labels+"\n"+"There are no people in this picture");

                        }
                        else if(i==1) {
                            holder.textViewName.setText(labels + "\n" + "There is 1 person in this picture");
                        }
                        else
                            holder.textViewName.setText(labels + "\n" + "There are"+ faceNumber+"person in this picture");


                    }
                });


        System.out.println("THIS IS THE IMG URL" + uploadCurrent.getImageUrl().toString());
       // Picasso.with(mContext)
/*        Picasso.get()
                .load(uploadCurrent.getImageUrl())
//                .load("com.google.android.gms.tasks.zzw@75ba05d")
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);*/
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("images/"+uploadCurrent.getName());
        Glide.with(mContext)
                .load(storageReference)
                .into(holder.imageView);
    }
    @Override
    public int getItemCount() {
        return mUploads.size();
    }
    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public ImageView imageView;
        public ImageViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            imageView = itemView.findViewById(R.id.image_view_upload);
        }
    }
}
