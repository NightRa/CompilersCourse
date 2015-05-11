package compiler.util;

import java.util.Iterator;

public abstract class List<A> implements Iterable<A> {
    public final int length;
    protected List(int length) {
        this.length = length;
    }
    public abstract boolean isEmpty();
    public abstract <B> List<B> map(Function<A, B> f);
    public abstract <B> List<B> flatMap(Function<A, List<B>> f);
    public abstract A head();
    public abstract List<A> tail();
    public abstract List<A> append(List<A> other);

    public static <A> List<A> nil() {
        return new List.Nil<>();
    }
    public static <A> List<A> cons(A head, List<A> tail) {
        return new List.Cons<>(head, tail);
    }
    public static <A> List<A> single(A head) {
        return List.cons(head, List.<A>nil());
    }

    public static int sum(Iterable<Integer> list) {
        int acc = 0;
        for (int a : list) {
            acc += a;
        }
        return acc;
    }

    public static int mult(Iterable<Integer> list) {
        int acc = 1;
        for (int a : list) {
            acc *= a;
        }
        return acc;
    }

    public static List<Integer> range(int from, int to) {
        if (from <= to) {
            return cons(from, range(from + 1, to));
        } else {
            return nil();
        }
    }

    public static <A, B, C> List<C> zipWith(List<A> as, List<B> bs, Function2<A, B, C> f) {
        if (as.isEmpty() || bs.isEmpty()) {
            return nil();
        } else {
            return cons(f.apply(as.head(), bs.head()), zipWith(as.tail(), bs.tail(), f));
        }
    }

    @SafeVarargs
    public static <A> List<A> list(A... as) {
        List<A> rev = nil();
        for (A a : as) {
            rev = cons(a, rev);
        }
        return reverse(rev);
    }
    public static <A> List<A> reverse(List<A> list) {
        List<A> acc = nil();
        while (!list.isEmpty()) {
            acc = cons(list.head(), acc);
            list = list.tail();
        }
        return acc;
    }

    public static final class Nil<A> extends List<A> {
        public Nil() {
            super(0);
        }
        public boolean isEmpty() {
            return true;
        }
        public <B> List<B> map(Function<A, B> f) {
            return nil();
        }
        public <B> List<B> flatMap(Function<A, List<B>> f) {
            return nil();
        }
        public A head() {
            throw new IllegalArgumentException("head() on empty list");
        }
        public List<A> tail() {
            throw new IllegalArgumentException("tail() on empty list");
        }
        public List<A> append(List<A> other) {
            return other;
        }

        public boolean equals(Object other) {
            return other instanceof Nil;
        }
        public int hashCode() {
            return 0;
        }
    }
    public static final class Cons<A> extends List<A> {
        public final A head;
        public final List<A> tail;

        public Cons(A head, List<A> tail) {
            super(tail.length + 1);
            this.head = head;
            this.tail = tail;
        }

        public boolean isEmpty() {
            return false;
        }
        public <B> List<B> map(Function<A, B> f) {
            return cons(f.apply(head), tail.map(f));
        }
        public <B> List<B> flatMap(Function<A, List<B>> f) {
            return f.apply(head).append(tail.flatMap(f));
        }
        public A head() {
            return head;
        }
        public List<A> tail() {
            return tail;
        }
        public List<A> append(List<A> other) {
            return cons(head, tail.append(other));
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cons<?> cons = (Cons<?>) o;
            return head.equals(cons.head) && tail.equals(cons.tail);

        }
        public int hashCode() {
            int result = head.hashCode();
            result = 31 * result + tail.hashCode();
            return result;
        }
    }

    public String toString() {
        return Strings.mkString("List(", ",", ")", this);
    }

    public Iterator<A> iterator() {
        return new Iterator<A>() {
            List<A> current = List.this;
            public boolean hasNext() {
                return !current.isEmpty();
            }
            public A next() {
                A value = current.head();
                current = current.tail();
                return value;
            }
        };
    }
}
