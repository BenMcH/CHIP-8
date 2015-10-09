package com.tycoon177.chip8.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import com.tycoon177.chip8.system.Display;

public class Screen extends JComponent {
	private Display display;

	public Screen(Display display) {
		super();
		this.display = display;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(Color.white);
		int cellWidth = getWidth() / display.getWidth();
		int cellHeight = getHeight() / display.getHeight();
		for (int i = 0; i < display.getHeight(); i++) {
			boolean[] screen = display.getScreen();
			for (int j = 0; j < display.getWidth(); j++) {
				if(screen[i*display.getWidth() + j]){
					g2.drawRect(j*cellWidth, i*cellHeight, cellWidth, cellHeight);
				}
			}
		}
	}

}
