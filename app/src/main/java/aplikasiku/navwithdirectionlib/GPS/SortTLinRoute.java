package aplikasiku.navwithdirectionlib.GPS;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

/**
 * Created by muiezhimura on 10/9/17.
 */

public class SortTLinRoute {
    LatLng Start;
    ArrayList<LatLngLoc> TLInRoute;
    ArrayList<LatLngLoc> SortTLInRoute;

    public SortTLinRoute(LatLng start, ArrayList<LatLngLoc> TLInRoute) {
        Start = start;
        this.TLInRoute = TLInRoute;
    }


    public ArrayList<LatLngLoc> getSortTLInRoute() {
        ArrayList<LatLngLoc> TempArray = new ArrayList<LatLngLoc>();
        LatLngLoc temp;

        TempArray = TLInRoute;
        if (TempArray.size()>1) {
            for (int i = 0; i < TempArray.size() - 1; i++) {
                for (int j = i +1; j < TempArray.size(); j++) {

                    if (SphericalUtil.computeDistanceBetween(Start, TempArray.get(i).getLocation()) > SphericalUtil.computeDistanceBetween(Start, TempArray.get(j).getLocation())) {
                        temp = TempArray.get(i);
                        TempArray.set(i, TempArray.get(j));
                        TempArray.set(j, temp);
                    }
                }
            }
        }
        return TempArray;
    }
}
