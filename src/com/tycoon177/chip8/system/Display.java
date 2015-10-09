package com.tycoon177.chip8.system;

public class Display {
	// Monochrome screen can be represented by booleans.
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean[] getScreen() {
		return screen;
	}

	/**
	 * Draws a sprite across the 8 pixels, wrapping on the x and y coordinate planes
	 * @param xLoc The starting x location for the top left of the sprite
	 * @param yLoc The starting y location for the top left of the sprite
	 * @param value The sprite
	 * @return Whether or not any pixels were deactivated as result of drawing this sprite.
	 */
	public boolean draw(int xLoc, int yLoc, int value) {
		String val = String.format("%8s", Integer.toBinaryString(value)).replace(" ", "0");
		boolean turnedOff = false;
		for (int i = 0; i < val.length(); i++) {
			if (val.charAt(i) == '1') {
				int location = ((xLoc + i) % width) + ((yLoc * width) % height); //Find X value and add it to the row
				if (screen[location]) {
					turnedOff = true;
				}
				screen[location] ^= true;
			}
		}
		return turnedOff;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		boolean[] nScreen = new boolean[width*height];
		System.arraycopy(screen, 0, nScreen, 0, Math.min(nScreen.length, screen.length));
		screen = nScreen;
	}

}
