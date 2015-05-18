package compiler.util;

import java.util.Iterator;

public class Strings {
    public static String multiply(int times, String s) {
        StringBuilder sb = new StringBuilder(s.length() * times);
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String indent(int size, String text) {
        final String indentation = indentation(size);
        String[] linesArr = text.split("\r\n|\r|\n");
        List<String> lines = List.list(linesArr);
        return mkString("", "\r\n", "", lines,
                new Function<String, String>() {
                    public String apply(String line) {
                        return indentation + line;
                    }
                });
    }

    public static String indentation(int size) {
        return multiply(size, " ");
    }
    public static List<String> appendPrefix(final String prefix, List<String> lines) {
        return lines.map(new Function<String, String>() {
            public String apply(String line) {
                return prefix + line;
            }
        });
    }

    public static <A> String mkString(String open, String separator, String close, Iterable<A> lines, Function<A, String> toString) {
        StringBuilder sb = new StringBuilder();
        sb.append(open);
        Iterator<A> iterator = lines.iterator();
        if (iterator.hasNext()) {
            sb.append(toString.apply(iterator.next()));
            while (iterator.hasNext()) {
                sb.append(separator);
                sb.append(toString.apply(iterator.next()));
            }
        }
        sb.append(close);
        return sb.toString();
    }
    public static <A> String mkString(String open, String separator, String close, Iterable<A> lines) {
        return mkString(open, separator, close, lines, new Function<A, String>() {
            public String apply(A value) {
                return value.toString();
            }
        });
    }

    public static <A> String blockToString(String header, Iterable<A> body) {
        return indentBlock(header + "{", body) + "} ";
    }

    public static <A> String indentBlock(String header, Iterable<A> body) {
        return header + "\r\n" +
                indent(2,
                        mkString("", "\r\n", "",
                                IterableUtil.mapLazy(body, new Function<A, String>() {
                                    public String apply(A line) {
                                        return line.toString();
                                    }
                                })))
                + "\r\n";
    }

}
