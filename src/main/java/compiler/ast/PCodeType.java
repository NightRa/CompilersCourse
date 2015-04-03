package compiler.ast;

public enum PCodeType {
    Int {
        public int size() {
            return 1;
        }
    },
    Real {
        public int size() {
            return 2;
        }
    },
    Bool {
        public int size() {
            return 1;
        }
    };


    public abstract int size();
}
