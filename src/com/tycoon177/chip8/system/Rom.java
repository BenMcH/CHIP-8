package com.tycoon177.chip8.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.DataFormatException;

/**
 * Represents a Rom file
 * 
 * @author Benjamin McHone
 *
 */
public class Rom {
	private int[] romData;
	private int length;

	/**
	 * Constructs a rom from a file
	 * 
	 * @param fileName
	 *            The file name of the rom
	 * @throws DataFormatException
	 *             Thrown when there are an odd number of bytes.
	 * @throws IOException
	 *             Thrown when the file cannot be read.
	 */
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

	/**
	 * Returns the rom data
	 * 
	 * @return The rom data in an integer array
	 */
	public int[] getRom() {
		return romData;
	}

	/**
	 * Gets the length of the rom file.
	 * 
	 * @return The amount of bytes that the rom is.
	 */
	public int getLength() {
		return length;
	}
}
