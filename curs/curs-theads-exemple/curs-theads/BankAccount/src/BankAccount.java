public class BankAccount {
    private int balance = 0;

    // Metoda critică: modifică variabila shared "balance"
    public synchronized void deposit(int amount) {
        int newBalance = balance + amount;

        // Simulăm un task care durează (pentru a evidenția problema fără sincronizare)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        balance = newBalance;
        System.out.println(Thread.currentThread().getName() + " a depus " + amount + ", noul sold = " + balance);
    }

    public int getBalance() {
        return balance;
    }
}
