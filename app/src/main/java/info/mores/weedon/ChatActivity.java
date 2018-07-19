package info.mores.weedon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import info.mores.weedon.Adapter.MultiViewTypeAdapter;
import info.mores.weedon.Model.Messages;


public class ChatActivity extends AppCompatActivity {

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private static final int GALLERY_PICK = 1;
    private final List<Messages> messagesList = new ArrayList<>();
    Toolbar ctoolbar;
    private String title;
    private String mNewtitle;
    private String mMessageTitle;
    // TextView idview;
    DatabaseReference ChatDatabaseRef;
    DatabaseReference mFeedbackDatabase;
    DatabaseReference mUserDatabase;
    RecyclerView messages_list;
    private SwipeRefreshLayout mRefreshLayout;
    private LinearLayoutManager mLinearLayout;
    private MultiViewTypeAdapter mAdapter; // data came from adapter if we check the count of adapter we xcan
    // handle it easily
    private int mCurrentPage = 1;
    //New Solution
    private int itemPos = 0;
    private DatabaseReference mRootRef;
    private String mLastKey = "";
    private String mPrevKey = "";
    private StorageReference mImageStorage;
    private EditText feedbackText;
    BottomSheetBehavior sheetBehavior;
    LinearLayout bottom_sheet;
    AdView adView;
    private FirebaseUser user;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

     //   overridePendingTransition(R.transition.trans_left_in,R.transition.trans_left_out);

        overridePendingTransition(R.transition.fade_in, R.transition.fade_out);


        ctoolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(ctoolbar);
        //getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ctoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        // idview = (TextView)findViewById(R.id.id_view);
        mLinearLayout = new LinearLayoutManager(this);
        messages_list = findViewById(R.id.messages_list);
        messages_list.setHasFixedSize(true);
        messages_list.setLayoutManager(mLinearLayout);
        messages_list.setItemViewCacheSize(20);
        messages_list.setDrawingCacheEnabled(true);
        messages_list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);




        //------- IMAGE STORAGE ---------
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.keepSynced(true);

        // All Intent Data

        final String service_id = getIntent().getStringExtra("service_id");
        String nData = getIntent().getStringExtra("from_user_id");
        title = getIntent().getStringExtra("chatname");
        mMessageTitle = getIntent().getStringExtra("msg_title");
        mNewtitle = getIntent().getStringExtra("toolbar_title");

        System.out.println("notificationid chat activity   " + nData);
        System.out.println("notificationtitle chat activity   " + mNewtitle);

        //Direct Notification Data

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        if (nData != null) {



            getSupportActionBar().setTitle(mNewtitle);

            ChatDatabaseRef = FirebaseDatabase.getInstance().getReference().child("chat_1").child(nData);
            ChatDatabaseRef.keepSynced(true);
            mFeedbackDatabase = FirebaseDatabase.getInstance().getReference().child("Feedback").child(mNewtitle);
            // Loading of messages ..
            mAdapter = new MultiViewTypeAdapter(messagesList, nData,ChatActivity.this);

            messages_list.setAdapter(mAdapter);
            loadMessages();

        }

        //App Opened and ChatActivity Data

        if (service_id != null) {

            getSupportActionBar().setTitle(title);
            ctoolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            ChatDatabaseRef = FirebaseDatabase.getInstance().getReference().child("chat_1").child(service_id);
            ChatDatabaseRef.keepSynced(true);
            mFeedbackDatabase = FirebaseDatabase.getInstance().getReference().child("Feedback").child(title);

            // Loading of messages ..
            mAdapter = new MultiViewTypeAdapter(messagesList, service_id,ChatActivity.this);

            messages_list.setAdapter(mAdapter);
            loadMessages();

        }


        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        mRefreshLayout = findViewById(R.id.message_swipe_layout);
        // messages_list.setAdapter(mAdapter);
        //  loadMessages();

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;
                itemPos = 0;
                Log.e("current page",""+mCurrentPage);
                loadMoreMessages();


            }
        });


    }


    // Send Feedback Dialog For sending reply
    public void sendFeedback(final String title, final String message, final BottomDialog dialog) {


        String userId =user.getUid();
        mUserDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("User_Name",dataSnapshot.child("name").getValue().toString());
                userMap.put("Message", message);


                mFeedbackDatabase.child(title).setValue(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(ChatActivity.this, "Thank you For Feedback. ", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }


    private void loadMessages() {
        Query messageQuery = ChatDatabaseRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
        messageQuery.keepSynced(true);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {//this function is called only when


                Messages message = dataSnapshot.getValue(Messages.class);
                Log.e("message ", "" + message.getTime());
                itemPos++;

                if (itemPos == 1) {

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }

                Log.e("check ",""+messagesList.contains(message));
                if(!messagesList.contains(message)) {
                    messagesList.add(message);
                }
                mAdapter.notifyDataSetChanged();
                messages_list.scrollToPosition(messagesList.size() - 1);
                mRefreshLayout.setRefreshing(false); //wait 1st time kahi item skip hota aahet
                //may i use internet YES
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }

    private void loadMoreMessages() {

      //  messagesList.clear();
        Query messageQuery = ChatDatabaseRef.orderByKey().endAt(mLastKey).limitToLast(TOTAL_ITEMS_TO_LOAD);
        messageQuery.keepSynced(true);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                Messages message = dataSnapshot.getValue(Messages.class);

                String messageKey = dataSnapshot.getKey();


                if (!mPrevKey.equals(messageKey)) {
                    Log.e("check loadmore mPrevK ",itemPos+" "+messagesList.contains(message));
                    if(!messagesList.contains(message)) {
                        Log.e("check added mPrevK ",itemPos+" ");
                        messagesList.add(itemPos++, message);
                    }
                } else {

                    mPrevKey = mLastKey;

                }


                if (itemPos == 1) {

                    mLastKey = messageKey;

                }

                Log.e("check loadmore",""+messagesList.contains(message));
                Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);
                /*if(!messagesList.contains(message)) {
                    Log.e("check added loadmore ",itemPos+" "+ message.getMessage());
                    messagesList.add(message);
                }*/
                mAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);

                //    mLinearLayout.scrollToPositionWithOffset(10, 0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.message_item) {

            View view = getLayoutInflater().inflate(R.layout.fragment_item_list_dialog, null, false);
            //Toast.makeText(ChatActivity.this, "Working pin Progresss...", Toast.LENGTH_SHORT).show();
            final BottomDialog bottomDialog = new BottomDialog.Builder(ChatActivity.this)
                    .setTitle("Feedback!")
                    .setContent("What can we improve? Your feedback is always welcome.")
                    .setCustomView(view)
                    .show();

            feedbackText = view.findViewById(R.id.feedback_edittext);


            Button b1 = view.findViewById(R.id.submit_feedback);
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String feedback = feedbackText.getText().toString();

                    if (TextUtils.isEmpty(feedbackText.getText().toString())) {
                        feedbackText.setError("Enter Message Here");
                    } else {
                        sendFeedback(title, feedback, bottomDialog);
                    }

                }
            });

        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.with(getApplicationContext()).pauseRequests();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ChatActivity.this,MainActivity.class));
      //  overridePendingTransition(R.transition.trans_right_in,R.transition.trans_right_out);
        overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
       // backToMain();
        finish();
    }

   /* public void backToMain()
    {
        Intent intent = new Intent(ChatActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }*/
}
