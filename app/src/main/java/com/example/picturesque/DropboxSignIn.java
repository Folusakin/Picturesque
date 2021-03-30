package com.example.picturesque;
import android.content.Intent;
import android.os.Bundle;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.google.firebase.auth.FirebaseAuth;



public class DropboxSignIn extends MainActivity {

    private static final String ACCESS_TOKEN = "sl.At8HmNUpLx3y1zNpG0slkON1LcKRwFQUtIYAop6sqi7nnIk-6C0aLseGaq2xZmQTfqhHFQ6dhe_bhYsUQ6E0zzXKrSAGuV8-nqmJS5IGc-hbLytqcMxC3dECf-9mOHmSkAw3B9XI";



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);



        createRequest();

        findViewById(R.id.dbSignIn).setOnClickListener(view -> signIn());

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public static void main(String args[]) throws DbxException {
        // Create Dropbox client
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);


        FullAccount account = client.users().getCurrentAccount();
        System.out.println(account.getName().getDisplayName());
    }
}
