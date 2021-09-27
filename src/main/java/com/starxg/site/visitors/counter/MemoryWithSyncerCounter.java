package com.starxg.site.visitors.counter;

import java.io.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * MemoryWithSyncerCounter
 * 
 * @author huangxingguang
 */
public class MemoryWithSyncerCounter extends MemoryCounter {

    private final BlockingDeque<String> commands = new LinkedBlockingDeque<>();

    private final PrintWriter pw;

    public MemoryWithSyncerCounter(File file) {

        if (file.exists()) {
            if (file.canRead()) {
                try (final BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String key, value;
                    while ((key = br.readLine()) != null && (value = br.readLine()) != null) {
                        if (key.isBlank() || value.isBlank()) {
                            continue;
                        }
                        cache.put(key, Long.parseLong(value));
                    }
                } catch (Exception e) {
                    System.err.print("Failed to restore data, reason: ");
                    e.printStackTrace(System.err);
                }
            }
        }

        try {
            final File p = file.getAbsoluteFile().getParentFile();
            if (!p.exists() && !p.mkdirs()) {
                throw new IllegalStateException("mkdirs failed");
            }
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)), true);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        startSyncer();

    }

    @Override
    public long incr(String key) {
        final long count = super.incr(key);
        commands.offer(key);
        commands.offer(String.valueOf(count));
        return count;
    }

    @SuppressWarnings("AlibabaThreadPoolCreation")
    private void startSyncer() {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {

                    final String command = commands.take();

                    try {
                        pw.println(command);
                    } catch (Exception e) {
                        System.err.print("Persistence failed, reason: ");
                        e.printStackTrace(System.err);
                    }

                }
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        });

        executorService.shutdown();
    }

}
