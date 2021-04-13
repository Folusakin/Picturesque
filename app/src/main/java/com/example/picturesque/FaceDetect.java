

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
        InputImage image2;


        ImageView imageView;

        Context context;
        Button button;
        ImageView imageView2;
        Button button2;

        private static final int PICK_IMAGE = 100;
        private static final int PICK_IMAGE_2 = 2;

        Uri imageUri;
        Uri imageUri2;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            imageView = (ImageView)findViewById(R.id.imageView);
            button = (Button)findViewById(R.id.buttonLoadPicture);
            imageView2 = (ImageView)findViewById(R.id.imageView2);
            button2 = (Button)findViewById(R.id.button);


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, PICK_IMAGE);

                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gallery2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(gallery2, PICK_IMAGE_2);

                }
            });
        }
        private void openGallery() {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        }
        private void openGallery2() {
            Intent gallery2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery2, PICK_IMAGE_2);
        }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
                imageUri = data.getData();
                System.out.println("THIS IS THE URI"+imageUri);
                imageView.setImageURI(imageUri);
                context = getApplicationContext();

                System.out.println("THIS IS THE URI:   "+imageUri);
                try {
                    image = InputImage.fromFilePath(context, imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("the image: "+image);

            }
            else if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_2 && image2 == null) {
                imageUri2 = data.getData();
                imageView2.setImageURI(imageUri2);
                context = getApplicationContext();

                try {
                    image2 = InputImage.fromFilePath(context, imageUri2);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("the image: "+image2);
            }



            byte[] image1 = new byte[0];
            byte[] image_2 = new byte[0];
            try {
                image1 = convertImageToByte(imageUri);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            if(image2 != null) {
                try {
                    image_2 = convertImageToByte(imageUri2);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }


            if(image2 != null) {
                for(int i=0; i< image1.length ; i++) {
                    System.out.print(image1[i] +" ");
                }
                System.out.println(" ");
                for(int i=0; i< image_2.length; i++) {
                    System.out.print(image_2[i] +" ");
                }
                boolean retval = Arrays.equals(image1, image_2);
                if (retval == true)
                    similarityAssessment("These are the same image");
                else if (retval == false)
                    similarityAssessment("These are not the same image");
                System.out.println(" ");
                System.out.println("Uri for the first image: "+imageUri);
                System.out.println("Uri for the second image: "+imageUri2);
            }

            // [END image_from_path
            labelImages(image); // Magic Here
            configureAndRunImageLabeler(image,imageUri);
            detectFaces(image);


        }
        public byte[] convertImageToByte(Uri uri) throws NoSuchAlgorithmException {
            byte[] data = new byte[0];
            try {
                ContentResolver cr = getBaseContext().getContentResolver();
                InputStream inputStream = cr.openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream boos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, boos);
                data = boos.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            MessageDigest digest = MessageDigest.getInstance("SHA-1");

            digest.update(data);

            byte[] hashedBytes = digest.digest();
            return hashedBytes;
        }

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
