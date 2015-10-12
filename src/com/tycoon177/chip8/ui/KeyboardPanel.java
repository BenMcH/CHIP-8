package com.tycoon177.chip8.ui;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

import com.tycoon177.chip8.system.Keyboard;

public class KeyboardPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8601869046441324384L;
	private JToggleButton[] buttons;

	public KeyboardPanel(Keyboard keyboard) {
		super(new GridLayout(4, 4, 2, 2));
		buttons = new JToggleButton[16];
		for (int i = 0; i < buttons.length; i++) {
			final int j = i;
			buttons[i] = new JToggleButton(Integer.toHexString(i));
			buttons[i].addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					keyboard.setKeyPressed(j, e.getStateChange() == ItemEvent.SELECTED);
				}
			});
			buttons[i].setFocusable(false);
		}
		add(buttons[1]);
		add(buttons[2]);
		add(buttons[3]);
		add(buttons[0xc]);
		add(buttons[4]);
		add(buttons[5]);
		add(buttons[6]);
		add(buttons[0xd]);
		add(buttons[7]);
		add(buttons[8]);
		add(buttons[9]);
		add(buttons[0xE]);
		add(buttons[0xa]);
		add(buttons[0]);
		add(buttons[0xb]);
		add(buttons[0xf]);
	}

}
