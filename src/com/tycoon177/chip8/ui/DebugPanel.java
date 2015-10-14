package com.tycoon177.chip8.ui;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.tycoon177.chip8.system.Computer;

/**
 * Represents the debug panel that is used to play stop and step through chip8
 * programs.
 * 
 * @author Benjamin McHone
 *
 */
public class DebugPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3713836342691410847L;

	/**
	 * Constructs the debug panel
	 * 
	 * @param computer
	 *            The computer object to control
	 */
	public DebugPanel(Computer computer) {
		super();
		JButton play = new JButton("Play");
		JButton step = new JButton("Step");
		JButton stop = new JButton("Stop");
		play.addActionListener((l) -> computer.playRom());
		play.setFocusable(false);
		stop.addActionListener((l) -> computer.stop());
		stop.setFocusable(false);
		step.addActionListener((l) -> computer.emulationCycle());
		step.setFocusable(false);
		add(play);
		add(step);
		add(stop);
	}
}
