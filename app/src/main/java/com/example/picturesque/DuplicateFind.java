package com.example.picturesque;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DuplicateFind extends AppCompatActivity {

    private Button btnChoose, btnFindDupes, btnBack, btnPhoneDelete, btnCloudDelete;

    InputImage image;
    // view for image view
    private ImageView imageView, imageView2;

    private String[] fb_hash_array={};
    Integer duplicate_count = 0;
    // Uri indicates, where the image will be picked from
    private Uri filePath;
    public String[] duplicate_substrings;
    public String[] substringref;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.duplicate_find);


        // initialise views
        btnChoose = findViewById(R.id.btnChoose);
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        btnBack = findViewById(R.id.btnBack);
        btnFindDupes = findViewById(R.id.btnFindDupes);
        btnPhoneDelete = findViewById(R.id.btnPhoneDelete);
        btnCloudDelete = findViewById(R.id.btnCloudDelete);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duplicate_count = 0;
                imageView2.setImageURI(null);
                SelectImage();
            }
        });

        // on pressing btnFindDupes getDuplicate() is called
        btnFindDupes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duplicate_count = 0;
                try {
                    getDuplicates();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);
        if(resultCode != RESULT_CANCELED) {
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


    private void getDuplicates() throws InterruptedException {
        if (filePath != null) {
            // get the md5 hash of the user uploaded image
            String user_upload_hash = getMD5(filePath).trim();
            String short_upload_hash = removeLastCharacter(user_upload_hash);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
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
                                for(int i = 0; i<substringref.length;i++) {

                                    System.out.println("Substrings of files");
                                   // System.out.println(substringref.get(i));
                                    System.out.println(substringref[i]);
                                    System.out.println(" ");

                                }
                               // System.out.println("LENGTH OF SUBSTRING "+substringref.size());
                                System.out.println("LENGTH OF SUBSTRING "+substringref.length);
                                // Get reference to the file
                                StorageReference forestRef = storageRef.child(sub);
                                forestRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                    @Override
                                    public void onSuccess(StorageMetadata storageMetadata) {

                                        String firebase_md5 = storageMetadata.getMd5Hash().trim();
                                        String[] fb_hash_array = {firebase_md5};
                                       /* ArrayList<String> fb_hash_array= new ArrayList<String>();
                                        ArrayList<String> short_fb_hash = new ArrayList<String>();
                                        ArrayList<String> duplicate_substrings = new ArrayList<String>();
                                        fb_hash_array.add(firebase_md5);
                                        short_fb_hash.add(removeLastCharacter(firebase_md5));*/
                                        String[] short_fb_hash = {removeLastCharacter(firebase_md5)};
                                       // for (int i = 0; i < substringref.size(); i++) {

                                            for (int i = 0; i < substringref.length; i++) {
                                        if (short_fb_hash[i].equals(short_upload_hash)) {

                                            //if (fb_hash_array.get(i).equals(short_upload_hash)) {

                                            //duplicate_substrings.set(i, substringref.get(i));
                                            String[] duplicate_substrings = {sub};
                                            duplicate_substrings[i] = sub;

                                            System.out.println("We HAVE A MATCH");
                                            System.out.println("Duplicate Substring: " + duplicate_substrings[i]);
                                            System.out.println(sub + "'s hash: " + firebase_md5);
                                            System.out.println("User Hash: " + user_upload_hash);
                                            System.out.println(" ");

                                            duplicate_count = duplicate_count + 1;
                                            //TODO store duplicate substrings


                                            btnPhoneDelete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    System.out.println("Phone DELETE BUTTON");
                                                    //ContentResolver test = getContentResolver();
                                                    // String  fileName = getFileName(filePath);
                                                    //filePath = Uri.parse(getIntent().getStringExtra(fileName));

                                                    // Get a filepath from a URI
                                                    String uriFilepath = getFilePath(filePath);

                                                    File fdelete = new File(uriFilepath);

                                                    if (fdelete.exists()) {
                                                        if (fdelete.delete()) {
                                                            System.out.println("file Deleted :");
                                                        } else {
                                                            System.out.println("file not Deleted :");
                                                        }
                                                    }
                                                }
                                            });

                                            btnCloudDelete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    System.out.println("Cloud DELETE BUTTON");
                                                    StorageReference storageRef = storage.getReference();

                                                   // for (int i = 0; i < duplicate_substrings.size(); i++) {
                                                    for (String duplicate_substring : duplicate_substrings) {
                                                        // Create a reference to the file to delete
                                                        // StorageReference desertRef = storageRef.child(duplicate_substrings.get(i));
                                                        StorageReference dupeRef = storageRef.child(duplicate_substring);

                                                        // Delete the file
                                                        dupeRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // File deleted successfully
                                                                System.out.println("Successful delete");
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception exception) {
                                                                // Uh-oh, an error occurred!
                                                                System.out.println("NOH an error happen :(");
                                                            }
                                                        });
                                                    }


                                                }
                                            });


                                        }
                                            else{
                                            System.out.println("This is not a match");
                                            System.out.println(sub + "'s hash: " + firebase_md5);
                                            System.out.println("User Hash: " + user_upload_hash);
                                            System.out.println(" ");
                                        }
                                    }


                                        System.out.println("DUPE COUNT: " + duplicate_count);
                                        String dupeString = duplicate_count.toString();
                                        TextView dupecount = (TextView) findViewById(R.id.dupecount);
                                        dupecount.setText(dupeString);

                                        if(duplicate_count != 0){
                                            imageView2.setImageURI(filePath);

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
                           // System.out.println("Dupe Count: " + dupeCount(short_upload_hash,fb_hash_array));

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


    //getting real path from uri
    private String getFilePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            String filePath = cursor.getString(columnIndex); // returns null
            cursor.close();
            return filePath;
        }
        return null;
    }




   /* public static void deleteFileFromMediaStore(final ContentResolver contentResolver, final File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");
        final int result = contentResolver.delete(uri,
                MediaStore.Files.FileColumns.DATA + "=?", new String[]{canonicalPath});
        if (result == 0) {
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                contentResolver.delete(uri,
                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
            }
        }
    }*/

   /* public Integer dupeCount(String user_hash, String[] firebase_hashes){
        Integer duplicate_count = 0;

        for(int i = 0; i<firebase_hashes.length; i++){

            if(user_hash.equals(firebase_hashes[i])){
                duplicate_count = duplicate_count + 1;
            }

        }

        return duplicate_count;
    };*/



}
