package aplikasiku.navwithdirectionlib.GPS;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by muiezhimura on 10/9/17.
 */

public class LatLngLoc {
    LatLng Location;
    String NameLoc;
    String TLkey;

    public LatLngLoc(LatLng location, String nameLoc) {
        Location = location;
        NameLoc = nameLoc;
    }

    public LatLngLoc(LatLng location, String nameLoc, String tlkey) {
        Location = location;
        NameLoc = nameLoc;
        TLkey = tlkey;
    }

    public LatLng getLocation() {
        return Location;
    }

    public String getNameLoc() {
        return NameLoc;
    }

    public String getTLkey(){
        return TLkey;
    }
}
