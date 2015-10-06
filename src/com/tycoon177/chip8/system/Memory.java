package com.tycoon177.chip8.system;

public class Memory {
	private byte[] memory;

	/**
	 * Allocates the correct amount of memory for the system.
	 * 
	 * @param amount
	 *            The amount, in bytes, of memory to be allowed to the system.
	 * 
	 */
	public Memory(int amount) {
		memory = new byte[amount];
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
}
