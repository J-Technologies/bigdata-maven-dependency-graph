package nl.ordina.jtech.mavendependencygraph.neo4j;

import nl.ordina.jtech.mavendependencygraph.model.GSonConverter;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Class: UploaderStatus
 */
public class UploaderStatus implements GSonConverter {

    private final int executedCount;
    private final int activeCount;
    private final long completedTaskCount;
    private final int poolSize;
    private final int queued;
    private final long taskCount;

    private UploaderStatus(int count, ThreadPoolExecutor service) {
        executedCount = count;
        activeCount = service.getActiveCount();
        completedTaskCount = service.getCompletedTaskCount();
        poolSize = service.getPoolSize();
        queued = service.getQueue().size();
        taskCount = service.getTaskCount();
    }

    public static final UploaderStatus build(final int executerCount, final ThreadPoolExecutor service) {
        return new UploaderStatus(executerCount, service);
    }

    public int getExecutedCount() {
        return executedCount;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public long getCompletedTaskCount() {
        return completedTaskCount;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public int getQueued() {
        return queued;
    }

    public long getTaskCount() {
        return taskCount;
    }
}
