import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExample {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(4);

        for (int i = 1; i <= 10; i++) {
            int taskId = i;
            pool.submit(() -> {
                System.out.println("Task " + taskId + " rulează în thread " +
                        Thread.currentThread().getName());
                try {
                    Thread.sleep(1000); // simulez o operație care durează
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Task " + taskId + " s-a terminat");
            });
        }

        // Semnalăm că nu mai trimitem task-uri noi
        pool.shutdown();

        // Așteptăm să termine toate task-urile
        if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
            pool.shutdownNow();
        }

        System.out.println("Toate task-urile s-au terminat.");
    }
}