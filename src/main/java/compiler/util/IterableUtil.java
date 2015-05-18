package compiler.util;

import java.util.Iterator;

public class IterableUtil {
    public static <A, B> Iterable<B> mapLazy(final Iterable<A> col, final Function<A, B> f) {
        return new Iterable<B>() {
            @Override
            public Iterator<B> iterator() {
                final Iterator<A> as = col.iterator();
                return new Iterator<B>() {
                    @Override
                    public boolean hasNext() {
                        return as.hasNext();
                    }
                    @Override
                    public B next() {
                        return f.apply(as.next());
                    }
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Remove in mapLazy iterator");
                    }
                };
            }
        };
    }
}
