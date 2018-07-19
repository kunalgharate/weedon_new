package info.mores.weedon;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;
import info.mores.weedon.Model.ServicePojo;


public class AllServicesActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    private RecyclerView mServicesList;

    private DatabaseReference mServicesDatabase;
    FirebaseRecyclerAdapter adapter;
    Context mContext = AllServicesActivity.this;


    //  private LinearLayoutManager mLayoutManager;
    private GridLayoutManager gridLayoutManager;
    AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_services);



        mToolbar = findViewById(R.id.allservices_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Follow Topics");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      /*  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String uid = user.getUid();*/
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mServicesDatabase = FirebaseDatabase.getInstance().getReference().child("all_services");
        // mLayoutManager = new LinearLayoutManager(this);

        gridLayoutManager = new GridLayoutManager(this,2);


        mServicesList = findViewById(R.id.allservices_list);
        mServicesList.setHasFixedSize(true);
        mServicesList.setLayoutManager(gridLayoutManager);
        mServicesList.setItemViewCacheSize(20);
        mServicesList.setDrawingCacheEnabled(true);
        mServicesList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // progress bar Code

    //    MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
        adView=findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    public void animateIntent(View view) {

        // Ordinary Intent for launching a new activity
        Intent intent = new Intent(this, SubscribeActivity.class);

        // Get the transition name from the string
        String transitionName = getString(R.string.transition_string);

        // Define the view that the animation will start from
        View viewStart = findViewById(R.id.card_view);

        ActivityOptionsCompat options =

                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        viewStart,   // Starting view
                        transitionName    // The String
                );
        //Start the Intent
        ActivityCompat.startActivity(this, intent, options.toBundle());

    }

    @Override
     protected void onStart() {
         super.onStart();

        FirebaseRecyclerOptions<ServicePojo> options =
                new FirebaseRecyclerOptions.Builder<ServicePojo>()
                        .setQuery(mServicesDatabase.orderByChild("service_name"), ServicePojo.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<ServicePojo,ServicesViewHolder>(options) {
            @NonNull
            @Override
            public ServicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service_item2, parent,false);

                return new ServicesViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ServicesViewHolder holder, int position, @NonNull ServicePojo model) {

                final String user_id = getRef(position).getKey();
                final String title = model.getName();

                //   servicesViewHolder.setDisplayName("Lets Up");
                holder.setDisplayName(model.getName());
                //  Toast.makeText(getApplicationContext(),serModel.getName().toString(),Toast.LENGTH_LONG).show();
                //   servicesViewHolder.setUserStatus(smodel.getImage());
               // holder.setUserImage(model.getImage(), getApplicationContext());

                RequestOptions options =  new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.ic_error)
                        .priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);

                Glide.with(mContext)
                        .load(model.getImage())
                        .apply(options)
                        .into(holder.serviceImageView);


                //  final String user_id = getRef(position).getKey();

                holder.card_view.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View view) {


                        Intent subsIntent = new Intent(AllServicesActivity.this, SubscribeActivity.class);
                        subsIntent.putExtra("user_id", user_id);
                        subsIntent.putExtra("title", title);
                        overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
                        //  subsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // subsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    /*    Pair[]  pairs = new Pair[2];
                        pairs[0] = new Pair<View,String>(holder.serviceImageView,"imageshared");
                        pairs[1] = new Pair<View,String>(holder.userNameView,"nameshared");
*/
                     //   ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AllServicesActivity.this,pairs);

                        animateIntent(holder.card_view);


                        Log.e("userid", user_id);
                        subsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(subsIntent);

                        // Toast.makeText(getApplicationContext(),"Working",Toast.LENGTH_LONG).show();

                        //  Intent profileIntent = new Intent(AllServicesActivity.this, .class);
                        //   profileIntent.putExtra("user_id", user_id);
                        //   startActivity(profileIntent);

                    }
                });

            }

           /*public void onDataChanged() {

                TextView mEmptyListMessage = findViewById(R.id.mEmptyListMessage);
                // If there are no chat messages, show a view that invites the user to add a message.
               mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }*/
        };

        adapter.startListening();

        adapter.notifyDataSetChanged();
        mServicesList.setAdapter(adapter);



    }

    public static class ServicesViewHolder extends RecyclerView.ViewHolder {

      /*  View mView;
        LinearLayout item_linear;
        CardView card_view;
        TextView userNameView;
        CircleImageView serviceImageView;*/

        View mView;
        RelativeLayout item_linear;
        TextView userNameView;
        ImageView serviceImageView;
        CardView card_view;

        public ServicesViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
           item_linear = mView.findViewById(R.id.itemlinear);
         card_view = mView.findViewById(R.id.card_view);
            serviceImageView = mView.findViewById(R.id.service_photo);
             userNameView = mView.findViewById(R.id.service_single_name);
        }


        public void setDisplayName(String name) {

            userNameView.setText(name);

        }

       /* public void setUserStatus(String status){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);


        }*/

     /*   public void setUserImage(String thumb_image, Context ctx) {


         *//*   GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(serviceImageView);
            Glide.with(ctx)
                    .load(thumb_image)
                    // .placeholder(R.drawable.common_google_signin_btn_icon_dark)
                    .into(imageViewTarget);*//*


            // Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.bg_card).into(serviceImageView);

           // Glide.with(ctx).load(thumb_image).into(serviceImageView);


        }
*/

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AllServicesActivity.this,MainActivity.class));
        overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
    }
}
