package info.mores.weedon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import info.mores.weedon.Adapter.MultiViewTypeAdapter;
import info.mores.weedon.Util.Utility;
import info.mores.weedon.library.ZoomableImageView;

public class ImageActivity extends AppCompatActivity {

    Toolbar iToolbar;
    String imageUrl;
    ZoomageView saveImageView;
    File image;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
     //   getWindow().requestFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_image);
        iToolbar = findViewById(R.id.image_appbar);
        setSupportActionBar(iToolbar);
        iToolbar.setBackgroundColor(Color.TRANSPARENT);
      //  getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        iToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Window window = this.getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
        saveImageView = findViewById(R.id.imageSet);

        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transation));
        saveImageView.setTransitionName("thumbnailTransition");

        imageUrl = getIntent().getStringExtra("image_url");

      //  Log.e(":LOG", imageUrl);

      /*  Picasso picasso = Picasso.with(this);
        picasso.setIndicatorsEnabled(false);
        Picasso.with(this).load(imageUrl)
                .placeholder(R.drawable.loader).into(setImage);*/

        RequestOptions options = new RequestOptions()
                .fitCenter()
                .error(R.drawable.ic_error)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(this)
                .load(imageUrl)
                .apply(options)
                .into(saveImageView);


        saveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // if (getSupportActionBar().isShowing())
                //{
                    getSupportActionBar().hide();
              /*  }
                else
                {
                    getSupportActionBar().show();
                }*/
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.download_item) {


           SaveImage();
        }

        if (item.getItemId() == R.id.share_item)

            if (Utility.checkWritePermission(this)) {
                SaveImage();
                {
                    if (image.exists()) {
                        shareImage();
                    }
                }
            }
            return true;


    }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBackPressed () {
            super.onBackPressed();
            overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
            finish();
        }


        Boolean shareImage()
        {


            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
           // share.putExtra(Intent.EXTRA_STREAM,imageUrl);
            share.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(image));
            share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share, getString(R.string.share)));

            return true ;
        }


        public void SaveImage()
        {
            if (Utility.checkWritePermission(this)) {

                File root = new File(Environment.getExternalStorageDirectory() + File.separator + "WeedON/Images");
                if (!root.exists()) {
                    root.mkdirs();
                }

                //   Toast.makeText(this,imageUrl, Toast.LENGTH_SHORT).show();

                BitmapDrawable drawable = (BitmapDrawable) saveImageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();


                //  File sdCardDirectory = Environment.getExternalStorageDirectory();
                image = new File(root, "WeedON_IMG"+System.currentTimeMillis()+".png");

                boolean success = false;

                // Encode the file as a PNG image.
                FileOutputStream outStream;
                try {

                    outStream = new FileOutputStream(image);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    /* 100 to keep full quality of the image */

                    outStream.flush();
                    outStream.close();
                    success = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (success) {

                    Toast toast = Toast.makeText(getApplicationContext(),"Image Saved .", Toast.LENGTH_SHORT);
//the default toast view group is a relativelayout
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toastTV.setTypeface(Typeface.create("nunito", Typeface.NORMAL));;
                    toastTV.setTextSize(20);
                    toast.show();
                 /*   Toast.makeText(getApplicationContext(), "Image saved with success",
                            Toast.LENGTH_LONG).show();*/
                }

                else {
                    Toast.makeText(getApplicationContext(),
                            "Error during image saving", Toast.LENGTH_LONG).show();
                }


            }
        }
    }

