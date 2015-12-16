package immutable;

import com.github.krukow.clj_lang.PersistentHashMap;
import com.github.krukow.clj_lang.PersistentHashSet;
import fj.F;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Option;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by wayne on 15/11/30.
 */
public class FHashMap<K, V> implements Iterable<Map.Entry<K, V>> {
    final PersistentHashMap<K, V> map;
    int recordHash;
    boolean calced;

    FHashMap(PersistentHashMap<K, V> map) {
        this.map = map;
        this.calced = false;
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof FHashMap && map.equals(((FHashMap) obj).map));
    }

    @Override
    public int hashCode() {
        if (calced) {
            return recordHash;
        } else {
            recordHash = map.hashCode();
            calced = true;
            return recordHash;
        }
    }

    static public <K, V> FHashMap<K, V> empty() {
        return new FHashMap<>(PersistentHashMap.emptyMap());
    }

    static public <K, V> FHashMap<K, V> build(Iterable<P2<K, V>> iterable) {
        PersistentHashMap<K, V> res = PersistentHashMap.emptyMap();
        for (P2<K, V> p : iterable) {
            res = (PersistentHashMap<K, V>)res.assoc(p._1(), p._2());
        }
        return new FHashMap<>(res);
    }

    static public <K, V> FHashMap<K, V> build(Object... init) {
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
        ArrayList<P2<K, V>> list = new ArrayList<>();
        for (Map.Entry<K, V> entry : map) {
            if (pred.f(entry.getKey())) {
                list.add(P.p(entry.getKey(), entry.getValue()));
            }
        }
        return FHashMap.build(list);
    }

    public boolean contains(K key) {
        return map.containsKey(key);
    }

    public FHashMap<K, V> delete(K key) {
        return new FHashMap<>((PersistentHashMap<K, V>)map.minus(key));
    }

    public List<K> keys() {
        return List.list(map.keySet());
    }

    public List<V> values() {
        return List.list(map.values());
    }

    public Iterator<Map.Entry<K, V>> iterator() {
        return map.iterator();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public <T> FHashMap<K, T> map(F<V, T> f) {
        ArrayList<P2<K, T>> list = new ArrayList<>();
        for (Map.Entry<K, V> entry : map) {
            list.add(P.p(entry.getKey(), f.f(entry.getValue())));
        }
        return FHashMap.build(list);
    }
}
