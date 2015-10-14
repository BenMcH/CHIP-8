package com.tycoon177.chip8.system;

import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * Stores the states for the 16 key keypad
 * 
 * @author Benjamin McHone
 *
 */
public class Keyboard {
	private boolean[] keys;
	private volatile boolean keyPressed;
	private int lastKeyPressed;
	private HashMap<Integer, Integer> keyboardLookup;

	/**
	 * Constructs the keyboard object
	 */
	public Keyboard() {
		keys = new boolean[16];
		keyboardLookup = new HashMap<>();
		setupKeyboardLookup();
	}

	/**
	 * Sets up the lookup map.
	 */
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

	/**
	 * Returns whether or not the key at index is pressed
	 * 
	 * @param index
	 *            The key pressed
	 * @return Whether or not it is pressed
	 */
	public boolean getKeyPressed(int index) {
		return keys[index];
	}

	/**
	 * Sets the state of a key press
	 * 
	 * @param index
	 *            key to set
	 * @param state
	 *            Whether it is pressed
	 */
	public void setKeyPressed(int index, boolean state) {
		if (state) {
			keyPressed = true;
			lastKeyPressed = index;
		}
		keys[index] = state;
	}

	/**
	 * Pauses execution until a key is pressed
	 * 
	 * @return Which key was pressed
	 */
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

	/**
	 * Gets the key mapping of a normal keyboard to the chip8 keyboard
	 * 
	 * @param keycode
	 *            The keyboard keycode
	 * @return The chip8 keyboard equivalent
	 */
	public int standardKeyboardToHex(int keycode) {
		if (keyboardLookup.containsKey((Integer) keycode)) {
			return keyboardLookup.get(keycode);

		}
		return -1;
	}

	/**
	 * Sets all keys to not pressed.
	 */
	public void reset() {
		for (int i = 0; i < keys.length; i++) {
			keys[i] = false;
		}
	}

}
