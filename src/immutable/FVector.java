package immutable;

import com.github.krukow.clj_lang.PersistentVector;
import fj.F2;
import fj.data.List;
import fj.data.Seq;

import java.util.ArrayList;

/**
 * Created by wayne on 15/12/1.
 */
public class FVector<E> {
    //final PersistentVector<E> vector;
    final Seq<E> vector;
    int recordHash;
    boolean calced;

    FVector(Seq<E> vector) {
        this.vector = vector;
        this.calced = false;
    }

    @Override
    public String toString() {
        return vector.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof FVector && vector.equals(((FVector) obj).vector));
    }

    @Override
    public int hashCode() {
        if (calced) {
            return recordHash;
        } else {
            recordHash = vector.hashCode();
            calced = true;
            return recordHash;
        }
    }

    static public <E> FVector<E> empty() {
        return new FVector<>(Seq.empty());
        //return new FVector<>(PersistentVector.emptyVector());
    }

    static public <E> FVector<E> build(int n, E elem) {
        ArrayList<E> list = new ArrayList<>();
        for (int i = 0; i < n; i += 1) {
            list.add(elem);
        }
        return new FVector<>(Seq.seq(List.list(list)));
        //return new FVector<>(PersistentVector.create(list));
    }

    static public <E> FVector<E> build(Iterable<E> iterable) {
        return new FVector<>(Seq.seq(List.list(iterable)));
        //return new FVector<>(PersistentVector.create(iterable));
    }

    public E index(int i) {
        return vector.index(i);
        //return vector.nth(i);
    }

    public FVector<E> update(int i, E elem) {
        return new FVector<>(vector.update(i, elem));
        //PersistentVector<E> res = vector.assocN(i, elem);
        //return new FVector<>(res);
    }

    public int length() {
        return vector.length();
    }

    public <T> T foldLeft(F2<T, E, T> f, T init) {
        T res = init;
        for (E elem : vector) {
            res = f.f(res, elem);
        }
        return res;
    }
}
