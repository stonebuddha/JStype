package immutable;

import com.github.krukow.clj_lang.PersistentVector;

import java.util.ArrayList;

/**
 * Created by wayne on 15/12/1.
 */
public class FVector<E> {
    final PersistentVector<E> vector;

    FVector(PersistentVector<E> vector) {
        this.vector = vector;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof FVector && vector.equals(((FVector) obj).vector));
    }

    @Override
    public int hashCode() {
        return vector.hashCode();
    }

    static public <E> FVector<E> vector(int n, E elem) {
        ArrayList<E> list = new ArrayList<>();
        for (int i = 0; i < n; i += 1) {
            list.add(elem);
        }
        return new FVector<>(PersistentVector.create(list));
    }

    public E index(int i) {
        return vector.nth(i);
    }

    public FVector<E> update(int i, E elem) {
        PersistentVector<E> res = vector.assocN(i, elem);
        return new FVector<>(res);
    }
}
