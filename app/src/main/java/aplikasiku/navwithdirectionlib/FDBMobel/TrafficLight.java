package aplikasiku.navwithdirectionlib.FDBMobel;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Zulfa_K on 29-05-2017.
 */

@IgnoreExtraProperties
public class TrafficLight {
    public String TLname, TLloc;
    public int TLcount;
    public double TLlat, TLlong;
    public boolean TLisEmergency;


    public TrafficLight(){}
    public TrafficLight(String tlname, String tlloc, double tllat, double tllong, int tlcout, boolean isEmergency){
        TLname = tlname;
        TLloc = tlloc;
        TLlat = tllat;
        TLlong = tllong;
        TLcount = tlcout;
        TLisEmergency = isEmergency;
    }
}