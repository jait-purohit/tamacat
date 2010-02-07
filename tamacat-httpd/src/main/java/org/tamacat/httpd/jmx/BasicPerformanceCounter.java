package org.tamacat.httpd.jmx;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class BasicPerformanceCounter implements PerformanceCounter, Serializable {

	private static final long serialVersionUID = -1071646501162154246L;
	
	private AtomicLong count = new AtomicLong();
	private AtomicLong responseTimes = new AtomicLong();
	private AtomicLong max = new AtomicLong();
	
	@Override
	public long getAverageResponseTime() {
		return count.get() > 0 ? responseTimes.get() / count.get() : 0;
	}

	@Override
	public long getMaximumResponseTime() {
		return max.get();
	}
	
	public void setResponseTime(long time) {
		responseTimes.addAndGet(time);
		count.incrementAndGet();
		if (max.get() < time) max.set(time);
	}

}
