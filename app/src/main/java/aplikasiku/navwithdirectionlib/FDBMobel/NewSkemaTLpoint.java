package aplikasiku.navwithdirectionlib.FDBMobel;

/**
 * Created by Zulfa_K on 20-06-2017.
 */

public class NewSkemaTLpoint {
    public boolean isEmergency;
    public boolean stat;
    public String pos;
    public NewSkemaRequest request;
    public String rlamp = "";
    public String ulamp = "";

    public NewSkemaTLpoint(){
        request = null;
        isEmergency = false;
        stat = false;
        pos = "";
        rlamp = "";
        ulamp = "";
    }
    public NewSkemaTLpoint(String pos, boolean isEmergency){
        request = new NewSkemaRequest();
        this.isEmergency = isEmergency;
        stat = true;
        this.pos = pos;
        rlamp = "green";
        ulamp = "green";
    }
}
