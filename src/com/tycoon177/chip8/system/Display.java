package com.tycoon177.chip8.system;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

/**
 * Represents the chip8 display, a 64x32 monochrome screen
 * 
 * @author Benjamin McHone
 *
 */
public class Display extends JComponent {
	private static final long serialVersionUID = 2590723775104624335L;
	// Monochrome screen can be represented by booleans.
	private boolean[] screen;
	private int width, height;

	/**
	 * Creates a screen of a set size
	 * 
	 * @param width
	 *            The width of the display
	 * @param height
	 *            The height of the display
	 */
	public Display(int width, int height) {
		screen = new boolean[width * height];
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(width * 10, height * 10));
	}

	/**
	 * Clears the screen
	 */
	public void cls() {
		fill(false);
	}

	/**
	 * Gets the screen as an array of booleans
	 * 
	 * @return The screen
	 */
	public boolean[] getScreen() {
		return screen;
	}

	/**
	 * Draws a sprite across the 8 pixels, wrapping on the x and y coordinate
	 * planes
	 * 
	 * @param xLoc
	 *            The starting x location for the top left of the sprite
	 * @param yLoc
	 *            The starting y location for the top left of the sprite
	 * @param value
	 *            The sprite
	 * @return Whether or not any pixels were deactivated as result of drawing
	 *         this sprite.
	 */
	public boolean draw(int xLoc, int yLoc, int value) {

		String val = String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(" ", "0");
		boolean turnedOff = false;
		for (int i = 0; i < val.length(); i++) {
			if (val.charAt(i) == '1') {
				int location = ((xLoc + i) % width) + (((yLoc % height) * width));
				if (screen[location]) {
					turnedOff = true;
				}
				screen[location] ^= true;
			}
		}
		repaint();
		return turnedOff;
	}

	/**
	 * Sets the size of the screen
	 */
	@Override
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		boolean[] nScreen = new boolean[width * height];
		System.arraycopy(screen, 0, nScreen, 0, Math.min(nScreen.length, screen.length));
		screen = nScreen;
	}

	/**
	 * Fills the screen with the either on or off pixels
	 * 
	 * @param value
	 *            What to fill the screen with
	 */
	public void fill(boolean value) {
		for (int i = 0; i < screen.length; i++) {
			screen[i] = value;
		}
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(Color.white);
		int cellWidth = getWidth() / width;
		int cellHeight = getHeight() / height;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (screen[i * width + j]) {
					g2.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
				}
			}
		}
	}

	/**
	 * Scrolls the screen down by amount lines
	 * 
	 * @param amount
	 *            Amount to scroll
	 */
	public void scrollDown(int amount) {
		boolean[] nScreen = new boolean[screen.length];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int oldLoc = (x % width) + ((y % height) * width);
				int nLoc = (x % width) + ((y + amount) % height) * width;
				nScreen[nLoc] = screen[oldLoc];
			}
		}
		System.arraycopy(nScreen, 0, screen, 0, screen.length);
		repaint();
	}

	/**
	 * Scrolls the screen to the right
	 * 
	 * @param amount
	 *            Amount of lines to scroll
	 */
	public void scrollRight(int amount) {
		boolean[] nScreen = new boolean[screen.length];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int oldLoc = (x % width) + ((y % height) * width);
				int nLoc = ((x + amount) % width) + (y % height) * width;
				nScreen[nLoc] = screen[oldLoc];
			}
		}
		System.arraycopy(nScreen, 0, screen, 0, screen.length);
		repaint();
	}

	/**
	 * Scrolls the screen to the left by using the scroll left function
	 * 
	 * @param amount
	 *            amount of lines to scroll to the left
	 */
	public void scrollLeft(int amount) {
		scrollRight(width - amount);
	}

	/**
	 * Sets the screen to the standard chip8 screen (64x32)
	 */
	public void setLow() {
		width = 64;
		height = 32;
		screen = new boolean[width * height];
		// setPreferredSize(new Dimension(width, height));
	}

	/**
	 * Sets the screen to the Super Chip 8 screen. (128x64)
	 */
	public void setHigh() {
		width = 128;
		height = 64;
		screen = new boolean[width * height];
		// setPreferredSize(new Dimension(width, height));
	}

}
