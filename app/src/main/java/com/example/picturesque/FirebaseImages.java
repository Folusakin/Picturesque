package com.example.picturesque;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class FirebaseImages extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference("images/0c12c938-f0dc-4009-a918-113eee81d0a1");
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_view);


        imageView = findViewById(R.id.image_view_upload);

        GlideApp.with(this)
                .load(storageReference)
                .into(imageView);

        StorageReference listRef = storage.getReference().child("images");
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        System.out.println("HERE are our Items: ");
                        //ArrayList<String> substringref = new ArrayList<String>();
                        for (StorageReference item : listResult.getItems()) {

                            // All the items under listRef
                            String refNumber = item.toString();
                            StorageReference storageRef = storage.getReference();
                            String sub = stringGrab(refNumber);
                            ///substringref.add(sub);// = {sub.toString().append()};

                            String[] substringref = {sub};


                            //for(int i = 0; i<substringref.size();i++) {
                            for (int i = 0; i < substringref.length; i++) {

                                System.out.println("Substrings of files");
                                // System.out.println(substringref.get(i));
                                System.out.println(substringref[i]);
                                System.out.println(" ");

                            }
                        }


                    }
                });
    }



    public String stringGrab(String Origin) {
        return Origin.substring(35);

    }
}