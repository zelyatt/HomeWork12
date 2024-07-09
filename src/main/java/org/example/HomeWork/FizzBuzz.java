package org.example.HomeWork;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class FizzBuzz {
    private final int n;
    private final AtomicInteger currentNumber = new AtomicInteger(1);
    private final BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
    private final Object lock = new Object();

    public FizzBuzz(int n) {
        this.n = n;
    }

    public void fizz() throws InterruptedException {
        while (true) {
            synchronized (lock) {
                while (currentNumber.get() <= n && !(currentNumber.get() % 3 == 0 && currentNumber.get() % 5 != 0)) {
                    lock.wait();
                }
                if (currentNumber.get() > n) break;
                outputQueue.put("fizz");
                currentNumber.incrementAndGet();
                lock.notifyAll();
            }
        }
    }

    public void buzz() throws InterruptedException {
        while (true) {
            synchronized (lock) {
                while (currentNumber.get() <= n && !(currentNumber.get() % 5 == 0 && currentNumber.get() % 3 != 0)) {
                    lock.wait();
                }
                if (currentNumber.get() > n) break;
                outputQueue.put("buzz");
                currentNumber.incrementAndGet();
                lock.notifyAll();
            }
        }
    }

    public void fizzbuzz() throws InterruptedException {
        while (true) {
            synchronized (lock) {
                while (currentNumber.get() <= n && !(currentNumber.get() % 3 == 0 && currentNumber.get() % 5 == 0)) {
                    lock.wait();
                }
                if (currentNumber.get() > n) break;
                outputQueue.put("fizzbuzz");
                currentNumber.incrementAndGet();
                lock.notifyAll();
            }
        }
    }

    public void number() throws InterruptedException {
        while (true) {
            synchronized (lock) {
                while (currentNumber.get() <= n && (currentNumber.get() % 3 == 0 || currentNumber.get() % 5 == 0)) {
                    lock.wait();
                }
                if (currentNumber.get() > n) break;
                outputQueue.put(String.valueOf(currentNumber.get()));
                currentNumber.incrementAndGet();
                lock.notifyAll();
            }
        }
    }

    public void printFromQueue() throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            System.out.print(outputQueue.take() + (i < n ? ", " : ""));
        }
    }

    public static void main(String[] args) {
        int n = 15;
        FizzBuzz fizzBuzz = new FizzBuzz(n);

        Thread fizzThread = new Thread(() -> {
            try {
                fizzBuzz.fizz();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread buzzThread = new Thread(() -> {
            try {
                fizzBuzz.buzz();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread fizzBuzzThread = new Thread(() -> {
            try {
                fizzBuzz.fizzbuzz();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread numberThread = new Thread(() -> {
            try {
                fizzBuzz.number();
                fizzBuzz.printFromQueue();
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
