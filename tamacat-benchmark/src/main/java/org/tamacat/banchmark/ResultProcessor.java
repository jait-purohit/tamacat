package org.tamacat.banchmark;

import java.text.NumberFormat;

import org.apache.http.HttpHost;
import org.tamacat.banchmark.worker.BenchmarkWorker;
import org.tamacat.banchmark.worker.Stats;

public class ResultProcessor {

    static NumberFormat nf2 = NumberFormat.getInstance();
    static NumberFormat nf3 = NumberFormat.getInstance();
    static NumberFormat nf6 = NumberFormat.getInstance();

    static {
        nf2.setMaximumFractionDigits(2);
        nf2.setMinimumFractionDigits(2);
        nf3.setMaximumFractionDigits(3);
        nf3.setMinimumFractionDigits(3);
        nf6.setMaximumFractionDigits(6);
        nf6.setMinimumFractionDigits(6);
    }

    static String printResults(BenchmarkWorker[] workers, HttpHost host,
        String uri, long contentLength) {

        double totalTimeNano = 0;
        long successCount    = 0;
        long failureCount    = 0;
        long writeErrors     = 0;
        long keepAliveCount  = 0;
        long totalBytesRcvd  = 0;

        Stats stats = workers[0].getStats();

        for (int i = 0; i < workers.length; i++) {
            Stats s = workers[i].getStats();
            totalTimeNano  += s.getDuration();
            successCount   += s.getSuccessCount();
            failureCount   += s.getFailureCount();
            writeErrors    += s.getWriteErrors();
            keepAliveCount += s.getKeepAliveCount();
            totalBytesRcvd += s.getTotalBytesRecv();
        }

        int threads = workers.length;
        double totalTimeMs  = (totalTimeNano / threads) / 1000000; // convert nano secs to milli secs
        double timePerReqMs = totalTimeMs / successCount;
        double totalTimeSec = totalTimeMs / 1000;
        double reqsPerSec   = successCount / totalTimeSec;
        long totalBytesSent = contentLength * successCount;
        long totalBytes     = totalBytesRcvd + (totalBytesSent > 0 ? totalBytesSent : 0);

        StringBuilder sb = new StringBuilder(1024);

        printAndAppend(sb,"\nServer Software:\t\t" + stats.getServerName());
        printAndAppend(sb, "Server Hostname:\t\t" + host.getHostName());
        printAndAppend(sb, "Server Port:\t\t\t" +
            (host.getPort() > 0 ? host.getPort() : uri.startsWith("https") ? "443" : "80") + "\n");
        printAndAppend(sb, "Document Path:\t\t\t" + uri);
        printAndAppend(sb, "Document Length:\t\t" + stats.getContentLength() + " bytes\n");
        printAndAppend(sb, "Concurrency Level:\t\t" + workers.length);
        printAndAppend(sb, "Time taken for tests:\t\t" + nf6.format(totalTimeSec) + " seconds");
        printAndAppend(sb, "Complete requests:\t\t" + successCount);
        printAndAppend(sb, "Failed requests:\t\t" + failureCount);
        printAndAppend(sb, "Write errors:\t\t\t" + writeErrors);
        printAndAppend(sb, "Kept alive:\t\t\t" + keepAliveCount);
        printAndAppend(sb, "Total transferred:\t\t" + totalBytes + " bytes");
        printAndAppend(sb, "Requests per second:\t\t" + nf2.format(reqsPerSec) + " [#/sec] (mean)");
        printAndAppend(sb, "Time per request:\t\t" + nf3.format(timePerReqMs * workers.length) + " [ms] (mean)");
        printAndAppend(sb, "Time per request:\t\t" + nf3.format(timePerReqMs) +
            " [ms] (mean, across all concurrent requests)");
        printAndAppend(sb, "Transfer rate:\t\t\t" +
            nf2.format(totalBytesRcvd/1000/totalTimeSec) + " [Kbytes/sec] received");
        printAndAppend(sb, "\t\t\t\t" +
            (totalBytesSent > 0 ? nf2.format(totalBytesSent/1000/totalTimeSec) : -1) + " kb/s sent");
        printAndAppend(sb, "\t\t\t\t" +
            nf2.format(totalBytes/1000/totalTimeSec) + " kb/s total");

        return sb.toString();
    }

    private static void printAndAppend(StringBuilder sb, String s) {
        System.out.println(s);
        sb.append(s).append("\r\n");
    }
}

