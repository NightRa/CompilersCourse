package compiler.util;

public abstract class List<A> {
    public abstract boolean isEmpty();
    public abstract <B> List<B> map(Function<A,B> f);
    public abstract A head();
    public abstract List<A> tail();
    public abstract List<A> append(List<A> other);

    public static <A> List<A> nil() {
        return new List.Nil<>();
    }
    public static <A> List<A> cons(A head, List<A> tail){
        return new List.Cons<>(head,tail);
    }
    public static <A> List<A> single(A head){
        return List.cons(head, List.<A>nil());
    }

    @SafeVarargs
    public static <A> List<A> list(A... as){
        List<A> rev = nil();
        for (A a: as){
            rev = cons(a, rev);
        }
        return reverse(rev);
    }
    public static <A> List<A> reverse(List<A> list){
        List<A> acc = nil();
        while(!list.isEmpty()){
            acc = cons(list.head(), acc);
            list = list.tail();
        }
        return acc;
    }


    public static final class Nil<A> extends List<A> {
        public boolean isEmpty() {
            return true;
        }
        public <B> List<B> map(Function<A, B> f) {
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
        public <B> List<B> map(Function<A, B> f) {
            return cons(f.apply(head), tail.map(f));
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
    }

    public String toString() {
        return Strings.mkString("List(", ",", ")", this);
    }
}
