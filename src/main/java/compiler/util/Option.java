package compiler.util;

// NonEmptyList[A] = A,List[A]
// 1 + x + x^2 + x^3 + ...
// List[A] = Nil | Cons (A,List[A])
// f(x) = 1      + x * f(x)
// 1 + x*(1 + x*(1 + ...))
// f - x * f = 1
// (1-x)f = 1
// f = 1 / (1-x)
// x * f' = Zipper = [a], a, [a]
// Taylor expansion:
// 1 + x + x^2 + x^3 + ...

// Doesn't work: -1
// x * (1 + x + x^2 + x^3 + ...) = x + x^2 + x^3 + ...

// Function: A => B
// Value: B^A
// (A,B) => C = C^(AxB)
// A => (B => C) = (C^B)^A = C^(AxB)
// ^ Currying!
// f(x,y) = ...
// f x y  = ...

public abstract class Option<A> {
    public abstract boolean isNone();
    public abstract boolean isSome();
    public abstract A get();
    public abstract A getOrElse(A other);
    public abstract A getOrError(String message);
    // B <: A => Option[B] <: Option[A]
    public abstract <B> Option<B> map(Function<A, B> f);
    public abstract <B> Option<B> flatMap(Function<A, Option<B>> f);
    public abstract Option<A> orElse(Option<A> other);

    public static <A> Option<A> none() {
        return new None<>();
    }
    public static <A> Option<A> some(A value) {
        return new Some<>(value);
    }
    public static <A> Option<A> iff(boolean p, A value) {
        if (p) {
            return some(value);
        } else {
            return none();
        }
    }
    public static <A> Option<A> fromNull(A value) {
        if (value == null) {
            return none();
        } else {
            return some(value);
        }
    }

    public static final class None<A> extends Option<A> {
        public boolean isNone() {
            return true;
        }
        public boolean isSome() {
            return false;
        }
        public A get() {
            throw new IllegalArgumentException("get() on None");
        }
        public A getOrElse(A other) {
            return other;
        }
        public A getOrError(String message) {
            throw new RuntimeException(message);
        }
        public <B> Option<B> map(Function<A, B> f) {
            return none();
        }
        public <B> Option<B> flatMap(Function<A, Option<B>> f) {
            return none();
        }
        public Option<A> orElse(Option<A> other) {
            return other;
        }
        public String toString() {
            return "None";
        }
    }
    public static final class Some<A> extends Option<A> {
        public final A value;

        public Some(A value) {
            this.value = value;
        }
        public boolean isNone() {
            return false;
        }
        public boolean isSome() {
            return true;
        }
        public A get() {
            return value;
        }
        public A getOrElse(A other) {
            return value;
        }
        public A getOrError(String message) {
            return value;
        }
        public <B> Option<B> map(Function<A, B> f) {
            return some(f.apply(value));
        }
        public <B> Option<B> flatMap(Function<A, Option<B>> f) {
            return f.apply(value);
        }
        public Option<A> orElse(Option<A> other) {
            return this;
        }
        public String toString() {
            return "Some(" + value.toString() + ")";
        }
    }

}
