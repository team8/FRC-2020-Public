package com.palyrobotics.frc2020.util;

import static org.junit.Assert.assertThat;

import java.io.File;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class JSONFormatterTest { // doesn't work probably cause of different file locations based on computer/roboRIO
	
	// TODO: replace with assertThat methods instead
	
	File testJSON = JSONFormatter.loadFileDirectory("constants/fields", "Team8Field.json");
	
	/**
	 * Checks what happens if the requested file doesn't exist
	 */
	@Test
	public void testFileExists() {
		assertThat("File not found", testJSON, not(0));
	}
	
	/**
	 * Checks what happens when json file has invalid formatting and cannot be parsed
	 */
	@Test
	public void testParsableJSON() {
	  assertThat("File cannot be read", JSONFormatter.newJSON(testJSON), not(0));
	}
}
