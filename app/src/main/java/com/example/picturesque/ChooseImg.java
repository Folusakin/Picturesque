package com.example.picturesque;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class ChooseImg extends AppCompatActivity {



    InputImage image;
    ImageView imageView;
    Button button;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    // private Object OnCompleteListener;
    private static final int IMAGE_REQUEST = 2;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.imageView);
        button = (Button)findViewById(R.id.buttonLoadPicture);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });
    }

    // implements logout button
    public void buttonClicked(View view){
        if(view.getId() == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            uploadImage();
        }
    }*/



    // if the upload button is clicked
    public void uploadClicked(View view){
        openGallery();
        uploadImage();
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();
        if (imageUri != null) {
            StorageReference filRef = FirebaseStorage.getInstance().getReference().child("uploads").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            // Put the file in the cloud
            filRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    filRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = imageUri.toString();

                            Log.d("DownloadUrl", url);
                            // dismissing the loading thing
                            pd.dismiss();
                            Toast.makeText(ChooseImg.this, "Image upload successfull", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        }


    }


    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }

        try {

            image = InputImage.fromFilePath(getApplicationContext(), imageUri);
            System.out.println("the image: "+image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // [END image_from_path]
        labelImages(image); // Magic Here
        configureAndRunImageLabeler(image);


    }

    public void displayLotteryNumberToUser(String randomNumber) {
        TextView randomNumberTv = (TextView) findViewById(R.id.textView2);
        randomNumberTv.setText(randomNumber.toString());
    }

    public void labelImages(InputImage image) {
        ImageLabelerOptions options =
                new ImageLabelerOptions.Builder()
                        .setConfidenceThreshold(0.8f)
                        .build();

        // [START get_detector_options]
        ImageLabeler labeler = ImageLabeling.getClient(options);
        // [END get_detector_options]

        // [START run_detector]
        Task<List<ImageLabel>> result =
                labeler.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<ImageLabel>>() {
                                    @Override
                                    public void onSuccess(List<ImageLabel> labels) {
                                        // Task completed successfully
                                        // [START_EXCLUDE]
                                        // [START get_labels]
                                        for (ImageLabel label : labels) {
                                            String text = label.getText();
                                            float confidence = label.getConfidence();
                                        }
                                        // [END get_labels]
                                        // [END_EXCLUDE]
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
        // [END run_detector]
    }

    public void configureAndRunImageLabeler(InputImage image) {
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        // Task completed successfully
                        // ...
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });

        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        // [START get_image_label_info]
                        List<String> label2 = new ArrayList<String>();
                        for (ImageLabel label : labels) {

                            String text = label.getText();
                            float confidence = label.getConfidence();
                            int index = label.getIndex();

                            label2.add(text);
                            System.out.println("THESE are the index NUMBERS HHAHAH!!!!!!!: "+text);

                            System.out.println("THESE are the index NUMBERS after HHAHAH!!!!!!!: "+text);
                            System.out.println(label2);

                        }
                        System.out.println("Outside the main: "+ label2);
                        String dude = " ";
                        for (int i = 0; i < label2.size(); i++) {

                            if(i>0 && label2.get(i) == "Dude")
                                dude = label2.get(i);
                            else if (i>0)
                                dude = dude+label2.get(i)+"\n";

                        }
                        System.out.println("Outside the dude main: "+ dude);
                        displayLotteryNumberToUser(dude);
                        // [END get_image_label_info]
                        System.out.println(dude);
                    }
                });


    }

}