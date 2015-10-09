package com.tycoon177.chip8.system;

import java.io.IOException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

import com.tycoon177.chip8.ui.Screen;
import com.tycoon177.chip8.ui.Window;

public class Test {

	public static void main(String[] args){
		Computer comp = new Computer();
		Scanner in = new Scanner(System.in);
		System.out.println("1 for IBM, 2 for input of opcodes");
		Screen screen = new Screen(comp.getDisplay());
		Window w = new Window(screen);
		w.setSize(100, 100);
		w.setVisible(true);
		if (in.nextInt() == 1) {
			Rom rom = null;
			try {
				rom = new Rom("IBM.ch8");
			} catch (DataFormatException e) {
				System.out.println("Rom was corrupt");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Rom could not be found or read.");
				e.printStackTrace();
			}
			//int[] romdata = rom.getRom();
			
			comp.loadRom(rom);
			new Thread(comp).run();
		} else {
			int opcode = 0;
			while (opcode != 0x300) {
				opcode = in.nextInt(16);
				comp.evaluateOpcode(opcode);
				System.out.println("Evaluated 0x" + Integer.toHexString(opcode));
			}
		}
		in.close();
	}

}
