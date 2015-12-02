package immutable;

import com.github.krukow.clj_lang.PersistentHashSet;
import fj.F;
import fj.F2;
import fj.data.List;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by wayne on 15/11/30.
 */
public class FHashSet<E> implements Iterable<E> {
    final PersistentHashSet<E> set;
    int recordHash;
    boolean calced;

    FHashSet(PersistentHashSet<E> set) {
        this.set = set;
        this.calced = false;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof FHashSet && set.equals(((FHashSet) obj).set));
    }

    @Override
    public int hashCode() {
        if (calced) {
            return recordHash;
        } else {
            recordHash = set.hashCode();
            calced = true;
            return recordHash;
        }
    }

    static public <E> FHashSet<E> empty() {
        return new FHashSet<>(PersistentHashSet.emptySet());
    }

    static public <E> FHashSet<E> build(Iterable<E> iterable) {
        return new FHashSet<>(PersistentHashSet.create(iterable));
    }

    static public <E> FHashSet<E> build(E... elems) {
        return new FHashSet<>(PersistentHashSet.create(elems));
    }

    public FHashSet<E> insert(E elem) {
        return new FHashSet<>(set.cons(elem));
    }

    public FHashSet<E> delete(E elem) {
        return new FHashSet<>(set.disjoin(elem));
    }

    public FHashSet<E> union(FHashSet<E> that) {
        PersistentHashSet<E> res = set;
        for (E elem : that.set) {
            res = res.cons(elem);
        }
        return new FHashSet<>(res);
    }

    public FHashSet<E> minus(FHashSet<E> that) {
        PersistentHashSet<E> res = set;
        for (E elem : that.set) {
            res = res.disjoin(elem);
        }
        return new FHashSet<>(res);
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public boolean member(E elem) {
        return set.contains(elem);
    }

    public List<E> toList() {
       return List.list(set);
    }

    public FHashSet<E> filter(F<E, Boolean> pred) {
        ArrayList<E> list = new ArrayList<>();
        for (E elem : set) {
            if (pred.f(elem)) {
                list.add(elem);
            }
        }
        return FHashSet.build(list);
    }

    public Iterator<E> iterator() {
        return set.iterator();
    }

    public <T> FHashSet<T> map(F<E, T> f) {
        ArrayList<T> list = new ArrayList<>();
        for (E elem : set) {
            list.add(f.f(elem));
        }
        return FHashSet.build(list);
    }

    public <T> FHashSet<T> bind(F<E, FHashSet<T>> f) {
        FHashSet<T> res = FHashSet.empty();
        for (E elem : set) {
            res = res.union(f.f(elem));
        }
        return res;
    }

    public <T> T foldLeft(F2<T, E, T> f, T init) {
        T res = init;
        for (E elem : set) {
            res = f.f(res, elem);
        }
        return res;
    }

    public int size() {
        return set.size();
    }

    public FHashSet<E> intersect(FHashSet<E> that) {
        ArrayList<E> list = new ArrayList<>();
        for (E elem : set) {
            if (that.set.contains(elem)) {
                list.add(elem);
            }
        }
        return FHashSet.build(list);
    }

    public E head() {
        return set.iterator().next();
    }
}
