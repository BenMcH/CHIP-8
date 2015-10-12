package com.tycoon177.chip8.system;

import java.io.FileInputStream;
import java.io.IOException;

public class Memory {
	private int[] memory;

	/**
	 * Allocates the correct amount of memory for the system.
	 * 
	 * @param amount
	 *            The amount, in bytes, of memory to be allowed to the system.
	 * 
	 */
	public Memory(int amount) {
		memory = new int[amount];
		try {
			loadSystemFromFile("chip8.rom");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets a specific byte of memory.
	 * 
	 * @param address
	 *            The address for the memory to be set
	 * @param data
	 *            The value to set the memory to
	 */
	public void setMemory(Address address, int data) {
		if (address.getAddress() < 0 || address.getAddress() >= memory.length) {
			throw new IllegalArgumentException("Memory Out of Bounds!");
		}
		memory[address.getAddress()] = data;
	}

	/**
	 * Gets a value from memory.
	 * 
	 * @param address
	 *            The address to read from
	 * @return The memory at the specified address.
	 */
	public int getMemory(Address address) {
		if (address.getAddress() < 0 || address.getAddress() >= memory.length) {
			throw new IllegalArgumentException("Memory Out of Bounds!");
		}
		return memory[address.getAddress()] & 0xff;
	}

	public int[] getMemory() {
		return memory;
	}

	/**
	 * Gets the rom file and loads it into memory.
	 * 
	 * @param file
	 *            The file name that is the rom.
	 * @throws IOException
	 *             Thrown when the file cannot be found or read.
	 */
	private void loadSystemFromFile(String file) throws IOException {
		FileInputStream input = new FileInputStream(file);
		byte[] rom = new byte[0x200];
		input.read(rom);
		input.close();
		for (int i = 0; i < rom.length; i++) {
			memory[i] = rom[i];
		}
	}

	public void clearMemory() {
		for (int i = 0; i < memory.length; i++) {
			memory[i] = 0;
		}
		try {
			loadSystemFromFile("chip8.rom");
		} catch (IOException e) {
		}
	}
}
