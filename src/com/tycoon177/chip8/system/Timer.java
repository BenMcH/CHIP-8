package com.tycoon177.chip8.system;

/**
 * Symbolizes a timer that counts down at 60 Hz
 * 
 * @author Benjamin McHone
 *
 */
public class Timer {
	private int value;
	private long time;

	/**
	 * Checks if the time has been over a second and takes one from the timer
	 */
	public void updateTimer() {
		if ((System.currentTimeMillis() - time) / 60.0 >= 1) {
			if (value > 0) {
				value--;
			}
			time = System.currentTimeMillis();
		}
	}

	/**
	 * Returns the value of the timer
	 * 
	 * @return Value on the timer
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Sets the value of time timer
	 * 
	 * @param i
	 *            Sets the value on the timer
	 */
	public void setTimer(int i) {
		this.value = i;
	}
}
