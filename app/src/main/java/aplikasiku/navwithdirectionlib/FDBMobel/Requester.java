package aplikasiku.navwithdirectionlib.FDBMobel;

/**
 * Created by Zulfa_K on 03-10-2017.
 */

public class Requester {
    //public String idUser; be firebase requester uid
    public String type;
    public int antrian;

    public Requester(){
        //this.idUser = "";
        this.type = "";
        this.antrian = 0;
    }
    public Requester(String type, int antrian){
        //this.idUser = idUser;
        this.type = type;
        this.antrian = antrian;
    }

}
