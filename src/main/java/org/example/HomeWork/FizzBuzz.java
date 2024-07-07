package org.example.HomeWork;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

public class FizzBuzz {
    private int n;
    private AtomicInteger currentNumber = new AtomicInteger(1);
    private BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();

    public FizzBuzz(int n) {
        this.n = n;
    }

    public void fizz(Runnable printFizz) throws InterruptedException {
        while (true) {
            int num = currentNumber.get();
            if (num > n) break;
            if (num % 3 == 0 && num % 5 != 0) {
                printFizz.run();
                currentNumber.incrementAndGet();
            }
        }
    }

    public void buzz(Runnable printBuzz) throws InterruptedException {
        while (true) {
            int num = currentNumber.get();
            if (num > n) break;
            if (num % 5 == 0 && num % 3 != 0) {
                printBuzz.run();
                currentNumber.incrementAndGet();
            }
        }
    }

    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        while (true) {
            int num = currentNumber.get();
            if (num > n) break;
            if (num % 3 == 0 && num % 5 == 0) {
                printFizzBuzz.run();
                currentNumber.incrementAndGet();
            }
        }
    }

    public void number(IntConsumer printNumber) throws InterruptedException {
        while (true) {
            int num = currentNumber.get();
            if (num > n) break;
            if (num % 3 != 0 && num % 5 != 0) {
                printNumber.accept(num);
                currentNumber.incrementAndGet();
            }
        }
    }

    public static void main(String[] args) {
        int n = 15;
        FizzBuzz fizzBuzz = new FizzBuzz(n);

        Thread fizzThread = new Thread(() -> {
            try {
                fizzBuzz.fizz(() -> {
                    try {
                        fizzBuzz.outputQueue.put("fizz");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread buzzThread = new Thread(() -> {
            try {
                fizzBuzz.buzz(() -> {
                    try {
                        fizzBuzz.outputQueue.put("buzz");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread fizzBuzzThread = new Thread(() -> {
            try {
                fizzBuzz.fizzbuzz(() -> {
                    try {
                        fizzBuzz.outputQueue.put("fizzbuzz");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread numberThread = new Thread(() -> {
            try {
                fizzBuzz.number(num -> {
                    try {
                        fizzBuzz.outputQueue.put(String.valueOf(num));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread printerThread = new Thread(() -> {
            try {
                for (int i = 1; i <= n; i++) {
                    System.out.print(fizzBuzz.outputQueue.take());
                    if (i < n) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        fizzThread.start();
        buzzThread.start();
        fizzBuzzThread.start();
        numberThread.start();
        printerThread.start();

        try {
            fizzThread.join();
            buzzThread.join();
            fizzBuzzThread.join();
            numberThread.join();
            printerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
