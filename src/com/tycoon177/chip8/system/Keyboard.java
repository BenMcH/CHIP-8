package com.tycoon177.chip8.system;

public class Keyboard {
	private boolean[] keys;
	private boolean keyPressed;
	private int lastKeyPressed;

	public Keyboard() {
		keys = new boolean[16];
	}

	public boolean getKeyPressed(int index) {
		return keys[index];
	}

	public void setKeyPressed(int index, boolean state) {
		if (state) {
			keyPressed = true;
			lastKeyPressed = index;
		}
		keys[index] = state;
	}

	public int waitForKeyPress() {
		keyPressed = false;
		while (!keyPressed) {
		}
		return lastKeyPressed;
	}

}
