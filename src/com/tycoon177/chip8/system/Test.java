package com.tycoon177.chip8.system;

import java.io.IOException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class Test {

	public static void main(String[] args) throws DataFormatException, IOException {
		Computer comp = new Computer();
		Scanner in = new Scanner(System.in);
		short inp = 0;
		while(inp!= 432){
			comp.evaluateOpcode((inp=Short.parseShort(in.next())));
		}/*
		Rom rom = new Rom("C:\\Users\\Ben\\Desktop\\IBM.ch8");
		comp.loadRom(rom);
		comp.run();*/
	}

}
