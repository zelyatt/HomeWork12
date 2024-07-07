package org.example.HomeWork;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class MassageControl {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public void showTimeElapsed() {
        final Runnable displayElapsedTime = new Runnable() {
            private long startTime = System.currentTimeMillis();

            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = (currentTime - startTime) / 1000;
                System.out.println(elapsedTime + " second(s) have passed since the program started");
            }
        };

        final Runnable displayFiveSecondMessage = new Runnable() {
            @Override
            public void run() {
                System.out.println("Five seconds have passed!");
            }
        };

        scheduler.scheduleAtFixedRate(displayElapsedTime, 0, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(displayFiveSecondMessage, 5, 5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        MassageControl massageControl = new MassageControl();
        massageControl.showTimeElapsed();
    }
}