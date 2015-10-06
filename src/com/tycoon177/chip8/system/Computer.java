package com.tycoon177.chip8.system;

import java.util.Random;
import java.util.Stack;

public class Computer {
	private Display display;
	private Keyboard keyboard;
	private long time;
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
		registers[0xF].setValue((short) (value & 1));
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
	 * 
	 * @param x
	 *            Register VX
	 */
	private void opcode_CXNN(Register x) {
		short value = (short) (x.getValue() & rand.nextInt(256));
		x.setValue(value);
	}

	private void opcode_DXYN() {
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
		// (value & 0xFF) >> 7)
		// MSB
		switch (opcode) {

			default:
				System.out.println("UNKNOWN OPCODE: 0x" + Integer.toHexString(opcode));
		}
	}

}
