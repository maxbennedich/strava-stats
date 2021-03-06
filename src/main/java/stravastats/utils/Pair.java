package stravastats.utils;

public class Pair<P, Q> {
    public final P p;
    public final Q q;

    public Pair(P p, Q q) {
        this.p = p;
        this.q = q;
    }

    @Override public String toString() {
        return "Pair [p=" + p + ", q=" + q + "]";
    }
}