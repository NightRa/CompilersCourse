package compiler.util;

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

    public static <A> String mkString(String open, String separator, String close, List<A> lines, Function<A, String> toString) {
        StringBuilder sb = new StringBuilder();
        sb.append(open);
        if (!lines.isEmpty()) {
            sb.append(toString.apply(lines.head()));
            lines = lines.tail();
            while (!lines.isEmpty()) {
                sb.append(separator);
                sb.append(toString.apply(lines.head()));
                lines = lines.tail();
            }
        }
        sb.append(close);
        return sb.toString();
    }
    public static <A> String mkString(String open, String separator, String close, List<A> lines) {
        return mkString(open, separator, close, lines, new Function<A, String>() {
            public String apply(A value) {
                return value.toString();
            }
        });
    }
}
