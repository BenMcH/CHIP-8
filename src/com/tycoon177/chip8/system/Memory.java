package com.tycoon177.chip8.system;

public class Memory {
	private byte[] memory;

	public static int[] fontList = { 0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
			0x20, 0x60, 0x20, 0x20, 0x70, // 1
			0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
			0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
			0x90, 0x90, 0xF0, 0x10, 0x10, // 4
			0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
			0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
			0xF0, 0x10, 0x20, 0x40, 0x40, // 7
			0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
			0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
			0xF0, 0x90, 0xF0, 0x90, 0x90, // A
			0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
			0xF0, 0x80, 0x80, 0x80, 0xF0, // C
			0xE0, 0x90, 0x90, 0x90, 0xE0, // D
			0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
			0xF0, 0x80, 0xF0, 0x80, 0x80 };// F

	/**
	 * Allocates the correct amount of memory for the system.
	 * 
	 * @param amount
	 *            The amount, in bytes, of memory to be allowed to the system.
	 * 
	 */
	public Memory(int amount) {
		memory = new byte[amount];
		setupFonts();
	}

	/**
	 * Sets a specific byte of memory.
	 * 
	 * @param address
	 *            The address for the memory to be set
	 * @param value
	 *            The value to set the memory to
	 */
	public void setMemory(Address address, byte value) {
		if (address.getAddress() < 0 || address.getAddress() >= memory.length) {
			throw new IllegalArgumentException("Memory Out of Bounds!");
		}
		memory[address.getAddress()] = value;
	}

	/**
	 * Gets a value from memory.
	 * 
	 * @param address
	 *            The address to read from
	 * @return The memory at the specified address.
	 */
	public byte getMemory(Address address) {
		if (address.getAddress() < 0 || address.getAddress() >= memory.length) {
			throw new IllegalArgumentException("Memory Out of Bounds!");
		}
		return memory[address.getAddress()];
	}

	/**
	 * Inserts the fonts into memory.
	 */
	private void setupFonts() {
		for (int i = 0; i <= 0xf; i++) {
			for (int j = 0; j < 5; j++) {
				memory[i * 5 + j] = (byte) fontList[i*5 + j]; //put fonts in with a space in between
			}
		}
	}
}
