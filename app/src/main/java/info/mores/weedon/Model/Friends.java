package info.mores.weedon.Model;

/**
 * Created by Kunal Gharate on 16-12-2017.
 */

public class Friends {

    private static Friends friends = null;

    public String my_string;

    public Friends(){

    }

    public static Friends getInstance()
    {
        if(friends==null)
        {
            friends=new Friends();
        }
        return friends;
    }

    public static Friends getInstance(String date)
    {
        if(friends==null)
        {
            friends=new Friends(date);
        }
        return friends;
    }



    public Friends(String date) {
        this.my_string = my_string;
    }

    public String getDate() {
        return my_string;
    }

    public void setDate(String date) {
        this.my_string = date;
    }
}