package compiler.util;

import java.util.HashMap;
import java.util.Map;

public class Maps {
    public static <K, V> Map<K, V> assocTable(List<V> list, Function<V, K> by) {
        Map<K, V> map = new HashMap<>(list.length);
        for (V value : list) {
            map.put(by.apply(value), value);
        }
        return map;
    }
}
