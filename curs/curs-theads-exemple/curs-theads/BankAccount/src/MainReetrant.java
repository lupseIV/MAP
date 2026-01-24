public class MainReetrant {

        public static void main(String[] args) {
            BankAccountReetrantLock account = new BankAccountReetrantLock();

            // Două thread-uri care depun simultan
            Thread t1 = new Thread(() -> {
                for (int i = 0; i < 5; i++) {
                    account.deposit(100);
                }
            }, "Thread-1");

            Thread t2 = new Thread(() -> {
                for (int i = 0; i < 5; i++) {
                    account.deposit(100);
                }
            }, "Thread-2");

            t1.start();
            t2.start();

            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Sold final = " + account.getBalance());
        }

}
