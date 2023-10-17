package nbd.gV.old;

import nbd.gV.exceptions.RepositoryException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class OldRepository<T> {
    List<T> elements;

    public OldRepository(List<T> elements) {
        this.elements = elements;
    }

    public OldRepository() {
        this(new ArrayList<>());
    }

    public T get(int index) {
        if (index < elements.size()) {
            return elements.get(index);
        } else {
            throw new RepositoryException("Podano zly indeks!");
        }
    }

    public boolean add(T element) {
        if (element != null) {
            return elements.add(element);
        }
        return false;
    }

    public boolean remove(T element) {
        if (element != null) {
            return elements.remove(element);
        }
        return false;
    }

    public String report() {
        return "Repozytorium zawiera obecnie " + size() + " element/y/ow";
    }

    public int size() {
        return elements.size();
    }

    public List<T> find(Predicate<T> predicate) {
        List<T> found = new ArrayList<>();
        for (T t: elements) {
            if (predicate.test(t)) {
                found.add(t);
            }
        }

        return found;
    }

    public T findByUID(Predicate<T> predicate) {
        for (T t: elements) {
            if (predicate.test(t)) {
                return t;
            }
        }

        return null;
    }
}
