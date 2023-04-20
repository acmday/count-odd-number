package com.acmday.count.odd.numer;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static com.acmday.count.odd.numer.Constants.FILE_PATH;
import static com.acmday.count.odd.numer.Constants.THREAD_NAME_PREFIX;

/**
 * @author acmday
 * @date 2023/4/19 下午10:11
 */
@Slf4j
public class Main {

    public static void main(String[] args) {

        final CountDownLatch latch = new CountDownLatch(Constants.THREAD_COUNT);
        Map<String, AtomicInteger> resultMap = new HashMap<>();
        CountOddRunnableImpl countOddRunnable = new CountOddRunnableImpl(latch, resultMap);

        for(int i = 1; i <= Constants.THREAD_COUNT; i++) {
            new Thread(countOddRunnable, THREAD_NAME_PREFIX + i)
                    .start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.info("系统异常！", e);
        }

        // 结果写入文件
        try {
            File file=new File(FILE_PATH);
            boolean newFile = file.createNewFile();
            if(!newFile) {
                log.error("文件已存在, 数据将被覆盖, path={}", file);
            }
            FileOutputStream outputStream=new FileOutputStream(file);
            OutputStreamWriter writer=new OutputStreamWriter(outputStream);
            resultMap.forEach((key, value) -> {
                try {
                    writer.write(row(key, value));
                } catch (IOException e) {
                    log.error("系统异常！", e);
                }
            });
            writer.close();
        } catch (IOException e) {
            log.error("系统异常！", e);
        }
    }

    private static String row(String name, AtomicInteger value) {
        return name + ":" + value + "\n";
    }
}
