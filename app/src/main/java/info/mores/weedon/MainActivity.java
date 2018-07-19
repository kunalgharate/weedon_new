package info.mores.weedon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import info.mores.weedon.Model.Friends;
import info.mores.weedon.Model.Messages;
import info.mores.weedon.Model.ServicePojo;
import info.mores.weedon.Util.GetTimeAgo;


public class MainActivity extends AppCompatActivity {
    private static final String SHOWCASE_ID = "simple example";
    RecyclerView mSubsList;
    DatabaseReference mFriendsDatabase;
    DatabaseReference mAllServicesDB;
    DatabaseReference mUsersDatabase;
    private DatabaseReference mMessageDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private TextView emptyView;
    private ProgressDialog loadProgress;
    FloatingActionButton fab;
    private iOSDialog mLogoutdialog;
    private FirebaseRecyclerAdapter adapter;
    AdView adView;
    private static Context context;
    List<ServicePojo> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("WeedON");
        mAuth = FirebaseAuth.getInstance();
        mLogoutdialog = new iOSDialog(this, "Logout", "Are you sure you want to logout ?", true, null, true);
        mSubsList = findViewById(R.id.subscribes_list);
        mSubsList.setHasFixedSize(true);
        mSubsList.setItemViewCacheSize(20);
        mSubsList.setDrawingCacheEnabled(true);
        mSubsList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        context = MainActivity.this;

        mSubsList.setHasFixedSize(true);
        mSubsList.setLayoutManager(linearLayoutManager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        // presentShowcaseView(1000);
        //  emptyView= findViewById(R.id.empty);
        String data = getIntent().getStringExtra("service_id");

        //  Log.d("TAG",data);

        // Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
     /*   no_subscription = findViewById(R.id.no_subscription);
        no_subscription.setVisibility(View.INVISIBLE);
*/
        if (mAuth.getCurrentUser() != null) {
            mCurrent_user_id = mAuth.getCurrentUser().getUid();
            mAllServicesDB = FirebaseDatabase.getInstance().getReference().child("all_services");
            mAllServicesDB.keepSynced(true);
            mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_following)).child(mCurrent_user_id);
            mFriendsDatabase.keepSynced(true);
            mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
            mUsersDatabase.keepSynced(true);
            mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("chat_1");
            mMessageDatabase.keepSynced(true);
        }






     /*else
     {
         startActivity(new Intent(MainActivity.this, LoginActivity.class));
     }*/

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(MainActivity.this, AllServicesActivity.class);
                startActivity(settingsIntent);
                overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
            }
        });


        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }


    private void signOut() {
        mAuth.signOut();
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            sendToStart();

        } else {
            // Query conversationQuery = mMessageDatabase.orderByChild("time");

            FirebaseRecyclerOptions<Friends> options =
                    new FirebaseRecyclerOptions.Builder<Friends>()
                            .setQuery(mFriendsDatabase.orderByChild("user_id"), Friends.class)
                            .build();

            adapter = new FirebaseRecyclerAdapter<Friends, MainViewHolder>(options) {
                @NonNull
                @Override
                public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service_layout, parent, false);

                    return new MainViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull final MainViewHolder holder, int position, @NonNull Friends model) {

                    final String list_service_id = getRef(position).getKey().toString();


                    Query lastMessageQuery = mMessageDatabase.child(list_service_id).limitToLast(1);

                    lastMessageQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            Messages message = dataSnapshot.getValue(Messages.class);

                            //Log.e("message ", "" + message.getMessage());


                            Long time = message.getTime();


                            holder.setMessage(message.getMessage());
                            holder.setTime(message.getTime());


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

                    mAllServicesDB.child(list_service_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                                final String serviceName = dataSnapshot.child("service_name").getValue().toString();
                                String thumbImage = dataSnapshot.child("service_thumbImage").getValue().toString();
                             /*   String topic_id = dataSnapshot.getKey();
                                String topic_id2 = list_service_id;*/
                                FirebaseMessaging.getInstance().subscribeToTopic(list_service_id);

                                holder.setDisplayName(serviceName);
                                holder.setUserImage(thumbImage,context);

                                if (holder.mView!= null) {

                                    holder.setOnClickListener(new MainViewHolder.ClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {

                                            {
                                                Intent msgIntent = new Intent(MainActivity.this, ChatActivity.class);
                                                msgIntent.putExtra("chatname", serviceName);
                                                msgIntent.putExtra("service_id", list_service_id);
                                                overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
                                                startActivity(msgIntent);

                                            }
                                        }



                                   /*     @Override
                                    public void onItemLongClick(View view, int position) {

                                        Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();

                                    }*/
                                    });
                                }


                            }


                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });


                }

                public void onDataChanged() {

                    TextView mEmptyListMessage = findViewById(R.id.mEmptyListMessage);
                    // If there are no chat messages, show a view that invites the user to add a message.
                    mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
                }


            };


            adapter.startListening();
            adapter.notifyDataSetChanged();
            mSubsList.setAdapter(adapter);


        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId())
        {
            case  R.id.main_logout_btn:

                mLogoutdialog.setPositive("Yes", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        signOut();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                });
                mLogoutdialog.setNegative("Cancel", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                    }
                });
                mLogoutdialog.show();

                break;

            case  R.id.main_all_btn:
                Intent settingsIntent = new Intent(MainActivity.this, AllServicesActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.terms_btn:
                termsAndConditions();
                break;
            case R.id.btn_Policy:
                policiesIntent();
                break;
            case R.id.share_btn:
                shareApp();
                break;
        }

        return true;
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        protected ClickListener mClickListener;
        View mView;

        public MainViewHolder(final View itemView) {
            super(itemView);

            mView = itemView;
         /*   itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mClickListener.onItemLongClick(v, getAdapterPosition());
                    return true;
                }
            });*/


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mView!=null) {
                        mClickListener.onItemClick(v, getAdapterPosition());
                    }

                }
            });
        }

        public void setMessage(String message) {

            TextView mLastMessage = mView.findViewById(R.id.user_single_msg);
            String messageReplace = message.replace("*", "");
            mLastMessage.setText(messageReplace);

         /*   if(!isSeen){
                mLastMessage.setTypeface(mLastMessage.getTypeface(), Typeface.BOLD);
            } else {
                mLastMessage.setTypeface(mLastMessage.getTypeface(), Typeface.NORMAL);
            }*/

        }

        public void setTime(Long time) {

            TextView timeTxt = mView.findViewById(R.id.time_txt);

            GetTimeAgo getTimeAgo = new GetTimeAgo();


            String lastSeenTime = getTimeAgo.getTimeAgo(time, context);

            timeTxt.setText(lastSeenTime);

         /*   if(!isSeen){
                mLastMessage.setTypeface(mLastMessage.getTypeface(), Typeface.BOLD);
            } else {
                mLastMessage.setTypeface(mLastMessage.getTypeface(), Typeface.NORMAL);
            }*/

        }

        public void setOnClickListener(ClickListener clickListener) {
            mClickListener = clickListener;
        }

        public void setDisplayName(String name) {

            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }


        public void setUserImage(String thumb_image, Context ctx) {

            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);

            //Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.bg_card).into(userImageView);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .error(R.drawable.ic_error)
                    .priority(Priority.IMMEDIATE)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

            Glide.with(ctx)
                    .load(thumb_image)
                    .apply(options)
                    .into(userImageView);

        }

       /* public void setUserStatus(String status){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);


        }*/

        //Interface to send callbacks...
        public interface ClickListener {
            void onItemClick(View view, int position);

            // void onItemLongClick(View view, int position);
        }


    }


    /*  private void presentShowcaseView(int withDelay) {
          new MaterialShowcaseView.Builder(this)
                  .setTarget(fab)
                  .setTitleText("Welcome to WeedON")
                  .setDismissText("GOT IT")
                  .setDismissOnTargetTouch(true)
                  .setContentText("Please select service to Get the messages")
                  .setDelay(withDelay) // optional but starting animations immediately in onCreate can make them choppy
                  .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
  //                .useFadeAnimation() // remove comment if you want to use fade animations for Lollipop & up
                  .show();
      }
  */
    @Override
    public void onBackPressed() {
        mLogoutdialog.dismiss();
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(startIntent);
        finish();

    }

    private void termsAndConditions() {
        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms)));
        startActivity(browser);
    }

    private void policiesIntent() {
        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.policies)));
        startActivity(browser);
    }

    private void shareApp()
    {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "WeedON");
            String sAux = "\n Download the new WeedON app and get latest jobs,news,information... \n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=info.mores.weedon \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }


    /*public void loadData()
    {

        mFriendsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Data1",dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       *//* mAllServicesDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                Log.e("data",dataSnapshot.toString());
              *//**//* ArrayList list = new ArrayList<ServicePojo>();
                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                    Log.e("Data",dataSnapshot.toString());
                    ServicePojo value = dataSnapshot1.getValue(ServicePojo.class);
                    ServicePojo fire = new ServicePojo();
                    String name = value.getName();
                    String thumb_image = value.getThumb_image();
                    fire.setName(name);
                    fire.setName(thumb_image);
                    list.add(fire);

                }*//**//*

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });
*//*
    }*/

}
