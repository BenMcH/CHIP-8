import java.io.IOException;
import java.util.zip.DataFormatException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.tycoon177.chip8.system.Computer;
import com.tycoon177.chip8.system.Rom;
import com.tycoon177.chip8.ui.SystemDisplay;

/**
 * Manages the launching of the program
 * @author Benjamin McHone
 *
 */
public class Launcher {

	public static void main(String[] args) throws DataFormatException, IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		Computer comp = new Computer();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SystemDisplay w = new SystemDisplay(comp);
				w.setVisible(true);
			}
		});
		Rom rom = new Rom("key.ch8");
		comp.loadRom(rom);
	}

}
