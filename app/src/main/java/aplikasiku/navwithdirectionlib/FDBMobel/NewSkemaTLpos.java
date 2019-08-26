package aplikasiku.navwithdirectionlib.FDBMobel;

/**
 * Created by Zulfa_K on 20-06-2017.
 */

public class NewSkemaTLpos {
    public NewSkemaTLpoint TLu,TLs,TLt,TLb;

    public NewSkemaTLpos(){
        TLu = null;
        TLs = null;
        TLt = null;
        TLb = null;
    }
    public NewSkemaTLpos(NewSkemaTLpoint tlb, NewSkemaTLpoint tlt, NewSkemaTLpoint tlu, NewSkemaTLpoint tls){
        TLu = tlu;
        TLs = tls;
        TLt = tlt;
        TLb = tlb;
    }
}
