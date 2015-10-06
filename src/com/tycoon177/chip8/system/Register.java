package com.tycoon177.chip8.system;

public class Register {
	private int numBytes;
	private short value;

	public Register(int numOfBytes) throws IllegalArgumentException {
		this.numBytes = numOfBytes;
		if (numOfBytes != 1 && numOfBytes != 2) {
			throw new IllegalArgumentException("The registers may only be 1 or 2 bytes.");
		}
	}
	
	public short getValue(){
		if(numBytes == 1){
			return (byte)value;
		}
		return value;
	}
	
	public void setValue(Short val){
		if(numBytes == 2){
			if(val > Byte.MAX_VALUE || val < Byte.MIN_VALUE){
				throw new IllegalArgumentException("Value out of bounds");
			}
		}
		value = val;
	}

}
