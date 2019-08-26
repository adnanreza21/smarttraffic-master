package aplikasiku.navwithdirectionlib.FDBMobel;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Zulfa_K on 29-05-2017.
 */

@IgnoreExtraProperties
public class Area {
    public String area_name;

    public Area(){}
    public Area(String area_name){
        this.area_name = area_name;
    }
}