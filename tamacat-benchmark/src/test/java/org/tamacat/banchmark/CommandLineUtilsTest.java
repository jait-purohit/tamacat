package org.tamacat.banchmark;

import static org.junit.Assert.*;

import org.apache.commons.cli.Options;
import org.junit.Test;

public class CommandLineUtilsTest {

	@Test
	public void testGetOptions() {
		Options option = CommandLineUtils.getOptions();
		assertEquals("concurrency", option.getOption("c").getArgName());
	}

	@Test
	public void testShowUsage() {
		//usage: HttpBenchmark [options] [http://]hostname[:port]/path?query
		CommandLineUtils.showUsage(new Options());
	}

}
