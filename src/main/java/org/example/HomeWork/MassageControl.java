package org.example.HomeWork;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class MassageControl {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void showMassageForFiveSecond() {
        final Runnable startProgram = new Runnable() {
            @Override
            public void run() {
                System.out.println("Five seconds have passed!");
            }
        };

        final ScheduledFuture<?> timeHandle = scheduler.scheduleAtFixedRate(startProgram, 5, 5, SECONDS);


    }

    public static void main(String[] args) {
        MassageControl massageControl = new MassageControl();
        massageControl.showMassageForFiveSecond();
    }
}
