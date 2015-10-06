package com.tycoon177.chip8.system;

public class Display {
	//Monochrome screen can be represented by booleans.
	private boolean[] screen;
	private int width, height;

	public Display(int width, int height) {
		screen = new boolean[width * height];
		this.width = width;
		this.height = height;
	}

	public void cls() {
		for (int i = 0; i < screen.length; i++) {
			screen[i] = false;
		}
	}
	
	

}
