package com.tycoon177.chip8.system;

public class Timer {
	private int value;
	private long time;

	public void updateTimer() {
		if ((System.currentTimeMillis() - time) / 60.0 > 1) {
			if (value > 0) {
				value--;
			}
			time = System.currentTimeMillis();
		}
	}

	public int getValue() {
		return value;
	}

	public void setTimer(int i) {
		this.value = i;
	}
}
