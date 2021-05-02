package com.example.picturesque;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Menu extends AppCompatActivity {


    private Button findDupes, upload, logout, mltagging, viewcloudimgs;

 protected void onCreate(Bundle savedInstanceState){
     super.onCreate(savedInstanceState);
     setContentView(R.layout.main_menu);


     findDupes = findViewById(R.id.findDupes);
     upload = findViewById(R.id.upload);
     logout = findViewById(R.id.logout);
     mltagging = findViewById(R.id.mltagging);
     viewcloudimgs = findViewById(R.id.viewcloudimgs);


     findDupes.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent = new Intent(getApplicationContext(),DuplicateFind.class);
             startActivity(intent);

         }
     });

     upload.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             Intent intent = new Intent(getApplicationContext(),UploadImg.class);
             startActivity(intent);

         }
     });

     logout.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             FirebaseAuth.getInstance().signOut();
             Intent intent = new Intent(getApplicationContext(),MainActivity.class);
             startActivity(intent);
             //this.finish();

         }
     });

     mltagging.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent = new Intent(getApplicationContext(),FaceDetect.class);
             startActivity(intent);
         }
     });

     viewcloudimgs.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent = new Intent(Menu.this,FirebaseImages.class);
             startActivity(intent);
         }
     });
 }

}
