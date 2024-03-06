package org.example.HomeWork;

import java.util.concurrent.Semaphore;
import java.util.function.IntConsumer;

class FizzBuzz {
    private int n;
    private int currentNumber;
    private final Object lock = new Object();

    public FizzBuzz(int n) {
        this.n = n;
        this.currentNumber = 1;
    }

    public void fizz(Runnable printFizz) throws InterruptedException {
        synchronized (lock) {
            while (currentNumber <= n) {
                if (currentNumber % 3 == 0 && currentNumber % 5 != 0) {
                    printFizz.run();
                    currentNumber++;
                    lock.notifyAll();
                } else {
                    lock.wait();
                }
            }
        }
    }

    public void buzz(Runnable printBuzz) throws InterruptedException {
        synchronized (lock) {
            while (currentNumber <= n) {
                if (currentNumber % 5 == 0 && currentNumber % 3 != 0) {
                    printBuzz.run();
                    currentNumber++;
                    lock.notifyAll();
                } else {
                    lock.wait();
                }
            }
        }
    }

    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        synchronized (lock) {
            while (currentNumber <= n) {
                if (currentNumber % 3 == 0 && currentNumber % 5 == 0) {
                    printFizzBuzz.run();
                    currentNumber++;
                    lock.notifyAll();
                } else {
                    lock.wait();
                }
            }
        }
    }

    public void number(IntConsumer printNumber) throws InterruptedException {
        synchronized (lock) {
            while (currentNumber <= n) {
                if (currentNumber % 3 != 0 && currentNumber % 5 != 0) {
                    printNumber.accept(currentNumber);
                    currentNumber++;
                    lock.notifyAll();
                } else {
                    lock.wait();
                }
            }
        }
    }

    public static void main(String[] args) {
        int n = 15;
        FizzBuzz fizzBuzz = new FizzBuzz(n);

        Thread fizzThread = new Thread(() -> {
            try {
                fizzBuzz.fizz(() -> System.out.println("fizz"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread buzzThread = new Thread(() -> {
            try {
                fizzBuzz.buzz(() -> System.out.println("buzz"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread fizzBuzzThread = new Thread(() -> {
            try {
                fizzBuzz.fizzbuzz(() -> System.out.println("fizzbuzz"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread numberThread = new Thread(() -> {
            try {
                fizzBuzz.number(System.out::println);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        fizzThread.start();
        buzzThread.start();
        fizzBuzzThread.start();
        numberThread.start();
    }
}
