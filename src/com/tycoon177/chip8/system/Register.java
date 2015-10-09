package com.tycoon177.chip8.system;

public class Register {
	private int numBytes;
	private short value;

	/**
	 * Creates a standard 1 byte register.
	 */
	public Register() {
		this.numBytes = 1;
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
	public Register(int numOfBytes) throws IllegalArgumentException {
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
	public short getValue() {
		if (numBytes == 1) {
			return (byte) value;
		}
		return value;
	}

	/**
	 * Sets the value within the register
	 * 
	 * @param i
	 *            Value to be set
	 */
	public void setValue(short val) {
		int num = 256;
		if (numBytes == 2) {
			num *= num;
		}
		value = (short) (val & num);
	}

	/**
	 * Sets the value within the register (Will be cast to short)
	 * 
	 * @param i
	 *            Value to be set
	 */
	public void setValueInt(int i) {
		setValue((short) i);
	}

}
