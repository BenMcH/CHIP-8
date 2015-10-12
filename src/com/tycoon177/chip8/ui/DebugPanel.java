package com.tycoon177.chip8.ui;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.tycoon177.chip8.system.Computer;

public class DebugPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3713836342691410847L;
	
	public DebugPanel(Computer computer){
		super();
		JButton play = new JButton("Play");
		JButton step = new JButton("Step");
		play.addActionListener((l)->computer.playRom());
		play.setFocusable(false);
		step.addActionListener((l)->computer.emulationCycle());
		step.setFocusable(false);
		add(play);
		add(step);
	}
}
