package com.tycoon177.chip8.system;

/**
 * Represents a memory address
 * 
 * @author Benjamin McHone
 *
 */
public class Address {
	private int address;

	/**
	 * Creates an address object
	 * 
	 * @param i
	 *            the address location
	 */
	public Address(int i) {
		this.address = i;
	}

	/**
	 * Creates an address object based on the string representation
	 * 
	 * @param address
	 *            Address of location as string
	 * @throws NumberFormatException
	 *             thrown when the string isnt a short
	 */
	public Address(String address) throws NumberFormatException {
		this.address = Short.parseShort(address);
	}

	/**
	 * Gets the address
	 * 
	 * @return The address
	 */
	public int getAddress() {
		return address;
	}

	/**
	 * Adds amount to the address
	 * 
	 * @param amount
	 *            The amount to move the address
	 */
	public void addToAddress(int amount) {
		this.address += amount;
	}

	@Override
	public String toString() {
		return "0x" + Integer.toHexString(address);
	}
}
