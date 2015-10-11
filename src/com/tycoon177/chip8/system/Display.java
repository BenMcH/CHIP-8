package com.tycoon177.chip8.system;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class Display extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2590723775104624335L;
	// Monochrome screen can be represented by booleans.
	private boolean[] screen;
	private int width, height;

	public Display(int width, int height) {
		screen = new boolean[width * height];
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(width * 10, height * 10));
	}

	public void cls() {
		fill(false);
	}

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

	@Override
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		boolean[] nScreen = new boolean[width * height];
		System.arraycopy(screen, 0, nScreen, 0, Math.min(nScreen.length, screen.length));
		screen = nScreen;
	}

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

}
