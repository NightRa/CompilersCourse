package compilers.util;

public abstract class List<A> {
    public abstract boolean isEmpty();

    public static <A> List<A> nil() {
        return new List.Nil<>();
    }

    public static <A> List<A> cons(A head, List<A> tail){
        return new List.Cons<>(head,tail);
    }

    public static final class Nil<A> extends List<A> {
        public boolean isEmpty() {
            return true;
        }
    }
    public static final class Cons<A> extends List<A>{
        public final A head;
        public final List<A> tail;

        public Cons(A head, List<A> tail) {
            this.head = head;
            this.tail = tail;
        }

        public boolean isEmpty() {
            return false;
        }
    }


}
