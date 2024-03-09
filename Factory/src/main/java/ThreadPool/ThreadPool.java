package ThreadPool;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPool {

    protected ExecutorService m_ExecutorService = null;

    public ThreadPool(Integer workerCount) {
        m_ExecutorService = Executors.newFixedThreadPool(workerCount);
    }

    public void submitTask(Runnable task) {
        if(!isValid()) return;

        m_ExecutorService.submit(task);
    }

    public boolean isValid() {
        return !m_ExecutorService.isTerminated() && !m_ExecutorService.isShutdown();
    }

    public void shutdown() {

        m_ExecutorService.shutdown();
        try {
            // Wait for tasks to complete execution after a shutdown request
            if (!m_ExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
                // Shutdown now, because awaitTermination timed out
                List<Runnable> notExecutedTasks = m_ExecutorService.shutdownNow();
                // Log.Log.GetLogger().info("Tasks that were awaiting execution: " + notExecutedTasks.size());
            }
        } catch (InterruptedException e) {
            // (Re-)Cancel if current thread also interrupted
            m_ExecutorService.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }

    }

}
