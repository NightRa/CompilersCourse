package compiler.util;

public abstract class Function<A,B> {
    public abstract B apply(A value);

    public static <A> Function<A, A> identity() {
        return new Function<A, A>() {
            @Override
            public A apply(A value) {
                return value;
            }
        };
    }
}
