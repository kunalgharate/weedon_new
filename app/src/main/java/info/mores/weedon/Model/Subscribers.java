package info.mores.weedon.Model;

/**
 * Created by Kunal Gharate on 15-12-2017.
 */

public class Subscribers {

    private static Subscribers subscribers = null;

    String date;
    public Subscribers() {
    }

    public Subscribers(String date) {
        this.date = date;
    }

    public static Subscribers getInstance() {

        if(subscribers==null)
        {
            subscribers = new Subscribers();
        }
        return subscribers;
    }

    public static Subscribers getInstance(String date) {

        if(subscribers==null)
        {
            subscribers = new Subscribers(date);
        }
        return subscribers;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
