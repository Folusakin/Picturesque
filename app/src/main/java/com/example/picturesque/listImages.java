package com.example.picturesque;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class listImages extends AppCompatActivity {
    private StorageReference mStorageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_file);

        mStorageReference = FirebaseStorage.getInstance().getReference().child("testfolder2/bike.jpg");


        try {
            final File localFile = File.createTempFile("bike","jpg");
            mStorageReference.getFile(localFile)
                  .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                      @Override
                      public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                          Toast.makeText(listImages.this, "Picture Retreived", Toast.LENGTH_SHORT).show();
                          Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                          ((ImageView)findViewById(R.id.imageView)).setImageBitmap(bitmap);
                      }
                  }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                        Toast.makeText(listImages.this, "Pic Recieved", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
