package aplikasiku.navwithdirectionlib.FDBMobel;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zulfa_K on 22-05-2017.
 */

@IgnoreExtraProperties
public class User {
    public String name,status;

    public User(){}
    public User(String name,String status){
        this.name = name;
        this.status = status;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("status", status);
        return result;
    }
}
