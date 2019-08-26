package aplikasiku.navwithdirectionlib.FDBMobel;

/**
 * Created by Zulfa_K on 28-08-2017.
 */

public class Route {
    public String driver;
    public String route_list;
    public LogPerjalanan log;

    public Route(){}
    public Route(String driver, String route_list, LogPerjalanan log){
        this.driver = driver;
        this.route_list = route_list;
        this.log = log;
    }
}
