package compiler.util;

import java.util.HashMap;
import java.util.Map;

public class Maps {
    public static <A, K, V> Map<K, V> assocTable(List<A> list, Function<A, K> key, Function<A, V> value) {
        Map<K, V> map = new HashMap<>(list.length);
        for (A a : list) {
            map.put(key.apply(a), value.apply(a));
        }
        return map;
    }

    public static <K, V> Map<K, V> union(Map<K, V> m1, Map<K, V> m2) {
        Map<K, V> joined = new HashMap<>(m1.size() + m2.size());
        joined.putAll(m1);
        joined.putAll(m2);
        return joined;
    }

    public static <K, V> V getOrError(Map<K, V> map, K key, String message) {
        if (!map.containsKey(key)) {
            throw new IllegalArgumentException(message);
        } else {
            return map.get(key);
        }
    }
}
