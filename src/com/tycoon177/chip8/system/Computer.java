package com.tycoon177.chip8.system;

import java.util.Random;
import java.util.Stack;

public class Computer {
	private Display display;
	private Keyboard keyboard;
	private Memory ram;
	private Random rand;
	private Register i;
	private Register[] registers;
	private short programCounter;
	private Stack<Address> returnStack;
	private Timer delay, sound;

	/**
	 * Initializes the CHIP-8 System.
	 */
	public Computer() {
		display = new Display(64, 32);
		keyboard = new Keyboard();
		ram = new Memory(4096);
		registers = new Register[16];
		for (int i = 0; i < registers.length; i++) {
			registers[i] = new Register(1);
		}
		i = new Register(2);
		returnStack = new Stack<>();
		delay = new Timer();
		sound = new Timer();
		programCounter = 0x200;// Program execution starts at 0x200
		rand = new Random();
	}

	private void opcode_0NNN(Address address) {
		// Calls RCA 1802 program at address NNN. Not necessary for most ROMs.
	}

	/**
	 * Clears the screen
	 */
	private void opcode_00E0() {
		display.cls();
	}

	/**
	 * Acts as RET in asm. Returning to the last item on the stack.
	 */
	private void opcode_00EE() {
		programCounter = returnStack.pop().getAddress();
	}

	/**
	 * JMP in asm. This jumps to a specific memory address.
	 * 
	 * @param address
	 *            The address to jump to.
	 */
	private void opcode_1NNN(Address address) {
		this.programCounter = address.getAddress();
	}

	/**
	 * CALL in asm. Calls a subroutine at a specific address.
	 * 
	 * @param address
	 *            The address of the subroutine.
	 */
	private void opcode_2NNN(Address address) {
		returnStack.push(new Address(programCounter));
		opcode_1NNN(address);
	}

	/**
	 * If the value in register x matches value, skip the next instruction.
	 * 
	 * @param x
	 *            The register in question
	 * @param value
	 *            The value to be checked
	 */
	private void opcode_3XNN(Register x, short value) {
		if (x.getValue() == value) {
			programCounter++;
		}
	}

	/**
	 * If the value in register x does not match the value, skip the next
	 * instruction.
	 * 
	 * @param x
	 *            The register in question
	 * @param value
	 *            The value to be checked
	 */
	private void opcode_4XNN(Register x, short value) {
		if (x.getValue() != value) {
			programCounter++;
		}
	}

	/**
	 * Checks for equality and skips the next instruction if the two registers
	 * contain the same value.
	 * 
	 * @param x
	 *            the first register
	 * @param y
	 *            the second register
	 */
	private void opcode_5XY0(Register x, Register y) {
		if (x.getValue() == y.getValue()) {
			programCounter++;
		}
	}

	/**
	 * Sets the value of register x to value
	 * 
	 * @param x
	 *            the Register to be changed
	 * @param value
	 *            the value that is put into Register
	 */
	private void opcode_6XNN(Register x, short value) {
		x.setValue(value);
	}

	/**
	 * Adds value to the current register's value.
	 * 
	 * @param x
	 *            the register
	 * @param value
	 *            the value to add to the register
	 */
	private void opcode_7XNN(Register x, short value) {
		short val = (short) (x.getValue() + value);
		x.setValue(val);
	}

	/**
	 * Sets the value in register VX to that in VY
	 * 
	 * @param x
	 *            The first register
	 * @param y
	 *            The register to get the value from
	 */
	private void opcode_8XY0(Register x, Register y) {
		x.setValue(y.getValue());
	}

	/**
	 * Sets VX to VY or YX
	 * 
	 * @param x
	 *            First Register
	 * @param y
	 *            Second Register
	 */
	private void opcode_8XY1(Register x, Register y) {
		x.setValue((short) (x.getValue() | y.getValue()));
	}

	/**
	 * Sets VX to VY & VX (Bitwise and)
	 * 
	 * @param x
	 *            First register (VX)
	 * @param y
	 *            Second register (VY)
	 */
	private void opcode_8XY2(Register x, Register y) {
		x.setValue((short) (x.getValue() & y.getValue()));
	}

	/**
	 * Sets VX to VY ^ VX (Bitwise xor)
	 * 
	 * @param x
	 *            First register (VX)
	 * @param y
	 *            Second register (VY)
	 */
	private void opcode_8XY3(Register x, Register y) {
		x.setValue((short) (x.getValue() ^ y.getValue()));
	}

	/**
	 * Adds VY to VX and sets VF to 1 if there is a carry.
	 * 
	 * @param x
	 *            First register
	 * @param y
	 *            Second register
	 */
	private void opcode_8XY4(Register x, Register y) {
		int val = x.getValue() + y.getValue();
		x.setValue((short) val);
		// Sets to 1 if there has been a carry
		short vf = (short) (val == x.getValue() ? 0 : 1);
		registers[0xF].setValue(vf);
	}

	/**
	 * Subtracts VY from VX and sets VF to 1 if there is a carry.
	 * 
	 * @param x
	 *            First register
	 * @param y
	 *            Second register
	 */
	private void opcode_8XY5(Register x, Register y) {
		int val = x.getValue() - y.getValue();
		x.setValue((short) val);
		// Sets to 1 if there has been a carry
		short vf = (short) (val == x.getValue() ? 0 : 1);
		registers[0xF].setValue(vf);
	}

	/**
	 * Shifts register x to the right 1 (Divide by 2) and then sets vf to the
	 * least significant bit of x before the change (0 or 1)
	 * 
	 * @param x
	 *            The register to shift right
	 */
	private void opcode_8XY6(Register x) {
		short value = x.getValue();
		value = (short) (value >> 1);
		registers[0xF].setValue((short) (value & 0xFF));
	}

	/**
	 * Subtracts VX from VY (Sets the result to VX) and sets VF to 1 if there is
	 * a carry.
	 * 
	 * @param x
	 *            First register
	 * @param y
	 *            Second register
	 */
	private void opcode_8XY7(Register x, Register y) {
		int val = y.getValue() - x.getValue();
		x.setValue((short) val);
		// Sets to 1 if there has been a carry
		short vf = (short) (val == x.getValue() ? 0 : 1);
		registers[0xF].setValue(vf);
	}

	/**
	 * Shifts Register x left by 1 and sets VF to the MSB
	 * 
	 * @param x
	 *            The register to shift
	 */
	private void opcode_8XYE(Register x) {
		short value = x.getValue();
		value = (short) (value << 1);
		registers[0xF].setValue((short) ((value & 0xFF) >> 7));
	}

	/**
	 * Skips the next instruction if VX doesn't equal VY.
	 * 
	 * @param x
	 *            The first register (VX)
	 * @param y
	 *            The second register (VY)
	 */
	private void opcode_9XY0(Register x, Register y) {
		if (x.getValue() == y.getValue()) {
			programCounter++;
		}
	}

	/**
	 * Sets I to the address NNN.
	 * 
	 * @param address
	 *            The address to set I as
	 */
	private void opcode_ANNN(Address address) {
		i.setValue(address.getAddress());
	}

	/**
	 * Jumps to the address NNN plus V0.
	 * 
	 * @param address
	 *            Address to be added to the value of V0
	 */
	private void opcode_BNNN(Address address) {
		this.programCounter = (short) (registers[0].getValue() + address.getAddress());
	}

	/**
	 * Sets VX to the result of a bitwise and (&) operation on a random number
	 * and val
	 * 
	 * @param x
	 *            Register VX
	 * @param val
	 *            The value to be (&) with the random number
	 */
	private void opcode_CXNN(Register x, short val) {
		short value = (short) (val & rand.nextInt(256));
		x.setValue(value);
	}

	private void opcode_DXYN(Register x, Register y, short value) {
		// Sprites stored in memory at location in index register (I), 8bits
		// wide. Wraps around the screen. If when drawn, clears a pixel,
		// register VF is set to 1 otherwise it is zero. All drawing is XOR
		// drawing (i.e. it toggles the screen pixels). Sprites are drawn
		// starting at position VX, VY. N is the number of 8bit rows that need
		// to be drawn. If N is greater than 1, second line continues at
		// position VX, VY+1, and so on.
	}

	private void opcode_EX9E() {
		// Skips the next instruction if the key stored in VX is pressed.
	}

	private void opcode_EXA1() {
		// Skips the next instruction if the key stored in VX isn't pressed.
	}

	/**
	 * Sets VX to the value of the delay timer
	 * 
	 * @param x
	 *            The register, VX
	 */
	private void opcode_FX07(Register x) {
		x.setValue(delay.getValue());
	}

	private void opcode_FX0A() {
		// A key press is awaited, and then stored in VX.
	}

	/**
	 * Sets the delay timer to VX
	 * 
	 * @param x
	 *            The register VX
	 */
	private void opcode_FX15(Register x) {
		delay.setTimer(x.getValue());
	}

	/**
	 * Sets the sound timer to VX
	 * 
	 * @param x
	 *            The register VX
	 */
	private void opcode_FX18(Register x) {
		sound.setTimer(x.getValue());
	}

	/**
	 * Adds VX to I
	 * 
	 * @param x
	 *            The VX register
	 */
	private void opcode_FX1E(Register x) {
		short value = (short) (i.getValue() + x.getValue());
		i.setValue(value);
	}

	private void opcode_FX29() {
		// Sets I to the location of the sprite for the character in VX.
		// Characters 0-F (in hexadecimal) are represented by a 4x5 font.
	}

	private void opcode_FX33() {
		// Stores the Binary-coded decimal representation of VX, with the most
		// significant of three digits at the address in I, the middle digit at
		// I plus 1, and the least significant digit at I plus 2. (In other
		// words, take the decimal representation of VX, place the hundreds
		// digit in memory at location in I, the tens digit at location I+1, and
		// the ones digit at location I+2.)
	}

	/**
	 * Stores V0-VX in consecutive memory
	 * 
	 * @param x
	 *            VX
	 */
	private void opcode_FX55(Register x) {
		// Stores V0 to VX in memory starting at address I.
		Address address = new Address(i.getValue());
		byte value;
		for (int j = 0; j < registers.length && registers[j] != x; j++) {
			value = (byte) registers[j].getValue();
			ram.setMemory(address, value);
			address.addToAddress((short) 0x1);
		}
	}

	/**
	 * Reads memory starting at memory address in i and store in V0-VX
	 * 
	 * @param x
	 *            The ending register
	 */
	private void opcode_FX65(Register x) {
		Address address = new Address(i.getValue());
		short value;
		for (int j = 0; j < registers.length && registers[j] != x; j++) {
			value = ram.getMemory(address);
			registers[j].setValue(value);
			address.addToAddress((short) 0x1);
		}
	}

	public void emulationCycle() {
		evaluateOpcode();
		sound.updateTimer();
		delay.updateTimer();
		programCounter++;
	}

	private void evaluateOpcode() {
		Address loc = new Address(programCounter);
		short opcode = ram.getMemory(loc);
		loc.addToAddress(1);
		opcode = (short) ((opcode << 8) | ram.getMemory(loc)); // Full opcode
		evaluateOpcode(opcode);
	}

	private void evaluateOpcode(short opcode) {
		Register x, y;
		short value;
		switch ((opcode & 0xf000) >> 8) {
			case 0x0000:
				execute0NNNOpcodes(opcode);
				break;
			case 0x1000:
				opcode_1NNN(new Address((short) (opcode & 0x0FFF)));
				break;
			case 0x2000:
				opcode_2NNN(new Address((short) (opcode & 0x0FFF)));
				break;
			case 0x3000:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				value = (short) (opcode & 0xff);
				opcode_3XNN(x, value);
				break;
			case 0x4000:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				value = (short) (opcode & 0xff);
				opcode_4XNN(x, value);
				break;
			case 0x5000:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				y = getRegister((byte) ((opcode & 0x00f0) >> 4));
				opcode_5XY0(x, y);
				break;
			case 0x6000:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				value = (short) (opcode & 0xff);
				opcode_6XNN(x, value);
				break;
			case 0x7000:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				value = (short) (opcode & 0xff);
				opcode_7XNN(x, value);
				break;
			case 0x8000:
				execute8XYNOpcodes(opcode);
				break;
			case 0x9000:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				y = getRegister((byte) ((opcode & 0x00f0) >> 4));
				opcode_9XY0(x, y);
				break;
			case 0xA000:
				value = (short) (opcode & 0xFFF);
				opcode_ANNN(new Address(value));
				break;
			case 0xB000:
				value = (short) (opcode & 0xFFF);
				opcode_BNNN(new Address(value));
				break;
			case 0xC000:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				value = (short) (opcode & 0xFF);
				opcode_CXNN(x, value);
			case 0xD000:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				y = getRegister((byte) ((opcode & 0x00f0) >> 4));
				value = (short) (opcode & 0xf);
				opcode_DXYN(x, y, value);
				break;
			case 0xE000:
				switch (opcode & 0xff) {
					case 0x9E:
						opcode_EX9E();
						break;
					case 0xA1:
						opcode_EXA1();
						break;
					default:
						System.out.println("UNKNOWN OPCODE: 0x" + Integer.toHexString(opcode));
				}
				break;
			case 0xF000:
				executeFXNNOpcodes(opcode);
				break;
			default:
				System.out.println("UNKNOWN OPCODE: 0x" + Integer.toHexString(opcode));
		}
	}

	private void execute0NNNOpcodes(short opcode) {
		switch (opcode & 0xFF) {
			case 0x00E0:
				opcode_00E0();
				break;
			case 0x00EE:
				opcode_00EE();
				break;
			default:
				opcode_0NNN(new Address((short) (opcode & 0x0FFF)));
		}
	}

	private void execute8XYNOpcodes(short opcode) {
		Register x = getRegister((byte) ((opcode & 0x0f00) >> 8));
		Register y = getRegister((byte) ((opcode & 0x00f0) >> 4));
		switch (opcode & 0xF) {
			case 0x0:
				opcode_8XY0(x, y);
				break;
			case 0x1:
				opcode_8XY1(x, y);
				break;
			case 0x2:
				opcode_8XY2(x, y);
				break;
			case 0x3:
				opcode_8XY3(x, y);
				break;
			case 0x4:
				opcode_8XY4(x, y);
				break;
			case 0x5:
				opcode_8XY5(x, y);
				break;
			case 0x6:
				opcode_8XY6(x);
				break;
			case 0x7:
				opcode_8XY7(x, y);
				break;
			case 0xE:
				opcode_8XYE(x);
				break;
			default:
				System.out.println("UNKNOWN OPCODE: 0x" + Integer.toHexString(opcode));
		}
	}

	private void executeFXNNOpcodes(short opcode) {
		Register x = getRegister((byte) ((opcode & 0x0f00) >> 8));
		switch (opcode & 0xFF) {
			case 0x07:
				opcode_FX07(x);
				break;
			case 0x0A:
				opcode_FX0A();
				break;
			case 0x15:
				opcode_FX15(x);
				break;
			case 0x18:
				opcode_FX18(x);
				break;
			case 0x1E:
				opcode_FX1E(x);
				break;
			case 0x29:
				opcode_FX29();
				break;
			case 0x33:
				opcode_FX33();
				break;
			case 0x55:
				opcode_FX55(x);
				break;
			case 0x65:
				opcode_FX65(x);
				break;
			default:
				System.out.println("UNKNOWN OPCODE: 0x" + Integer.toHexString(opcode));
		}
	}

	public Register getRegister(byte identifier) {
		return registers[identifier];
	}

}
