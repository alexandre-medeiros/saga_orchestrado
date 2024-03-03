import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class MapDebug {

    public static void main(String[] args) {
//        Map<String, Integer> map =Collections.synchronizedMap(new HashMap<>());
        Map<String, Integer> map =new HashMap<>();
        map.put("aaa",1);
        map.put("bbb",2);
        map.put("ccc",3);
        map.put("ddd",4);
        map.put("FB",5);
        map.put("Ea",6);
        map.put(null,7);


    }

}
