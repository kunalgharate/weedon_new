package info.mores.weedon.Model;

/**
 * Created by Kunal Gharate on 25-12-2017.
 */

class CountryCode {

    private static CountryCode countryCode=null;
    String cName;
    String code;



    public CountryCode() {
    }

    public CountryCode(String cName, String code) {
        this.cName = cName;
        this.code = code;
    }

    public static CountryCode getInstance()
    {
        if(countryCode==null)
        {
            countryCode=new CountryCode();
        }
        return countryCode;
    }

    public static CountryCode getInstance(String cName, String code)
    {
        if(countryCode==null)
        {
            countryCode=new CountryCode(cName,code);
        }
        return countryCode;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
