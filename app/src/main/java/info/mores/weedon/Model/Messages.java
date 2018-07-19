package info.mores.weedon.Model;

import android.text.StaticLayout;

/**
 * Created by Kunal Gharate on 20-12-2017.
 */

public class Messages {

    private static Messages messages=null;

    public static final int TEXT_TYPE = 0;
    public static final int IMAGE_TYPE = 1;
    public static final int IMAGE_MSG_TYPE = 3;
    public static final int DATE_TYPE = 2;

    public int type;
    private String message;
    private String image;
    private long time;
    private boolean seen;
    private String from;

    public Messages(String from) {
        this.from = from;
    }

    public Messages(String message,String image, int type, long time, boolean seen) {
        this.message = message;
        this.image = image;
        this.type = type;
        this.time = time;
        this.seen = seen;
    }

    public Messages() {

    }

    public static Messages getInstance()
    {
        if(messages==null)
        {
            messages=new Messages();
        }
        return messages;
    }

    public static Messages getInstance(String message,String image, int type, long time, boolean seen)
    {
        if(messages==null)
        {
            messages=new Messages(message,image,type,time,seen);
        }
        return messages;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}