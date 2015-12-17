package immutable;

import com.github.krukow.clj_ds.PersistentSet;
import com.github.krukow.clj_ds.PersistentSortedSet;
import com.github.krukow.clj_lang.PersistentTreeSet;
import fj.F2;

/**
 * Created by wayne on 15/12/17.
 */
public class FTreeSet<E> {
    final PersistentTreeSet<E> set;
    int recordHash;
    boolean calced;

    FTreeSet(PersistentTreeSet<E> set) {
        this.set = set;
        this.calced = false;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof FTreeSet && set.equals(((FTreeSet) obj).set));
    }

    @Override
    public int hashCode() {
        if (calced) {
            return recordHash;
        } else {
            calced = true;
            recordHash = set.hashCode();
            return recordHash;
        }
    }

    static public <E> FTreeSet<E> empty() {
        return new FTreeSet<>(PersistentTreeSet.EMPTY);
    }

    public FTreeSet<E> union(Iterable<E> iterable) {
        PersistentTreeSet<E> res = set;
        for (E elem : iterable) {
            res = res.cons(elem);
        }
        return new FTreeSet<>(res);
    }

    public <T> T foldLeft(F2<T, E, T> f, T init) {
        T res = init;
        for (E elem : set) {
            res = f.f(res, elem);
        }
        return res;
    }
}
