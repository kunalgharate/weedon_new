package info.mores.weedon;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kaopiz.kprogresshud.KProgressHUD;

import de.hdodenhof.circleimageview.CircleImageView;
import info.mores.weedon.Adapter.MultiViewTypeAdapter;

public class SubscribeActivity extends AppCompatActivity {

    private static final String TAG = "SubscribeActivity";
    public static int subscriber_id;
    public static String subscribe = "subscribe";
    public static String unsubscribe = "unsubscribe";
    private static String user_id;
    private static String checkId;
    private static String subscriber_string;
    Context context;
    private Button subscribebtn, unsubscribebtn;
    String display_name;
    Toolbar toolbar;
    String title;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView mServiceName;
    Boolean mFollowing = false;
    private ImageView serviceSubImg;
    private TextView mServiceDesc, mServiceCount;
    private DatabaseReference mServicesDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;
    private FirebaseUser mCurrent_user;
    private KProgressHUD loadingDialog;
    private iOSDialog dialog;
    private Toolbar subToolbar;
    AdView adView;
    private int mFollowingCount = 0;
    private int mFollowersCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        title = getIntent().getStringExtra("title");
        user_id = getIntent().getStringExtra("user_id");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        loadingDialog = new KProgressHUD(this);
        dialog = new iOSDialog(this,"Unfollow","Do you want to unfollow it ?",false,null,true);
        //Progress progress = new Progress(context);
        /*subToolbar =findViewById(R.id.sub_toolbar);
        setSupportActionBar(subToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
        Log.e("oncreate", "oncreate");
        /////////////////////////////////////////
        mServicesDatabase = FirebaseDatabase.getInstance().getReference().child("all_services").child(user_id);
        mServicesDatabase.keepSynced(true);
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("subscribers");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();


        serviceSubImg = findViewById(R.id.expandedImage);
        mServiceName = findViewById(R.id.service_title);
        mServiceDesc = findViewById(R.id.desc_details);
        mServiceName.setText(title);
        //  mServiceCount = (TextView) findViewById(R.id.total_subscribers);
        subscribebtn = findViewById(R.id.button_subscribe);
        unsubscribebtn = findViewById(R.id.button_unsubscribe);
        mServiceCount = findViewById(R.id.counter_text);
        // loadingDialog.setMessage("Loading....");



        mServicesDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    display_name = dataSnapshot.child("service_name").getValue().toString();
                    String desc = dataSnapshot.child("service_desc").getValue().toString();
                    String image = dataSnapshot.child("service_image").getValue().toString();

                    //     mServiceName.setText(display_name);
                    mServiceDesc.setText(desc);

                    if (!SubscribeActivity.this.isFinishing()) {
                     //   GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(serviceSubImg);
                       /* Glide.with(SubscribeActivity.this)
                                .load(image)
                                // .placeholder(R.drawable.common_google_signin_btn_icon_dark)
                                .into(serviceSubImg);
*/

                        RequestOptions options =  new RequestOptions()
                                .centerCrop()
                                .error(R.drawable.ic_error)
                                .priority(Priority.HIGH)
                                .diskCacheStrategy(DiskCacheStrategy.ALL);

                        Glide.with(SubscribeActivity.this)
                                .load(image)
                                .apply(options)
                                .into(serviceSubImg);
                    }



                }
                // loadingDialog.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        isFollowing();
        getFollowersCount();
        //showData();


        subscribebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgressDialog();

                FirebaseMessaging.getInstance().subscribeToTopic(user_id);

               // Toast.makeText(SubscribeActivity.this, "Subscribed  : "+user_id, Toast.LENGTH_LONG).show();

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(user_id)
                        .child(getString(R.string.field_user_id))
                        .setValue(user_id);

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.dbname_followers))
                        .child(user_id)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.field_user_id))
                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                setFollowing();

                loadingDialog.dismiss();
                Intent intent =new Intent(SubscribeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }


        });

        unsubscribebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        dialog.setPositive("Yes", new iOSDialogClickListener() {
                            @Override
                            public void onClick(iOSDialog dialog) {
                                showProgressDialog();

                                Log.d(TAG, "onClick: now unfollowing: " + user_id);
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(user_id);
                                FirebaseDatabase.getInstance().getReference()
                                        .child(getString(R.string.dbname_following))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(user_id)
                                        .removeValue();

                                FirebaseDatabase.getInstance().getReference()
                                        .child(getString(R.string.dbname_followers))
                                        .child(user_id)
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .removeValue();
                                setUnfollowing();
                                loadingDialog.dismiss();
                                Intent intent =new Intent(SubscribeActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                dialog.dismiss();
                            }
                        });

                        dialog.setNegative("Cancel", new iOSDialogClickListener() {
                            @Override
                            public void onClick(iOSDialog dialog) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();


            }
        });

        // for showing ads on activity
        adView=findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);



    }

    private void setFollowing() {
        Log.d(TAG, "setFollowing: updating UI for following this user");
        subscribebtn.setVisibility(View.GONE);
        unsubscribebtn.setVisibility(View.VISIBLE);
        // editProfile.setVisibility(View.GONE);
        //loadingDialog.dismiss();

    }

    private void setUnfollowing() {
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        subscribebtn.setVisibility(View.VISIBLE);
        unsubscribebtn.setVisibility(View.GONE);
        // editProfile.setVisibility(View.GONE);
       // loadingDialog.dismiss();
    }


    private void isFollowing() {
        //showProgressDialog();
        Log.d(TAG, "isFollowing: checking if following this users.");
        setUnfollowing();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild(getString(R.string.field_user_id)).equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue());

                    setFollowing();
                    FirebaseMessaging.getInstance().subscribeToTopic(user_id);
                   // loadingDialog.dismiss();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        //showData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // subscriber_string =null;
        loadingDialog.dismiss();
        finish();
    }

    @Override
    public void onBackPressed() {
        loadingDialog.dismiss();
        dialog.dismiss();
        overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
        finish();

        super.onBackPressed();
    }


   /* public void showData() {
        loadingDialog.show();

        mFriendDatabase.child(mCurrent_user.getUid()).child(user_id).addValueEventListener(new ValueEventListener() {
            private int i = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                

                loadingDialog.show();

                if (dataSnapshot != null) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    int length = (int) dataSnapshot.getChildrenCount();
                    String[] sampleString = new String[length];
                    //
                    while (i < length) {
                        sampleString[i] = iterator.next().getValue().toString();
                        Log.d(Integer.toString(i), sampleString[i]);
                        i++;
                        //       Log.d("sub", subs);
                        subscriber_string = sampleString[0];
                        //if (Arrays.asList(sampleString).contains("subscribed"))
                        if (subscriber_string.equals("subscribed")) {

                                subscribebtn.setVisibility(View.INVISIBLE);
                                unsubscribebtn.setVisibility(View.VISIBLE);
                                loadingDialog.hide();
                                Log.e("IF","if callled");
                            } else {

                                subscribebtn.setVisibility(View.VISIBLE);
                                unsubscribebtn.setVisibility(View.INVISIBLE);
                                loadingDialog.hide();
                            Log.e("ELSE","if callled");
                            }




                    }

                } else {
                }


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


    }*/

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }



    @Override
    protected void onPause() {
        super.onPause();

        Log.e("pause", "pause called");
    }

    public void showProgressDialog() {
        loadingDialog.create(SubscribeActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Downloading data")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    private void getFollowersCount(){
        mFollowersCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_followers))
                .child(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found follower:" + singleSnapshot.getValue());
                    mFollowersCount++;
                }
                mServiceCount.setText(String.valueOf(mFollowersCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}

