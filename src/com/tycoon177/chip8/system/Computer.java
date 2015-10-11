package com.tycoon177.chip8.system;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Stack;
import java.util.zip.DataFormatException;

import javax.swing.JFileChooser;

public class Computer implements Runnable {
	private Display display;
	private Keyboard keyboard;
	private long time;
	private Memory ram;
	private Random rand;
	private Register i;
	private Register[] registers;
	private Stack<Address> returnStack;
	private Timer delay, sound;
	private Thread program;
	private int programCounter;
	private int startPlace;
	private int romLength;
	private boolean keepRunning;

	/**
	 * Initializes the CHIP-8 System.
	 */
	public Computer() {
		display = new Display(64, 32);
		keyboard = new Keyboard();
		ram = new Memory(4096);
		registers = new Register[16];
		for (int i = 0; i < registers.length; i++) {
			registers[i] = new Register(Integer.toHexString(i));
		}
		i = new Register(2, "i");
		returnStack = new Stack<>();
		delay = new Timer();
		sound = new Timer();
		rand = new Random();
		resetComputerState();
	}

	private void resetComputerState() {
		programCounter = 0x200;// Program execution starts at 0x200
		startPlace = 0x200;
		delay.setTimer(0);
		sound.setTimer(0);
		for (Register x : registers) {
			x.setValue(0);
		}
		i.setValue(0);
		while (!returnStack.empty()) {
			returnStack.pop();
		}
		keyboard.reset();
		display.cls();
		ram.clearMemory();

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
		programCounter = returnStack.pop().getAddress() - 2;
	}

	/**
	 * JMP in asm. This jumps to a specific memory address.
	 * 
	 * @param address
	 *            The address to jump to.
	 */
	private void opcode_1NNN(Address address) {
		this.programCounter = address.getAddress() - 2;
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
	private void opcode_3XNN(Register x, int value) {
		if (x.getValue() == value) {
			programCounter += 2;
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
	private void opcode_4XNN(Register x, int value) {
		if (x.getValue() != value) {
			programCounter += 2;
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
			programCounter += 2;
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
	private void opcode_6XNN(Register x, int value) {
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
	private void opcode_7XNN(Register x, int value) {
		int val = (x.getValue() + value);
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
		x.setValue(x.getValue() | y.getValue());
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
		x.setValue(x.getValue() & y.getValue());
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
		x.setValue(x.getValue() ^ y.getValue());
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
		int vf = (val > 0xff ? 1 : 0);
		x.setValue(val & 0xff); // & 256 for modulous
		// Sets to 1 if there has been a carry
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
		registers[0xF].setValue(x.getValue() > y.getValue() ? 0x1 : 0x0);
		int val = x.getValue() - y.getValue();
		x.setValue(val & 0xff);
	}

	/**
	 * Shifts register x to the right 1 (Divide by 2) and then sets vf to the
	 * least significant bit of x before the change (0 or 1)
	 * 
	 * @param x
	 *            The register to shift right
	 */
	private void opcode_8XY6(Register x) {
		int value = x.getValue();
		registers[0xF].setValue((short) (value & 0x1));
		value = (short) (value >> 1);
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
		x.setValue(val & 0xff);
		// Sets to 1 if there has been a carry
		int vf = y.getValue() > x.getValue() ? 0x1 : 0x0;
		registers[0xF].setValue(vf);
	}

	/**
	 * Shifts Register x left by 1 and sets VF to the MSB
	 * 
	 * @param x
	 *            The register to shift
	 */
	private void opcode_8XYE(Register x) {
		int value = x.getValue();
		registers[0xF].setValue((value & 0xF0) >> 4);
		value = (value << 1);
		x.setValue(value);
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
		if (x.getValue() != y.getValue()) {
			programCounter += 2;
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
		this.programCounter = (registers[0].getValue() + address.getAddress()) - 2;
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
	private void opcode_CXNN(Register x, int val) {
		int value = (val & rand.nextInt(256));
		x.setValue(value);
	}

	/**
	 * Display n-byte sprite starting at memory location I at (Vx, Vy), set VF =
	 * collision.
	 * 
	 * @param x
	 *            Vx
	 * @param y
	 *            Vy
	 * @param height
	 *            Height of sprite
	 */
	private void opcode_DXYN(Register x, Register y, int height) {
		int xLoc = x.getValue();
		int yLoc = y.getValue();
		boolean turnedOff = false;
		int value;
		Address address = new Address(i.getValue());
		registers[0xF].setValue((short) 0);
		for (int i = 0; i < height; i++) {
			value = ram.getMemory(address);
			address.addToAddress(1);
			turnedOff |= display.draw(xLoc, yLoc + i, value);
		}
		if (turnedOff) {
			registers[0xF].setValue(1);
		}
	}

	/**
	 * Skips the next instruction if the key stored in VX is pressed.
	 * 
	 * @param x
	 *            The register holding the value of the key
	 */
	private void opcode_EX9E(Register x) {
		if (keyboard.getKeyPressed(x.getValue())) {
			programCounter += 2;
		}
	}

	/**
	 * Skips the next instruction if the key stored in VX isn't pressed.
	 * 
	 * @param x
	 *            The register with the key value
	 */
	private void opcode_EXA1(Register x) {
		if (!keyboard.getKeyPressed(x.getValue())) {
			programCounter += 2;
		}
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

	/**
	 * A key press is awaited, and then stored in VX.
	 * 
	 * @param x
	 *            Register VX
	 */
	private void opcode_FX0A(Register x) {
		int value = keyboard.waitForKeyPress();
		x.setValue(value);
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
		int value = (i.getValue() + x.getValue());
		i.setValue(value);
	}

	/**
	 * Sets the pointer to the location of the sprite cooresponding to the hex
	 * code held in VX
	 * 
	 * @param x
	 *            VX
	 */
	private void opcode_FX29(Register x) {
		int val = x.getValue() & 0xf; // If value isnt 0-f force it to be
		i.setValue(val * 5); // 5 entries for the rows and an empty.
	}

	/**
	 * Stores the value in the register VX as each individual value. (Hundreds,
	 * tens, and ones)
	 * 
	 * @param x
	 *            Register VX
	 */
	private void opcode_FX33(Register x) {
		Address addr = new Address(i.getValue());
		int value = x.getValue();
		int hundreds = (value / 100);
		int tens = ((value - hundreds) / 10);
		int ones = ((value - hundreds - tens));
		ram.setMemory(addr, hundreds);
		addr.addToAddress(1);
		ram.setMemory(addr, tens);
		addr.addToAddress(1);
		ram.setMemory(addr, ones);
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
		int value;
		for (int j = 0; j < registers.length && registers[j] != x; j++) {
			value = registers[j].getValue();
			ram.setMemory(address, value);
			address.addToAddress((short) 0x1);
		}
		ram.setMemory(address, x.getValue());
	}

	/**
	 * Reads memory starting at memory address in i and store in V0-VX
	 * 
	 * @param x
	 *            The ending register
	 */
	private void opcode_FX65(Register x) {
		Address address = new Address(i.getValue());
		int value;
		for (int j = 0; j < registers.length && registers[j] != x; j++) {
			value = ram.getMemory(address);
			registers[j].setValue(value);
			address.addToAddress((short) 0x1);
		}
		x.setValue(ram.getMemory(address));
	}

	public void emulationCycle() {
		evaluateOpcode();
		sound.updateTimer();
		delay.updateTimer();
		programCounter += 2;
	}

	private void evaluateOpcode() {
		Address loc = new Address(programCounter);
		int opcode = ram.getMemory(loc);
		loc.addToAddress(1);
		opcode = createOpcode(opcode, ram.getMemory(loc));
		// System.out.println("Evaluating Opcode: 0x" +
		// Integer.toHexString(opcode));
		evaluateOpcode(opcode);
		// System.out.println("Evaluated Opcode: 0x" +
		// Integer.toHexString(opcode));
	}

	public void evaluateOpcode(int opcode) {
		System.out.println("Evaluating " + Integer.toHexString(opcode));
		opcode &= 0xffff;
		Register x, y;
		short value;
		switch ((opcode & 0xf000) >> 12) {
			case 0x0:
				execute0NNNOpcodes(opcode);
				break;
			case 0x1:
				opcode_1NNN(new Address((short) (opcode & 0x0FFF)));
				break;
			case 0x2:
				opcode_2NNN(new Address((short) (opcode & 0x0FFF)));
				break;
			case 0x3:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				value = (short) (opcode & 0xff);
				opcode_3XNN(x, value);
				break;
			case 0x4:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				value = (short) (opcode & 0xff);
				opcode_4XNN(x, value);
				break;
			case 0x5:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				y = getRegister((byte) ((opcode & 0x00f0) >> 4));
				opcode_5XY0(x, y);
				break;
			case 0x6:
				int register = ((opcode & 0x0f00) >> 8);
				x = getRegister(register);
				value = (short) (opcode & 0xff);
				opcode_6XNN(x, value);
				break;
			case 0x7:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				value = (short) (opcode & 0xff);
				opcode_7XNN(x, value);
				break;
			case 0x8:
				execute8XYNOpcodes(opcode);
				break;
			case 0x9:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				y = getRegister((byte) ((opcode & 0x00f0) >> 4));
				opcode_9XY0(x, y);
				break;
			case 0xA:
				value = (short) (opcode & 0xFFF);
				opcode_ANNN(new Address(value));
				break;
			case 0xB:
				value = (short) (opcode & 0xFFF);
				opcode_BNNN(new Address(value));
				break;
			case 0xC:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				value = (short) (opcode & 0xFF);
				opcode_CXNN(x, value);
			case 0xD:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				y = getRegister((byte) ((opcode & 0x00f0) >> 4));
				value = (short) (opcode & 0xf);
				opcode_DXYN(x, y, value);
				break;
			case 0xE:
				x = getRegister((byte) ((opcode & 0x0f00) >> 8));
				switch (opcode & 0xff) {
					case 0x9E:
						opcode_EX9E(x);
						break;
					case 0xA1:
						opcode_EXA1(x);
						break;
					default:
						System.out.println("UNKNOWN OPCODE: 0x" + Integer.toHexString(opcode) + " Found at Program location: "
								+ Integer.toHexString(programCounter - startPlace));
				}
				break;
			case 0xF:
				executeFXNNOpcodes(opcode);
				break;
			default:
				System.out.println("UNKNOWN OPCODE: 0x" + Integer.toHexString(opcode) + " Found at Program location: "
						+ Integer.toHexString(programCounter - startPlace));
		}
	}

	private void execute0NNNOpcodes(int opcode) {
		switch (opcode & 0xFF) {
			case 0x00E0:
				opcode_00E0();
				break;
			case 0x00EE:
				opcode_00EE();
				break;
			default:
				System.out.println("UNKNOWN OPCODE: 0x" + Integer.toHexString(opcode) + " Found at Program location: "
						+ Integer.toHexString(programCounter - startPlace));
		}
	}

	private void execute8XYNOpcodes(int opcode) {
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
				System.out.println("UNKNOWN OPCODE: 0x" + Integer.toHexString(opcode) + " Found at Program location: "
						+ Integer.toHexString(programCounter - startPlace));
		}
	}

	private void executeFXNNOpcodes(int opcode) {
		Register x = getRegister((byte) ((opcode & 0x0f00) >> 8));
		switch (opcode & 0xFF) {
			case 0x07:
				opcode_FX07(x);
				break;
			case 0x0A:
				opcode_FX0A(x);
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
				opcode_FX29(x);
				break;
			case 0x33:
				opcode_FX33(x);
				break;
			case 0x55:
				opcode_FX55(x);
				break;
			case 0x65:
				opcode_FX65(x);
				break;
			default:
				System.out.println("UNKNOWN OPCODE: 0x" + Integer.toHexString(opcode) + " Found at Program location: "
						+ Integer.toHexString(programCounter - startPlace));

		}
	}

	public Register getRegister(int j) {
		return registers[j];
	}

	public void loadRom() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		int returnVal = chooser.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				Rom rom = new Rom(chooser.getSelectedFile().getAbsolutePath());
				loadRom(rom);
			} catch (DataFormatException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadRom(Rom rom) {
		ejectRom();
		int[] data = rom.getRom();
		romLength = data.length;
		Address address = new Address(0x200);
		for (int i = 0; i < data.length; i++) {
			ram.setMemory(address, data[i]);
			address.addToAddress(1);
		}
		program = new Thread(this);
		playRom();
	}

	public void playRom() {
		display.cls();
		keepRunning = true;
		program.start();
	}

	private void ejectRom() {

		if (program != null) {
			keepRunning = false;
			try {
				program.join(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		resetComputerState();
	}

	@Override
	public void run() {
		System.out.println("Rom Length: " + Integer.toHexString(romLength));
		while (keepRunning) {
			time = System.currentTimeMillis();
			emulationCycle();
			while (System.currentTimeMillis() - time < 1000.0 / 60.0)
				;
		}
//		System.out.println(Integer.toHexString(programCounter - startPlace));

	}

	public Display getDisplay() {
		return display;
	}

	private int createOpcode(int msb, int lsb) {
		int opcode = msb;
		opcode <<= 8;
		opcode |= lsb;
		return opcode;
	}

	/**
	 * Gets the keyboard object for this computer object
	 * 
	 * @return The keyboard
	 */
	public Keyboard getKeyboard() {
		return keyboard;
	}

}
