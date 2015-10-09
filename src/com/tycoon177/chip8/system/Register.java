package com.tycoon177.chip8.system;

public class Register {
	private int numBytes;
	private int value;
	private String name;

	/**
	 * Creates a standard 1 byte register.
	 */
	public Register(String name) {
		this(1, name);
	}

	/**
	 * Creates a register with either 1 or 2 bytes.
	 * 
	 * @param numOfBytes
	 *            The number of bytes that this register can be (1 or 2)
	 * @throws IllegalArgumentException
	 *             Thrown when trying to give more bytes of memory to a
	 *             register.
	 */
	public Register(int numOfBytes, String name) throws IllegalArgumentException {
		this.name = name;
		this.numBytes = numOfBytes;
		if (numOfBytes != 1 && numOfBytes != 2) {
			throw new IllegalArgumentException("The registers may only be 1 or 2 bytes.");
		}
	}

	/**
	 * Gets the value within the register
	 * 
	 * @return The value in the register.
	 */
	public int getValue() {
		if (numBytes == 1) {
			return value & 0xFF;
		}
		return value & 0xFFFF;
	}


	/**
	 * Sets the value within the register (Will be cast to short)
	 * 
	 * @param i
	 *            Value to be set
	 */
	public void setValue(int i) {
		this.value = i & 0xFFFF;
	}
	
	@Override
	public String toString(){
		return name + ":" + value;
	}

}
