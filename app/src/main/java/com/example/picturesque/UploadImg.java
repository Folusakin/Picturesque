
package com.example.picturesque;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.renderscript.ScriptGroup;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UploadImg extends AppCompatActivity {

    // views for button
    private Button btnSelect, btnUpload, btnBack, btnFindDupes, btnDelete;
    InputImage image;
    // view for image view
    private ImageView imageView;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    private DatabaseReference mDatabaseRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_img);


        // initialise views
        btnSelect = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        imageView = findViewById(R.id.imageView);
        btnBack = findViewById(R.id.btnBack);
        btnFindDupes = findViewById(R.id.btnFindDupes);
        // btnDelete = findViewById(R.id.btnDelete);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        /*final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();*/

       mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        // on pressing btnSelect SelectImage() is called
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        // on pressing btnUpload uploadImage() is called
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        // on pressing btnFindDupes getDuplicate() is called
      /*  btnFindDupes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDuplicates();
            }
        });*/

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);

            }
        });


    }


    // Select Image method
    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);

    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);
        if (resultCode != RESULT_CANCELED) {
            // checking request code and result code
            // if request code is PICK_IMAGE_REQUEST and
            // resultCode is RESULT_OK
            // then set image in the image view
            if (requestCode == PICK_IMAGE_REQUEST
                    && resultCode == RESULT_OK
                    && data != null
                    && data.getData() != null) {

                // this gets the MD5 hash of the image that the user chose
                // Get the Uri of data
                filePath = data.getData();

                String user_upload_hash = getMD5(filePath);
                try {
                    image = InputImage.fromFilePath(getApplicationContext(), filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                labelImages(image); // Magic Here
                configureAndRunImageLabeler(image, filePath);
                detectFaces(image);

                System.out.println("This is the user upload hash: " + user_upload_hash);
                try {

                    // Setting image on image view using Bitmap
                    Bitmap bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    getContentResolver(),
                                    filePath);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    // Log the exception
                    e.printStackTrace();
                }
            }
        }

    }

   /* private void getDuplicates(){
        if (filePath != null) {
            // get the md5 hash of the user uploaded image
            String user_upload_hash = getMD5(filePath).trim();
            String short_upload_hash = removeLastCharacter(user_upload_hash);





            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            //for parents within root
            //StorageReference testRef = storage.getReference().child("picturesque-again.appspot.com");
           // System.out.println("This is the list i guess: " + testRef.toString());
            StorageReference listRef = storage.getReference().child("images");

            listRef.listAll()
                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            System.out.println("HERE are our Items: ");
                            for (StorageReference item : listResult.getItems()) {

                                // All the items under listRef
                                String refNumber = item.toString();
                                StorageReference storageRef = storage.getReference();
                                String sub = stringGrab(refNumber);
                                String[] substringref = {sub.toString()};

                                for(int i = 0; i<substringref.length;i++) {

                                    System.out.println("Substrings of files");
                                    System.out.println(substringref[i]);
                                    System.out.println(" ");

                                }

                                    // Get reference to the file
                                StorageReference forestRef = storageRef.child(sub);
                                forestRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                    @Override
                                    public void onSuccess(StorageMetadata storageMetadata) {
                                       // System.out.println("THIS IS THE SUBSTRING " + sub);
                                        //System.out.println("This is the MD5 Hash: " + storageMetadata.getMd5Hash());


                                       String firebase_md5 = storageMetadata.getMd5Hash().trim();
                                       String short_fb_hash = removeLastCharacter(firebase_md5);
                                       if(short_fb_hash.equals(short_upload_hash)){

                                           System.out.println("We HAVE A MATCH");
                                           System.out.println(sub + "'s hash: " + firebase_md5);
                                           System.out.println("User Hash: " + user_upload_hash);
                                           System.out.println(" ");

                                           //TODO we need a way to reference the

                                       }
                                       else{
                                           System.out.println("This is not a match");
                                           System.out.println(sub + "'s hash: " + firebase_md5);
                                           System.out.println("User Hash: " + user_upload_hash);
                                           System.out.println(" ");
                                       }




                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        System.out.println("Some sort of error happened >:( ");
                                        // Uh-oh, an error occurred!
                                    }
                                });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Uh-oh, an error occurred!
                        }
                    });

        }
        else{
            System.out.println("There was an error, no filepath");
        }


    }*/

    private String getMD5(Uri filePath) {

        String base64Digest = "";
        try {
            InputStream inputStream = getContentResolver().openInputStream(filePath);
            //InputStream input = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            MessageDigest md5Hash = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0) {
                    md5Hash.update(buffer, 0, numRead);
                }
            }
            inputStream.close();
            byte[] md5Bytes = md5Hash.digest();
            base64Digest = Base64.encodeToString(md5Bytes, Base64.DEFAULT);

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return base64Digest;

    }


    // UploadImage method
    private void uploadImage() {
        if (filePath != null) {
            String imageName = getFileName(filePath);
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            /*StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + imageName);*/
// I changed this because if we keep the same file name firebase wont count the upload
            // Firebase auto-protects against duplicate uploads

            String upload_image_name = UUID.randomUUID().toString();
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + upload_image_name);

            System.out.println("This is our IMAGE NAME DUDE: " + imageName);
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
                                            if (i > 0)
                                                System.out.println("There is/are " + i + " person(s) in this picture");
                                            else
                                                System.out.println("There are no faces in this picture");
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

                                            int finalI = i;
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
                                                            String dude = "";
                                                            int k;
                                                            if(label2.size()<4){
                                                                k = label2.size();
                                                            }
                                                            else
                                                                k=4;
                                                            for(int j = 0;j<k;j++){
                                                                if(!dude.equals("")){
                                                                    dude = dude+" "+label2.get(j);
                                                                }
                                                                else
                                                                    dude = dude+label2.get(j);

                                                            }
                                                            StorageMetadata metadata = new StorageMetadata.Builder()
                                                                    .setCustomMetadata("Face Count", String.valueOf(finalI))
                                                                    .setCustomMetadata("Contents", dude)
                                                                    .build();


                                                            ref.putFile(filePath, metadata)
                                                                    .addOnSuccessListener(
                                                                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                                                                @Override
                                                                                public void onSuccess(
                                                                                        UploadTask.TaskSnapshot taskSnapshot) {

                                                                                    // Image uploaded successfully
                                                                                    // Dismiss dialog
                                                                                    progressDialog.dismiss();
                                                                                    Toast
                                                                                            .makeText(UploadImg.this,
                                                                                                    "Image Uploaded!!",
                                                                                                    Toast.LENGTH_SHORT)
                                                                                            .show();
                                                                                    Upload upload = new Upload(upload_image_name.trim(),
                                                                                            taskSnapshot.getStorage().getDownloadUrl().toString());
                                                                                    String uploadId = mDatabaseRef.push().getKey();
                                                                                    mDatabaseRef.child(upload_image_name).setValue(upload);
                                                                                }


                                                                            }


                                                                    )
// This is not the On failure listener we want to get rid of
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {

                                                                            // Error, Image not uploaded
                                                                            progressDialog.dismiss();
                                                                            Toast
                                                                                    .makeText(UploadImg.this,
                                                                                            "Failed " + e.getMessage(),
                                                                                            Toast.LENGTH_SHORT)
                                                                                    .show();
                                                                        }
                                                                    })
                                                                    .addOnProgressListener(
                                                                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                                                                // Progress Listener for loading
                                                                                // percentage on the dialog box
                                                                                @Override
                                                                                public void onProgress(
                                                                                        UploadTask.TaskSnapshot taskSnapshot) {
                                                                                    double progress
                                                                                            = (100.0
                                                                                            * taskSnapshot.getBytesTransferred()
                                                                                            / taskSnapshot.getTotalByteCount());
                                                                                    progressDialog.setMessage(
                                                                                            "Uploaded "
                                                                                                    + (int) progress + "%");
                                                                                }
                                                                            });

                                                            IntBuffer buffer = IntBuffer.allocate(1);
                                                            buffer.put(finalI);
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



                                        }
                                                    });

            /*StorageMetadata metadata = new StorageMetadata.Builder()
                    .setCustomMetadata("Face Count", "Number")
                    .setCustomMetadata("Contents", "ML Tags")
                    .build();


            ref.putFile(filePath, metadata)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(UploadImg.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    Upload upload = new Upload(upload_image_name.trim(),
                                            taskSnapshot.getStorage().getDownloadUrl().toString());
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(upload_image_name).setValue(upload);
                                }


                            }


                    )
// This is not the On failure listener we want to get rid of
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(UploadImg.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });*/

        }
    }

    public void buttonClicked(View view) {
        if (view.getId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    public String stringGrab(String Origin) {
        return Origin.substring(35);

    }

    public static String removeLastCharacter(String str) {
        String result = null;
        if ((str != null) && (str.length() > 0)) {
            result = str.substring(0, str.length() - 2);
        }
        return result;
    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void detectFaces(InputImage image) {

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
                                        if (i > 0)
                                            faceCount("There is/are " + i + " person(s) in this picture");
                                        else
                                            faceCount("There are no faces in this picture");

                                        IntBuffer buffer = IntBuffer.allocate(1);
                                        buffer.put(i);
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

    public void similarityAssessment(String randomNumber) {
        TextView randomNumberTv = (TextView) findViewById(R.id.textView2);
        randomNumberTv.setText(randomNumber);
    }

    public void faceCount(String Face) {
        System.out.println(Face);
        TextView faceTv = (TextView) findViewById(R.id.textView);
        faceTv.setText(Face.toString());

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

       /* private String getFileExtension (Uri uri){
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cR.getType(uri));


        }*/
    }
}
//}


