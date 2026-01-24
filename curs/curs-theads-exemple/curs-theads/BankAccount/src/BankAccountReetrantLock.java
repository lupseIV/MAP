import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccountReetrantLock {
    private int balance = 0;

    // Creăm un lock pentru acest cont
    private final Lock lock = new ReentrantLock();

    public void deposit(int amount) {
        lock.lock();
        try {
            int newBalance = balance + amount;

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            balance = newBalance;
            System.out.println(Thread.currentThread().getName() +
                    " a depus " + amount + ", noul sold = " + balance);
            // --------------------------
        } finally {
            // FOARTE IMPORTANT: eliberăm lock-ul în finally
            lock.unlock();
        }
    }

    public int getBalance() {
        return balance;
    }
}

