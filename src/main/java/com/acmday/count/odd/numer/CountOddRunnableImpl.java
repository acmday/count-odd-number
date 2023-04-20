package com.acmday.count.odd.numer;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import static com.acmday.count.odd.numer.Constants.COUNT;
import static com.acmday.count.odd.numer.Constants.NUMBER_COUNT;

/**
 * @author acmday
 * @date 2023/4/19 下午10:13
 */
@Slf4j
public class CountOddRunnableImpl implements Runnable{

    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(Constants.THREAD_COUNT);
    private final CountDownLatch latch;
    private final Map<String, AtomicInteger> resultMap;

    public CountOddRunnableImpl(CountDownLatch cdl, Map<String, AtomicInteger> resultMap) {
        this.latch = cdl;
        this.resultMap = resultMap;
    }

    @Override
    public void run() {
        for (int j = 0; j < COUNT; j++) {
            execute();
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                log.error("系统异常！", e);
            }
        }
        latch.countDown();
    }

    private synchronized void execute() {
        for (int i = 0; i < NUMBER_COUNT; i++) {
            // 生成随机数
            int value = RANDOM.nextInt();
            // 判断value是否是偶数，如果3个数中有一个数字是偶数，则直接返回
            if(Objects.equals(0, value & 1 )) {
                return;
            }
        }
        // 3个数字都是奇数则保存结果
        String threadName = Thread.currentThread().getName();
        // AtomicInteger用Integer代替也行，不过效率也高不了多少
        AtomicInteger value = resultMap.putIfAbsent(threadName, new AtomicInteger(1));
        if (Objects.nonNull(value)) {
            value.incrementAndGet();
        }
    }
}
