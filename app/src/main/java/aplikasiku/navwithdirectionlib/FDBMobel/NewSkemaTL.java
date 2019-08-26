package aplikasiku.navwithdirectionlib.FDBMobel;

/**
 * Created by Zulfa_K on 20-06-2017.
 */

public class NewSkemaTL {
    public String TLname, TLloc;
    public int TLcount;
    public double TLlat, TLlong;
    public NewSkemaTLpos TLpos;
    public boolean TLstate;
    public boolean TLplaysound;

    public NewSkemaTL(){
        TLname = "";
        TLloc = "";
        TLcount = 0;
        TLlat = 0.0;
        TLlong = 0.0;
        TLpos = null;
        TLstate = false;
        TLplaysound = false;
    }
    public NewSkemaTL(String tlname, String tlloc, double tllat, double tllong, int tlcout, boolean tlstate, boolean tlplaysound, NewSkemaTLpos tlpos){
        TLname = tlname;
        TLloc = tlloc;
        TLlat = tllat;
        TLlong = tllong;
        TLcount = tlcout;
        TLpos=tlpos;
        TLstate = tlstate;
    }
}