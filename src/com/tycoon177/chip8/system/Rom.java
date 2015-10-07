package com.tycoon177.chip8.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.DataFormatException;

public class Rom {
	int[] romData;

	public Rom(String fileName) throws DataFormatException, IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			throw new FileNotFoundException("ROM FILE NOT FOUND");
		}
		FileInputStream input = new FileInputStream(file);
		int index = 0;
		romData = new int[4096 - 0x200];
		while (input.available() > 0) {
			romData[index++] = input.read();
		}
	}

	public int[] getRom() {
		return romData;
	}
}
