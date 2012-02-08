package org.tamacat.banchmark;

public class BenchmarkTest {

	public static void main(String a[]) throws Exception {
		String[] args = new String[]{
			"-c", "2", "-n", "10",
			"http://localhost/"
		};
		Benchmark.main(args);
	}
}
