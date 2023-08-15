package com.shameless.bookingtech.integration;

import com.shameless.bookingtech.common.util.Constants;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecuterTest {

    @Test
    @Ignore
    public void executerListTest() {
        try{
            executorTest();
        } catch (Exception e) {
            System.out.println("GENEL HATA");
        }
        try{
            executorTest();
        } catch (Exception e) {
            System.out.println("GENEL HATA");
        }
    }

    private void executorTest() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(Constants.CONCURRENT_SIZE);
        ExecuterTest obj = new ExecuterTest();
        for (int j = 0; j < Constants.CONCURRENT_COUNT; j++) {
            CountDownLatch innerLatch = new CountDownLatch(Constants.CONCURRENT_SIZE);
            for (int i = 0; i < Constants.CONCURRENT_SIZE; i++) {
                int finalI = (j * Constants.CONCURRENT_SIZE) + i;
                executor.execute(() -> {
                    try {
                        obj.run(finalI);
                    } catch (Exception e) {
                        System.out.println("Catch e düştü");
                        //throw new IllegalArgumentException("Stoppp");
                    } finally {
                        innerLatch.countDown();
                    }
                });
            }
            innerLatch.await();
        }
        executor.shutdown();
        System.out.println("Executor shutdown!");
    }

    private void run(int finalI){
        runInside2();
        runInside(finalI);
        runInside3();
    }

    private void runInside2() {
        System.out.println("run inside 2");
    }

    private void runInside3() {
        System.out.println("run inside 3");
    }

    private void runInside(int finalI) {
        System.out.println("run inside >>>>> " + finalI);
        if (finalI == 7) throw new NoSuchElementException("No Element");
    }
}
