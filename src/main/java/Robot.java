/**
 * https://www.dokwork.ru/2017/02/threadstates.html
 * <p>
 * Надо написать робота который умеет ходить. За движение каждой его ноги отвечает отдельный поток.
 * Шаг выражается в выводе в консоль LEFT или RIGHT.
 */
public class Robot {

    public static class Leg implements Runnable {

        private static final Object LOCK = new Object();

        private final String name;

        public Leg(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            for (int i = 1; i <= 100; i++) {
                synchronized (LOCK) {
                    LOCK.notify();
                    System.out.printf("'%s': step number %d%n", name, i);
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new Leg("Right")).start();
        new Thread(new Leg("Left")).start();
    }
}
