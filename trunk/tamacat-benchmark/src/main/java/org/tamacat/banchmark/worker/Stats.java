package org.tamacat.banchmark.worker;


/**
 * Helper to gather statistics for an {@link HttpBenchmark HttpBenchmark}.
 *
 *
 * @since 4.0
 */
public class Stats {

    private long startTime = -1;    // nano seconds - does not represent an actual time
    private long finishTime = -1;   // nano seconds - does not represent an actual time
    private int successCount = 0;
    private int failureCount = 0;
    private int writeErrors = 0;
    private int keepAliveCount = 0;
    private String serverName = null;
    private long totalBytesRecv = 0;
    private long contentLength = -1;

    public Stats() {
        super();
    }

    public void start() {
        this.startTime = System.nanoTime();
    }

    public void finish() {
        this.finishTime = System.nanoTime();
    }

    public long getFinishTime() {
        return this.finishTime;
    }

    public long getStartTime() {
        return this.startTime;
    }

    /**
     * Total execution time measured in nano seconds
     *
     * @return duration in nanoseconds
     */
    public long getDuration() {
        // we are using System.nanoTime() and the return values could be negative
        // but its only the difference that we are concerned about
        return this.finishTime - this.startTime;
    }

    public void incSuccessCount() {
        this.successCount++;
    }

    public int getSuccessCount() {
        return this.successCount;
    }

    public void incFailureCount() {
        this.failureCount++;
    }

    public int getFailureCount() {
        return this.failureCount;
    }

    public void incWriteErrors() {
        this.writeErrors++;
    }

    public int getWriteErrors() {
        return this.writeErrors;
    }

    public void incKeepAliveCount() {
        this.keepAliveCount++;
    }

    public int getKeepAliveCount() {
        return this.keepAliveCount;
    }

    public long getTotalBytesRecv() {
        return this.totalBytesRecv;
    }

    public void incTotalBytesRecv(int n) {
        this.totalBytesRecv += n;
    }

    public long getContentLength() {
        return this.contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }

}

