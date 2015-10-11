package com.tycoon177.chip8.system;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Hashtable;

public class Keyboard {
	private boolean[] keys;
	private volatile boolean keyPressed;
	private int lastKeyPressed;
	private Hashtable<Integer, Integer> keyboardLookup;

	public Keyboard() {
		keys = new boolean[16];
		keyboardLookup = new Hashtable<>();
		setupKeyboardLookup();
	}

	private void setupKeyboardLookup() {
		keyboardLookup.put(KeyEvent.VK_1, 0x1);
		keyboardLookup.put(KeyEvent.VK_2, 0x2);
		keyboardLookup.put(KeyEvent.VK_3, 0x3);
		keyboardLookup.put(KeyEvent.VK_4, 0xC);
		keyboardLookup.put(KeyEvent.VK_Q, 0x4);
		keyboardLookup.put(KeyEvent.VK_W, 0x5);
		keyboardLookup.put(KeyEvent.VK_E, 0x6);
		keyboardLookup.put(KeyEvent.VK_R, 0xD);
		keyboardLookup.put(KeyEvent.VK_A, 0x7);
		keyboardLookup.put(KeyEvent.VK_S, 0x8);
		keyboardLookup.put(KeyEvent.VK_D, 0x9);
		keyboardLookup.put(KeyEvent.VK_F, 0xE);
		keyboardLookup.put(KeyEvent.VK_Z, 0xA);
		keyboardLookup.put(KeyEvent.VK_X, 0x0);
		keyboardLookup.put(KeyEvent.VK_C, 0xB);
		keyboardLookup.put(KeyEvent.VK_V, 0xF);
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
			try {
				Thread.sleep(10); // Try not to use ALL the cpu while waiting.
			} catch (InterruptedException e) {
			}
		}
		return lastKeyPressed;
	}

	public int standardKeyboardToHex(int keycode) {
		if (keyboardLookup.containsKey((Integer) keycode)) {
			return keyboardLookup.get(keycode);

		}
		return -1;
	}

	public void reset() {
		Arrays.fill(keys, false);
	}

}
