package org.tamacat.banchmark;

public class BenchmarkTest {

	public static void main(String a[]) throws Exception {
		String[] args = new String[]{
			"-c", "20", "-n", "1000",
			"http://192.168.10.10:8080/examples/servlets/servlet/RequestHeaderExample"
		};
		Benchmark.main(args);
	}
}
