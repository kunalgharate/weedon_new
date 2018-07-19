package info.mores.weedon.Adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import info.mores.weedon.ImageActivity;
import info.mores.weedon.Model.Messages;
import info.mores.weedon.R;
import info.mores.weedon.library.CoustomTextView;


/**
 * Created by anupamchugh on 09/02/16.
 */
public class MultiViewTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Messages> dataSet;
    Context mContext;
    int total_types;
    String service_id;
    MediaPlayer mPlayer;
    private boolean fabStateVolume = false;

    public static class TextTypeViewHolder extends RecyclerView.ViewHolder {


        CoustomTextView txtType;
        CoustomTextView txtTime;

        public TextTypeViewHolder(View itemView) {
            super(itemView);

            this.txtType = itemView.findViewById(R.id.message_text);
            this.txtTime = itemView.findViewById(R.id.time_text);
            //  this.cardView = (CardView) itemView.findViewById(R.id.card_view);

        }

    }

    public static class DateTypeViewHolder extends RecyclerView.ViewHolder {


        CoustomTextView mDateTxt;

        public DateTypeViewHolder(View itemView) {
            super(itemView);

            this.mDateTxt = itemView.findViewById(R.id.date_text);

        }

    }

    public static class DummyViewHolder extends RecyclerView.ViewHolder {


        CardView mDateTxt;

        public DummyViewHolder(View itemView) {
            super(itemView);

            this.mDateTxt = itemView.findViewById(R.id.date_single_layout);

        }

    }

    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder {


        CoustomTextView txtType;
        CoustomTextView txtTime;
        ImageView image;
        // ProgressBar mLoadImage;

        public ImageTypeViewHolder(View itemView) {
            super(itemView);

            this.txtTime = itemView.findViewById(R.id.time_text);
            // this.mLoadImage =  itemView.findViewById(R.id.imageLoad);
            // this.mLoadImage.bringToFront();
            this.image = (ImageView) itemView.findViewById(R.id.message_image_layout);

        }

    }


    public static class ImageMsgViewHolder extends RecyclerView.ViewHolder {


        CoustomTextView txtType;
        CoustomTextView txtTime;
        ImageView image;
        // ProgressBar mLoadImage;

        public ImageMsgViewHolder(View itemView) {
            super(itemView);

            this.txtType = itemView.findViewById(R.id.message_text);
            this.txtTime = itemView.findViewById(R.id.time_text);
            // this.mLoadImage =  itemView.findViewById(R.id.imageLoad);
            // this.mLoadImage.bringToFront();
            this.image = (ImageView) itemView.findViewById(R.id.message_image_layout);

        }

    }

   /* public static class AudioTypeViewHolder extends RecyclerView.ViewHolder {


        TextView txtType;
        FloatingActionButton fab;

        public AudioTypeViewHolder(View itemView) {
            super(itemView);

            this.txtType = (TextView) itemView.findViewById(R.id.type);
            this.fab = (FloatingActionButton) itemView.findViewById(R.id.fab);

        }

    }*/

    public MultiViewTypeAdapter(List<Messages> mMessageList, String serviceId, Context context) {
        this.dataSet = mMessageList;
        this.mContext = context;
        total_types = dataSet.size();
        this.dataSet = mMessageList;
        this.service_id = serviceId;
        this.mContext = context;
        total_types = dataSet.size();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case Messages.TEXT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
                return new TextTypeViewHolder(view);
            case Messages.IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_single_layout, parent, false);
                return new ImageTypeViewHolder(view);
            case Messages.IMAGE_MSG_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_message_single_layout, parent, false);
                return new ImageMsgViewHolder(view);
            case Messages.DATE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_single_layout, parent, false);
                return new DateTypeViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_dummy_layout, parent, false);
                return new DummyViewHolder(view);
        }

    }


    @Override
    public int getItemViewType(int position) {

        switch (dataSet.get(position).type) {
            case 0:
                return Messages.TEXT_TYPE;
            case 1:
                return Messages.IMAGE_TYPE;
            case 2:
                return Messages.DATE_TYPE;
            case 3:
                return Messages.IMAGE_MSG_TYPE;
            default:
                return -1;
        }


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {

        final Messages object = dataSet.get(listPosition);


        int pos = object.getMessage().indexOf('*');
        int endPos = object.getMessage().indexOf('*', pos + 1);

        Log.e("ORIGINAL START", String.valueOf(pos));
        Log.e("ORIGINAL END", String.valueOf(endPos));

        if (endPos >= object.getMessage().length()) {
            endPos = object.getMessage().length();
            Log.e("NEW END", String.valueOf(endPos));
            Log.e("NEWSTRING END", String.valueOf(object.getMessage().length()));

        }

        if (object != null) {

            switch (object.type) {

                case Messages.TEXT_TYPE:
                    Log.e("POSITION", "1st pos    " + pos + "   Last Pos" + endPos);
                    String newString = object.getMessage().replace("*", "");
                    if (pos >= 0 && newString != null && endPos >= newString.length()) {
                        endPos = newString.length();
                        final SpannableString out2 = new SpannableString(newString);
                        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                        out2.setSpan(boldSpan, pos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        ((TextTypeViewHolder) holder).txtType.setText(out2);
                        //   ((TextTypeViewHolder) holder).txtType.setText(object.getMessage());

                        ((TextTypeViewHolder) holder).txtType.setLinkTextColor(Color.parseColor("#007dff"));
                        Linkify.addLinks(((TextTypeViewHolder) holder).txtType, Linkify.ALL);

                        ((TextTypeViewHolder) holder).txtTime.setText(messageTime(object.getTime()));


                    }
                    if (pos >= 0 && newString != null && endPos <= newString.length()) {
                        // endPos =newString.length();
                        final SpannableString out2 = new SpannableString(newString);
                        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                        out2.setSpan(boldSpan, pos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        ((TextTypeViewHolder) holder).txtType.setText(out2);
                        //   ((TextTypeViewHolder) holder).txtType.setText(object.getMessage());

                        ((TextTypeViewHolder) holder).txtType.setLinkTextColor(Color.parseColor("#007dff"));
                        Linkify.addLinks(((TextTypeViewHolder) holder).txtType, Linkify.ALL);

                        ((TextTypeViewHolder) holder).txtTime.setText(messageTime(object.getTime()));


                    } else {
                        ((TextTypeViewHolder) holder).txtType.setText(object.getMessage());

                        ((TextTypeViewHolder) holder).txtType.setLinkTextColor(Color.parseColor("#007dff"));
                        Linkify.addLinks(((TextTypeViewHolder) holder).txtType, Linkify.ALL);

                        ((TextTypeViewHolder) holder).txtTime.setText(messageTime(object.getTime()));
                    }

                    ((TextTypeViewHolder) holder).txtType.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("text", object.getMessage());
                            clipboard.setPrimaryClip(clip);
                            Toast toast = Toast.makeText(mContext,
                                    "Text Copied", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                            return true;
                        }
                    });
                    break;
                case Messages.DATE_TYPE:

                 /*   if (listPosition % 2 == 1) {
                        holder.itemView.setBackgroundColor(Color.parseColor("#007dff"));
                        //  holder.imageView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    } else {
                        holder.itemView.setBackgroundColor(Color.parseColor("#03c959"));
                        //  holder.imageView.setBackgroundColor(Color.parseColor("#FFFAF8FD"));
                    }
*/

                    ((DateTypeViewHolder) holder).mDateTxt.setText(object.getMessage());
                    break;
                case Messages.IMAGE_TYPE:

                    // String newIString = object.getMessage().replace("*", "");

                   /* if (object.getMessage() != null)

                    {
                       // ((ImageTypeViewHolder) holder).txtType.setVisibility(View.VISIBLE);
                        if (pos >= 0 && newIString != null && endPos >= newIString.length()) {

                            Log.e("POSITION", "1st pos Image " + pos + "   Last Pos Image" + endPos);
                            endPos = newIString.length();
                            //    String newIString = object.getMessage().replace("*", "");
                            final SpannableString out2 = new SpannableString(newIString);
                            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                            out2.setSpan(boldSpan, pos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            ((ImageTypeViewHolder) holder).txtType.setText(out2);
                            //   ((TextTypeViewHolder) holder).txtType.setText(object.getMessage());

                            ((ImageTypeViewHolder) holder).txtType.setLinkTextColor(Color.parseColor("#007dff"));
                            Linkify.addLinks(((ImageTypeViewHolder) holder).txtType, Linkify.ALL);
                        }
                        if (pos >= 0 && newIString != null && endPos <= newIString.length()) {
                            Log.e("POSITION", "1st pos Image " + pos + "   Last Pos Image" + endPos);
                            final SpannableString out2 = new SpannableString(newIString);
                            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                            out2.setSpan(boldSpan, pos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            ((ImageTypeViewHolder) holder).txtType.setText(out2);
                            ((ImageTypeViewHolder) holder).txtType.setLinkTextColor(Color.parseColor("#007dff"));
                            Linkify.addLinks(((ImageTypeViewHolder) holder).txtType, Linkify.ALL);
                        } else if (object.getMessage() != null) {
                            ((ImageTypeViewHolder) holder).txtType.setText(object.getMessage());
                            ((ImageTypeViewHolder) holder).txtType.setLinkTextColor(Color.parseColor("#007dff"));
                            Linkify.addLinks(((ImageTypeViewHolder) holder).txtType, Linkify.ALL);
                        }
                    }
*/

                    ((ImageTypeViewHolder) holder).txtTime.setText(messageTime(object.getTime()));
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.ic_error)
                            .priority(Priority.HIGH)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);

                    Glide.with(mContext)
                            .load(object.getImage())
                            .apply(options)
                            .into(((ImageTypeViewHolder) holder).image);

                    // For Setting Progressbar
                       /*listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                    ((ImageTypeViewHolder) holder).mLoadImage.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    ((ImageTypeViewHolder) holder).mLoadImage.setVisibility(View.GONE);
                                    return false;
                                }
                           })*/


                    ((ImageTypeViewHolder) holder).image.setOnClickListener(new View.OnClickListener() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {

                            if (object.getImage() != null) {
                                Intent intent = new Intent(mContext, ImageActivity.class);
                                intent.putExtra("image_url", object.getImage());
                                ((ImageTypeViewHolder) holder).image.setTransitionName("thumbnailTransition");
                                Pair<View, String> pair1 = Pair.create((View) ((ImageTypeViewHolder) holder).image, ((ImageTypeViewHolder) holder).image.getTransitionName());
                                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, pair1);
                                mContext.startActivity(intent, optionsCompat.toBundle());
                            }
                        }
                    });

                    break;


                case Messages.IMAGE_MSG_TYPE:

                    String newIString = object.getMessage().replace("*", "");

                    if (object.getMessage() != null)

                    {
                        // ((ImageTypeViewHolder) holder).txtType.setVisibility(View.VISIBLE);
                        if (pos >= 0 && newIString != null && endPos >= newIString.length()) {

                            Log.e("POSITION", "1st pos Image " + pos + "   Last Pos Image" + endPos);
                            endPos = newIString.length();
                            //    String newIString = object.getMessage().replace("*", "");
                            final SpannableString out2 = new SpannableString(newIString);
                            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                            out2.setSpan(boldSpan, pos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            ((ImageMsgViewHolder) holder).txtType.setText(out2);
                            //   ((TextTypeViewHolder) holder).txtType.setText(object.getMessage());

                            ((ImageMsgViewHolder) holder).txtType.setLinkTextColor(Color.parseColor("#007dff"));
                            Linkify.addLinks(((ImageMsgViewHolder) holder).txtType, Linkify.ALL);
                        }
                        if (pos >= 0 && newIString != null && endPos <= newIString.length()) {
                            Log.e("POSITION", "1st pos Image " + pos + "   Last Pos Image" + endPos);
                            final SpannableString out2 = new SpannableString(newIString);
                            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                            out2.setSpan(boldSpan, pos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            ((ImageMsgViewHolder) holder).txtType.setText(out2);
                            ((ImageMsgViewHolder) holder).txtType.setLinkTextColor(Color.parseColor("#007dff"));
                            Linkify.addLinks(((ImageMsgViewHolder) holder).txtType, Linkify.ALL);
                        } else if (object.getMessage() != null) {
                            ((ImageMsgViewHolder) holder).txtType.setText(object.getMessage());
                            ((ImageMsgViewHolder) holder).txtType.setLinkTextColor(Color.parseColor("#007dff"));
                            Linkify.addLinks(((ImageMsgViewHolder) holder).txtType, Linkify.ALL);
                        }
                    }


                    ((ImageMsgViewHolder) holder).txtTime.setText(messageTime(object.getTime()));
                    options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.ic_error)
                            .priority(Priority.HIGH)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);

                    Glide.with(mContext)
                            .load(object.getImage())
                            .apply(options)
                            .into(((ImageMsgViewHolder) holder).image);

                    // For Setting Progressbar
                       /*listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                    ((ImageTypeViewHolder) holder).mLoadImage.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    ((ImageTypeViewHolder) holder).mLoadImage.setVisibility(View.GONE);
                                    return false;
                                }
                           })*/


                    ((ImageMsgViewHolder) holder).image.setOnClickListener(new View.OnClickListener() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {
                            if (object.getImage() != null) {
                                Intent intent = new Intent(mContext, ImageActivity.class);
                                intent.putExtra("image_url", object.getImage());
                                ((ImageMsgViewHolder) holder).image.setTransitionName("thumbnailTransition");
                                Pair<View, String> pair1 = Pair.create((View) ((ImageMsgViewHolder) holder).image, ((ImageMsgViewHolder) holder).image.getTransitionName());
                                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, pair1);
                                mContext.startActivity(intent, optionsCompat.toBundle());
                            }
                        }
                    });

                    break;
                default:

                    ((DummyViewHolder) holder).mDateTxt.setVisibility(View.GONE);

                    break;
               /* case Messages.AUDIO_TYPE:

                    ((AudioTypeViewHolder) holder).txtType.setText(object.text);


                    ((AudioTypeViewHolder) holder).fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (fabStateVolume) {
                                if (mPlayer.isPlaying()) {
                                    mPlayer.stop();

                                }
                                ((AudioTypeViewHolder) holder).fab.setImageResource(R.drawable.volume);
                                fabStateVolume = false;

                            } else {
                                mPlayer = MediaPlayer.create(mContext, R.raw.sound);
                                mPlayer.setLooping(true);
                                mPlayer.start();
                                ((AudioTypeViewHolder) holder).fab.setImageResource(R.drawable.mute);
                                fabStateVolume = true;

                            }
                        }
                    });



                   break;*/
            }

        }

    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public String messageTime(long i) {
        DateFormat currentTime = new SimpleDateFormat("h:mm:a");
        final String time = currentTime.format(i);
        return time;
    }

}
