package info.mores.weedon;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Kunal Gharate on 15-12-2017.
 */

public class WeedOn extends Application {

    FirebaseAuth mAuth;
    DatabaseReference mServicesDatabase;
    DatabaseReference ChatDatabaseRef;
    DatabaseReference mFriendsDatabase;


    @Override
    public void onCreate() {
        super.onCreate();


        if (FirebaseApp.initializeApp(getApplicationContext())!= null) {

            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        }
          /* Picasso */

 /*       Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
*/
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {

            mServicesDatabase = FirebaseDatabase.getInstance()
                    .getReference().child("all_services").child(mAuth.getCurrentUser().getUid());
            ChatDatabaseRef = FirebaseDatabase.getInstance().getReference().child("chat");
            mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_following));
            mFriendsDatabase.keepSynced(true);
            mServicesDatabase.keepSynced(true);
            ChatDatabaseRef.keepSynced(true);


            mServicesDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {

                        // mServicesDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
}
