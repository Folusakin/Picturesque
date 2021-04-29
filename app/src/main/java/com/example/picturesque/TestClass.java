package com.example.picturesque;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/*public class TestClass {
 
        // All the items under listRe
    public List listofFiles(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        
        StorageReference listRef = storage.getReference().child("images");
        List listResult = (List) listRef.listAll();

        try{}
        catch{};



        return listResult;
    }
    public String stringGrab(String Origin) {
        return Origin.substring(35);

    }
    

    
}*/
