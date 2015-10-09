package com.tycoon177.chip8.ui;

import javax.swing.JFrame;

public class Window extends JFrame {
	private Screen screen;

	public Window(Screen screen) {
		super();
		this.screen = screen;
		this.getContentPane().add(screen);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
