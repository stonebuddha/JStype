package immutable;

import com.github.krukow.clj_lang.PersistentHashMap;
import fj.F;
import fj.P2;
import fj.data.Option;

import java.util.Map;
import java.util.Set;

/**
 * Created by wayne on 15/11/30.
 */
public class FHashMap<K, V> {
    final PersistentHashMap<K, V> map;

    FHashMap(PersistentHashMap<K, V> map) {
        this.map = map;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof FHashMap && map.equals(((FHashMap) obj).map));
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    static public <K, V> FHashMap<K, V> empty() {
        return new FHashMap<>(PersistentHashMap.emptyMap());
    }

    static public <K, V> FHashMap<K, V> map(Iterable<P2<K, V>> iterable) {
        PersistentHashMap<K, V> res = PersistentHashMap.emptyMap();
        for (P2<K, V> p : iterable) {
            res = (PersistentHashMap<K, V>)res.assoc(p._1(), p._2());
        }
        return new FHashMap<>(res);
    }

    static public <K, V> FHashMap<K, V> map(Object... init) {
        return new FHashMap<>(PersistentHashMap.create(init));
    }

    public Option<V> get(K key) {
        V val = map.valAt(key);
        if (val == null) {
            return Option.none();
        } else {
            return Option.some(val);
        }
    }

    public FHashMap<K, V> set(K key, V val) {
        return new FHashMap<>((PersistentHashMap<K, V>)map.assoc(key, val));
    }

    public FHashMap<K, V> union(Iterable<P2<K, V>> iterable) {
        PersistentHashMap<K, V> res = map;
        for (P2<K, V> p2 : iterable) {
            res = (PersistentHashMap<K, V>)res.assoc(p2._1(), p2._2());
        }
        return new FHashMap<>(res);
    }

    public FHashMap<K, V> union(FHashMap<K, V> that) {
        PersistentHashMap<K, V> res = map;
        for (Map.Entry<K, V> entry : that.map) {
            res = (PersistentHashMap<K, V>)res.assoc(entry.getKey(), entry.getValue());
        }
        return new FHashMap<>(res);
    }

    public FHashMap<K, V> filter(F<K, Boolean> pred) {
        PersistentHashMap<K, V> res = PersistentHashMap.emptyMap();
        for (Map.Entry<K, V> entry : map) {
            if (pred.f(entry.getKey())) {
                res = (PersistentHashMap<K, V>)res.assoc(entry.getKey(), entry.getValue());
            }
        }
        return new FHashMap<>(res);
    }

    public boolean contains(K key) {
        return map.containsKey(key);
    }

    public FHashMap<K, V> delete(K key) {
        return new FHashMap<>((PersistentHashMap<K, V>)map.minus(key));
    }

    public Set<K> keys() {
        return map.keySet();
    }
}
