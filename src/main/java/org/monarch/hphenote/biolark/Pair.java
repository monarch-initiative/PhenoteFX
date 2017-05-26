package org.monarch.hphenote.biolark;

/**
 * Created by robinp on 5/26/17.
 */
public class Pair<T extends Comparable<T>> implements Comparable<Pair<T>> {

    private T left;

    private T right;

    public Pair(T one, T two) {
        left=one;
        right=two;
    }

    public T getRight() {return right; }
    public T getLeft() { return left; }

    public boolean equals(Pair<T> other) {
        return (other.getLeft().equals(getLeft()) && other.getRight().equals(getRight()));
    }

    public int compareTo(Pair<T> other) {
        int cmp = this.getLeft().compareTo(other.getLeft());
        if (cmp==0) {
            return this.getRight().compareTo(other.getRight());
        } else {
            return cmp;
        }
    }
}
