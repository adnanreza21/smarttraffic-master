package aplikasiku.navwithdirectionlib.FDBMobel;

/**
 * Created by Zulfa_K on 20-06-2017.
 */

public class NewSkemaRequest {
    public boolean adaRequest;
    public double jarakAcuan;
    public long useNumber;
    //public Requester perequest; push in firebase

    public NewSkemaRequest(){
        adaRequest = false;
        jarakAcuan = 0;
        useNumber = 0;
    }
    public NewSkemaRequest(boolean adaRequest, long jrk, long useNumber){
        this.adaRequest = adaRequest;
        jarakAcuan = jrk;
        this.useNumber = useNumber;
        //perequest = new Requester();
    }
}
