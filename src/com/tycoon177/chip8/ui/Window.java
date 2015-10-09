package com.tycoon177.chip8.ui;

import javax.swing.JFrame;

import com.tycoon177.chip8.system.Display;

public class Window extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7868537628790047711L;
	private Display screen;

	public Window(Display screen) {
		super("CHIP-8 Display");
		this.screen = screen;
		add(screen);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
	}
}
