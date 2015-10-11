package com.tycoon177.chip8.ui;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import com.tycoon177.chip8.system.Computer;
import com.tycoon177.chip8.system.Display;

public class SystemDisplay extends JFrame implements KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7868537628790047711L;
	private Computer computer;
	private Display screen;

	public SystemDisplay(Computer computer) {
		super("CHIP-8 Display");
		this.computer = computer;
		this.screen = computer.getDisplay();
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		setContentPane(panel);
		getContentPane().add(screen, BorderLayout.CENTER);
		getContentPane().add(new KeyboardPanel(computer.getKeyboard()), BorderLayout.SOUTH);
		makeMenuBar();
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		addKeyListener(this);
		setFocusable(true);
	}

	private void makeMenuBar() {
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem loadRom = new JMenuItem("Load Rom");
		file.add(loadRom);
		bar.add(file);
		loadRom.addActionListener(e->computer.loadRom());
		setJMenuBar(bar);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("Key pressed");
		int key = computer.getKeyboard().standardKeyboardToHex(e.getKeyCode());
		if (key != -1) {
			computer.getKeyboard().setKeyPressed(key, true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = computer.getKeyboard().standardKeyboardToHex(e.getKeyCode());
		if (key != -1) {
			computer.getKeyboard().setKeyPressed(key, false);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}
