package compilers.ast;

public enum PCodeType {
    INT {
        public int size() {
            return 1;
        }
    },
    REAL {
        public int size() {
            return 2;
        }
    },
    BOOL {
        public int size() {
            return 1;
        }
    };


    public abstract int size();
}
