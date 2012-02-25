import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ch.zhaw.simulation.clipboard.ClipboardInterface;
import ch.zhaw.simulation.clipboard.ClipboardListener;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.window.SimulationWindow;
import ch.zhaw.simulation.window.XYWindow;

public class Test {

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");


		XYWindow win = new XYWindow(new Sysintegration(), new ClipboardInterface() {
			
			@Override
			public void removeListener(ClipboardListener l) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addListener(ClipboardListener l) {
				// TODO Auto-generated method stub
				
			}
		});
		
		win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		win.setVisible(true);
	}

}
