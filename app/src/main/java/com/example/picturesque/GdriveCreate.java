/*
package com.example.picturesque;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class GdriveCreate extends AppCompatActivity {


        private static final int REQUEST_CODE_SIGN_IN = 100;
        private GoogleSignInClient mGoogleSignInClient;
        private DriveServiceHelper mDriveServiceHelper;
        private static final String TAG = "MainActivity";
        private Button login;
    private Button searchFile;
        private Button searchFolder;
        private Button createTextFile;
        private Button createFolder;
        private Button uploadFile;
        private Button downloadFile;
        private Button deleteFileFolder;
        private TextView email;
        private Button viewFileFolder;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            private void initView initView();


            searchFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDriveServiceHelper == null) {
                        return;
                    }
                    mDriveServiceHelper.searchFile("textfilename.txt", "text/plain")
                            .addOnSuccessListener(new OnSuccessListener<List<GoogleDriveFileHolder>>() {
                                @Override
                                public void onSuccess(List<GoogleDriveFileHolder> googleDriveFileHolders) {

                                    Gson gson = new Gson();
                                    Log.d(TAG, "onSuccess: " + gson.toJson(googleDriveFileHolders));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.getMessage());
                                }
                            });

                }
            });

            () {

                email = findViewById(R.id.email);
                LinearLayout gDriveAction = findViewById(R.id.g_drive_action);
                searchFile = findViewById(R.id.search_file);
                searchFolder = findViewById(R.id.search_folder);
                createTextFile = findViewById(R.id.create_text_file);
                createFolder = findViewById(R.id.create_folder);
                uploadFile = findViewById(R.id.upload_file);
                downloadFile = findViewById(R.id.download_file);
                deleteFileFolder = findViewById(R.id.delete_file_folder);
                viewFileFolder = findViewById(R.id.view_file_folder);
            }
        }

}
*/
