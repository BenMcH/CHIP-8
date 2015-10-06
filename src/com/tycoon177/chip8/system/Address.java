package com.tycoon177.chip8.system;

public class Address {
	private short address;

	public Address(short address) {
		this.address = address;
	}

	public Address(String address) throws NumberFormatException {
		this.address = Short.parseShort(address);
	}

	public short getAddress() {
		return address;
	}

	public void addToAddress(int amount) {
		this.address += amount;
	}
}
