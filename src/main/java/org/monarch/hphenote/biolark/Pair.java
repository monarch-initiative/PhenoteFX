package org.monarch.hphenote.biolark;

/**
 * Created by robinp on 5/26/17.
 */
public class Pair implements Comparable<Pair> {

    private Integer left;

    private Integer right;

    public Pair(Integer one, Integer two) {
        left=one;
        right=two;
    }

    public Integer getRight() {return right; }
    public Integer getLeft() { return left; }

    @Override public boolean equals(Object o) {
        if (! (o instanceof Pair) ) return false;
        Pair other = (Pair) o;
        return (other.getLeft().equals(getLeft()) && other.getRight().equals(getRight()));
    }

    @Override public int compareTo(Pair other) {
        int cmp = this.getLeft().compareTo(other.getLeft());
        if (cmp==0) {
            return this.getRight().compareTo(other.getRight());
        } else {
            return cmp;
        }
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right== null) ? 0 : right.hashCode());
        return result;
    }
}
