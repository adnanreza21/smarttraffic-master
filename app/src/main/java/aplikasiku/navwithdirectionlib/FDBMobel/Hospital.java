package aplikasiku.navwithdirectionlib.FDBMobel;

/**
 * Created by Zulfa_K on 23-04-2017.
 */
public class Hospital {
    public String RsName,RsLoc,RsImageUrl;
    public double RsLat, RsLong;

    public Hospital(){}
    public Hospital(String rsname, String rsimageurl, String rsloc, double rslat, double rslong){
        RsName = rsname;
        RsLoc = rsloc;
        RsImageUrl = rsimageurl;
        RsLat = rslat;
        RsLong = rslong;
    }
}
