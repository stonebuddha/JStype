public class Main {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        analysis.Interpreter.main(new String[]{"src/test/test0.js"});
        //concrete.Interpreter.main(new String[]{"src/test/test.js"});
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000.0);
    }
}
