package aplikasiku.navwithdirectionlib.FDBMobel;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Zulfa_K on 29-05-2017.
 */

@IgnoreExtraProperties
public class TLCondition {
    public String TLpos;
    public boolean TLstat;

    public TLCondition(){}
    public TLCondition(String tlpos, boolean tlstat){
        TLpos = tlpos;
        TLstat = tlstat;
    }
}
