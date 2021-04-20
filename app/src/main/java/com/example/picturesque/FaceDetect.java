//TODO get a list of all images in FB


package com.example.picturesque;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.google.mlkit.vision.common.InputImage;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
    public class FaceDetect extends  AppCompatActivity {

        InputImage image;

        ImageView imageView;

        Context context;
        Button button, btnBack;

        Button button2;

        private static final int PICK_IMAGE = 100;
        Uri imageUri;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.img_comparison);
            imageView = (ImageView)findViewById(R.id.imageView);
            button = (Button)findViewById(R.id.buttonLoadPicture);

            //button2 = (Button)findViewById(R.id.button);
            btnBack = (Button)findViewById(R.id.btnBack);


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, PICK_IMAGE);

                }
            });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Menu.class);
                    startActivity(intent);
                }
            });
        }
        private void openGallery() {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode != RESULT_CANCELED) {
                if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
                    imageUri = data.getData();
                    System.out.println("THIS IS THE URI" + imageUri);
                    imageView.setImageURI(imageUri);
                    context = getApplicationContext();

                    System.out.println("THIS IS THE URI:   " + imageUri);
                    try {
                        image = InputImage.fromFilePath(context, imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("the image: " + image);

                }

            }



            // [END image_from_path
            labelImages(image); // Magic Here
            configureAndRunImageLabeler(image,imageUri);
            detectFaces(image);


        }

// returns the labels
        public void similarityAssessment(String randomNumber) {
            TextView randomNumberTv = (TextView) findViewById(R.id.textView2);
            randomNumberTv.setText(randomNumber);
        }
        public void faceCount(String Face){
            System.out.println(Face);
            TextView faceTv = (TextView) findViewById(R.id.textView);
            faceTv.setText(Face.toString());


        }



        public void detectFaces(InputImage image){

            FaceDetectorOptions options =
                    new FaceDetectorOptions.Builder()
                            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                            .setMinFaceSize(0.1f)
                            .enableTracking()
                            .build();
            // [END set_detector_options]

            // [START get_detector]

            FaceDetector detector = FaceDetection.getClient(options);
            Task<List<Face>> result =
                    detector.process(image)
                            .addOnSuccessListener(
                                    new OnSuccessListener<List<Face>>() {
                                        @Override
                                        public void onSuccess(List<Face> faces) {
                                            // Task completed successfully
                                            // [START_EXCLUDE]
                                            // [START get_face_info]
                                            int i = 0;
                                            for (Face face : faces) {
                                                Rect bounds = face.getBoundingBox();
                                                i++;

                                            }
                                            if(i>0)
                                                faceCount("There is/are "+ i + " person(s) in this picture");
                                            else
                                                faceCount("There are no faces in this picture");
                                            // [END get_face_info]
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

        public void configureAndRunImageLabeler(InputImage image, Uri Uri) {
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


                            }
                            similarityAssessment(label2.get(1));



                        }
                    });


        }
    }
//}
