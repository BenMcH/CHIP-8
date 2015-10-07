package com.tycoon177.chip8.system;

public class Address {
	private short address;

	/**
	 * Creates an address object
	 * @param address the address location
	 */
	public Address(short address) {
		this.address = address;
	}

	/**
	 * Creates an address object based on the string representation
	 * @param address Address of location as string
	 * @throws NumberFormatException thrown when the string isnt a short
	 */
	public Address(String address) throws NumberFormatException {
		this.address = Short.parseShort(address);
	}

	/**
	 * Gets the address
	 * @return The address
	 */
	public short getAddress() {
		return address;
	}

	/**
	 * Adds amount to the address
	 * @param amount The amount to move the address
	 */
	public void addToAddress(int amount) {
		this.address += amount;
	}
}
