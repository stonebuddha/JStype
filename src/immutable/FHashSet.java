package immutable;

import com.github.krukow.clj_lang.PersistentHashSet;
import fj.data.List;

/**
 * Created by wayne on 15/11/30.
 */
public class FHashSet<E> {
    final PersistentHashSet<E> set;

    FHashSet(PersistentHashSet<E> set) {
        this.set = set;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof FHashSet && set.equals(((FHashSet) obj).set));
    }

    @Override
    public int hashCode() {
        return set.hashCode();
    }

    static public <E> FHashSet<E> empty() {
        return new FHashSet<>(PersistentHashSet.emptySet());
    }

    static public <E> FHashSet<E> set(E... elems) {
        return new FHashSet<>(PersistentHashSet.create(elems));
    }

    static public <E> FHashSet<E>set(Iterable<E> iterable) {
        return new FHashSet<>(PersistentHashSet.create(iterable));
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
}
