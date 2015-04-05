package compiler.util;

public class Tuple2<A,B> {
    public final A first;
    public final B second;

    public Tuple2(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A,B> Tuple2<A,B> pair(A a, B b){
        return new Tuple2<>(a,b);
    }
}
