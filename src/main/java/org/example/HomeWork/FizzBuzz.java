package org.example.HomeWork;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

public class FizzBuzz {
    private final int n;
    private final AtomicInteger currentNumber = new AtomicInteger(1);
    private final Object lock = new Object();

    public FizzBuzz(int n) {
        this.n = n;
    }

    public void fizz(Runnable printFizz) throws InterruptedException {
        while (true) {
            synchronized (lock) {
                while (currentNumber.get() <= n && !(currentNumber.get() % 3 == 0 && currentNumber.get() % 5 != 0)) {
                    lock.wait();
                }
                if (currentNumber.get() > n) break;
                printFizz.run();
                currentNumber.incrementAndGet();
                lock.notifyAll();
            }
        }
    }

    public void buzz(Runnable printBuzz) throws InterruptedException {
        while (true) {
            synchronized (lock) {
                while (currentNumber.get() <= n && !(currentNumber.get() % 5 == 0 && currentNumber.get() % 3 != 0)) {
                    lock.wait();
                }
                if (currentNumber.get() > n) break;
                printBuzz.run();
                currentNumber.incrementAndGet();
                lock.notifyAll();
            }
        }
    }

    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        while (true) {
            synchronized (lock) {
                while (currentNumber.get() <= n && !(currentNumber.get() % 3 == 0 && currentNumber.get() % 5 == 0)) {
                    lock.wait();
                }
                if (currentNumber.get() > n) break;
                printFizzBuzz.run();
                currentNumber.incrementAndGet();
                lock.notifyAll();
            }
        }
    }

    public void number(IntConsumer printNumber) throws InterruptedException {
        while (true) {
            synchronized (lock) {
                while (currentNumber.get() <= n && (currentNumber.get() % 3 == 0 || currentNumber.get() % 5 == 0)) {
                    lock.wait();
                }
                if (currentNumber.get() > n) break;
                printNumber.accept(currentNumber.get());
                currentNumber.incrementAndGet();
                lock.notifyAll();
            }
        }
    }

    public static void main(String[] args) {
        int n = 15;
        FizzBuzz fizzBuzz = new FizzBuzz(n);

        Thread fizzThread = new Thread(() -> {
            try {
                fizzBuzz.fizz(() -> System.out.print("fizz, "));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread buzzThread = new Thread(() -> {
            try {
                fizzBuzz.buzz(() -> System.out.print("buzz, "));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread fizzBuzzThread = new Thread(() -> {
            try {
                fizzBuzz.fizzbuzz(() -> System.out.print("fizzbuzz, "));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread numberThread = new Thread(() -> {
            try {
                fizzBuzz.number(num -> System.out.print(num + ", "));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        fizzThread.start();
        buzzThread.start();
        fizzBuzzThread.start();
        numberThread.start();

        try {
            fizzThread.join();
            buzzThread.join();
            fizzBuzzThread.join();
            numberThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
