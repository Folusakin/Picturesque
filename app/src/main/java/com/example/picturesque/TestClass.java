package com.example.picturesque;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class TestClass {
 
        // All the items under listRe
    public List listofFiles(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        
        StorageReference listRef = storage.getReference().child("images");
        List listResult = (List) listRef.listAll();

        return listResult;
    }
    public String stringGrab(String Origin) {
        return Origin.substring(35);

    }
    

    
}
