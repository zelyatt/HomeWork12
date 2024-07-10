package org.example.HomeWork;

public class FizzBuzz {
    private int n;
    private int current = 1;
    private final Object lock = new Object();

    public FizzBuzz(int n) {
        this.n = n;
    }

    public void fizz() {
        synchronized (lock) {
            while (current <= n) {
                while (current <= n && (current % 3 != 0 || current % 5 == 0)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                if (current > n) {
                    break;
                }
                System.out.print("fizz, ");
                current++;
                lock.notifyAll();
            }
        }
    }

    public void buzz() {
        synchronized (lock) {
            while (current <= n) {
                while (current <= n && (current % 5 != 0 || current % 3 == 0)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                if (current > n) {
                    break;
                }
                System.out.print("buzz, ");
                current++;
                lock.notifyAll();
            }
        }
    }

    public void fizzbuzz() {
        synchronized (lock) {
            while (current <= n) {
                while (current <= n && (current % 3 != 0 || current % 5 != 0)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                if (current > n) {
                    break;
                }
                System.out.print("fizzbuzz, ");
                current++;
                lock.notifyAll();
            }
        }
    }

    public void number() {
        synchronized (lock) {
            while (current <= n) {
                while (current <= n && (current % 3 == 0 || current % 5 == 0)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                if (current > n) {
                    break;
                }
                System.out.print(current + ", ");
                current++;
                lock.notifyAll();
            }
        }
    }

    public static void main(String[] args) {
        int n = 150000;
        FizzBuzz fizzBuzz = new FizzBuzz(n);

        Thread threadA = new Thread(fizzBuzz::fizz);
        Thread threadB = new Thread(fizzBuzz::buzz);
        Thread threadC = new Thread(fizzBuzz::fizzbuzz);
        Thread threadD = new Thread(fizzBuzz::number);

        threadA.start();
        threadB.start();
        threadC.start();
        threadD.start();

        try {
            threadA.join();
            threadB.join();
            threadC.join();
            threadD.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}