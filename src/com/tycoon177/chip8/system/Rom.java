package com.tycoon177.chip8.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.DataFormatException;

public class Rom {
	private int[] romData;
	private int length;

	public Rom(String fileName) throws DataFormatException, IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			throw new FileNotFoundException("ROM FILE NOT FOUND");
		}
		FileInputStream input = new FileInputStream(file);
		int index = 0;
		length = (int) file.length();
		romData = new int[length];
		while (input.available() > 0) {
			if (index >= romData.length) {
				input.close();
				throw new IndexOutOfBoundsException("Not enough space allocated for rom data");
			}
			romData[index++] = input.read();
		}
		input.close();
	}

	public int[] getRom() {
		return romData;
	}

	public int getLength() {
		return length;
	}
}
