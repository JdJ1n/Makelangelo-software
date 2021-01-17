package com.marginallyclever.makelangelo;
/**
 * @(#)Makelangelo.java drawbot application with GUI
 * @author Dan Royer (dan@marginallyclever.com)
 * @version 1.00 2012/2/28
 */

// io functions

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

import com.marginallyclever.communications.ConnectionManager;
import com.marginallyclever.communications.NetworkConnection;
import com.marginallyclever.convenience.log.Log;
import com.marginallyclever.convenience.log.LogPanel;
import com.marginallyclever.makelangelo.preferences.MakelangeloAppPreferences;
import com.marginallyclever.makelangelo.preferences.MetricsPreferences;
import com.marginallyclever.makelangelo.preview.Camera;
import com.marginallyclever.makelangelo.preview.PreviewPanel;
import com.marginallyclever.makelangeloRobot.MakelangeloRobot;
import com.marginallyclever.makelangeloRobot.MakelangeloRobotListener;
import com.marginallyclever.makelangeloRobot.MakelangeloRobotPanel;
import com.marginallyclever.makelangeloRobot.settings.MakelangeloRobotSettings;
import com.marginallyclever.makelangeloRobot.settings.MakelangeloRobotSettingsListener;
import com.marginallyclever.util.PreferencesHelper;
import com.marginallyclever.util.PropertiesFileHelper;

/**
 * The root window of the GUI
 * 
 * @author Dan Royer
 * @since 0.0.1
 */
public final class Makelangelo extends TransferHandler
		implements ActionListener, WindowListener, MakelangeloRobotListener, MakelangeloRobotSettingsListener {
	static final long serialVersionUID = 1L;

	/**
	 * software VERSION. Defined in src/resources/makelangelo.properties and
	 * uses Maven's resource filtering to update the VERSION based upon VERSION
	 * defined in POM.xml. In this way we only define the VERSION once and
	 * prevent violating DRY.
	 */
	public String VERSION;
	
	private final String FORUM_URL = "https://www.marginallyclever.com/learn/forum/forum/makelangelo-polargraph-art-robot/";

	// only used on first run.
	private static int DEFAULT_WINDOW_WIDTH = 1200;
	private static int DEFAULT_WINDOW_HEIGHT = 1020;

	private Preferences preferences;

	private MakelangeloAppPreferences appPreferences;
	private ConnectionManager connectionManager;
	private Camera camera;
	private MakelangeloRobot robot;

	// GUI elements
	private JFrame mainFrame = null;
	private JPanel contentPane;

	// Top of window
	private JMenuBar menuBar;
	// file menu
	private JMenuItem buttonAdjustPreferences, buttonCheckForUpdate, buttonExit;
	// view menu
	private JMenuItem buttonZoomIn, buttonZoomOut, buttonZoomToFit;
	// help menu
	private JMenuItem buttonForums, buttonAbout;

	// main window layout
	private Splitter splitLeftRight;
	private Splitter splitUpDown;

	// OpenGL window
	private PreviewPanel previewPanel;
	// Context sensitive menu
	private MakelangeloRobotPanel robotPanel;
	// Bottom of window
	private LogPanel logPanel;

	public static void main(String[] argv) {
		Log.start();
		CommandLineOptions.setFromMain(argv);
		
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Makelangelo makelangeloProgram = new Makelangelo();
				makelangeloProgram.run();
			}
		});
	}

	@SuppressWarnings("deprecation")
	public Makelangelo() {
		Log.message("Locale="+Locale.getDefault().toString());
		
		Log.message("Starting preferences...");
		preferences = PreferencesHelper.getPreferenceNode(PreferencesHelper.MakelangeloPreferenceKey.LEGACY_MAKELANGELO_ROOT);
		VERSION = PropertiesFileHelper.getMakelangeloVersionPropertyValue();
		appPreferences = new MakelangeloAppPreferences(this);

		Log.message("Starting camera...");
		camera = new Camera();
		
		Log.message("Starting robot...");
		// create a robot and listen to it for important news
		robot = new MakelangeloRobot();
		robot.addListener(this);
		robot.getSettings().addListener(this);

		Log.message("Starting connection manager...");
		// network connections
		connectionManager = new ConnectionManager();
	}
	
	public void run() {
		Translator.start();
		
		createAndShowGUI();
		
		checkSharingPermission();

		if (preferences.getBoolean("Check for updates", false))
			checkForUpdate(true);
	}

	// check if we need to ask about sharing
	protected void checkSharingPermission() {
		Log.message("checking sharing permissions...");
		
		final String SHARING_CHECK_STRING = "Last version sharing checked";
		
		String v = preferences.get(SHARING_CHECK_STRING,"0");
		int comparison = VERSION.compareTo(v);
		if(comparison!=0) {
			preferences.put(SHARING_CHECK_STRING,VERSION);
			int dialogResult = JOptionPane.showConfirmDialog(mainFrame, Translator.get("collectAnonymousMetricsOnUpdate"),"Sharing Is Caring",JOptionPane.YES_NO_OPTION);
			MetricsPreferences.setAllowedToShare(dialogResult == JOptionPane.YES_OPTION);
		}
	}
	
	// The user has done something. respond to it.
	@Override
	public void actionPerformed(ActionEvent e) {
		Object subject = e.getSource();

		if (subject == buttonExit)					onClose();
		if (subject == buttonAdjustPreferences) 	appPreferences.run();
		if (subject == buttonZoomIn)				camera.zoomIn();
		if (subject == buttonZoomOut)				camera.zoomOut();
		if (subject == buttonZoomToFit)				camera.zoomToFitPaper();
		if (subject == buttonAbout)					(new DialogAbout()).display(this.mainFrame,this.VERSION);
		if (subject == buttonCheckForUpdate)		checkForUpdate(false);
		if( subject == buttonForums) {
			try {
				java.awt.Desktop.getDesktop().browse(URI.create(this.FORUM_URL));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * If the menu bar exists, empty it. If it doesn't exist, create it.
	 * 
	 * @return the refreshed menu bar
	 */
	public JMenuBar createMenuBar() {
		Log.message("Create menu bar");

		menuBar = new JMenuBar();

		JMenu menu;

		// File menu
		Log.message("  file...");
		menu = new JMenu(Translator.get("MenuMakelangelo"));
		menuBar.add(menu);

		buttonAdjustPreferences = new JMenuItem(Translator.get("MenuPreferences"));
		buttonAdjustPreferences.addActionListener(this);
		menu.add(buttonAdjustPreferences);

		buttonCheckForUpdate = new JMenuItem(Translator.get("MenuUpdate"));
		buttonCheckForUpdate.addActionListener(this);
		menu.add(buttonCheckForUpdate);

		menu.addSeparator();

		buttonExit = new JMenuItem(Translator.get("MenuQuit"));
		buttonExit.addActionListener(this);
		menu.add(buttonExit);

		// view menu
		Log.message("  view...");
		menu = new JMenu(Translator.get("MenuPreview"));
		menuBar.add(menu);
		
		buttonZoomOut = new JMenuItem(Translator.get("ZoomOut"));
		buttonZoomOut.addActionListener(this);
		buttonZoomOut.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menu.add(buttonZoomOut);

		buttonZoomIn = new JMenuItem(Translator.get("ZoomIn"), KeyEvent.VK_EQUALS);
		buttonZoomIn.addActionListener(this);
		buttonZoomIn.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menu.add(buttonZoomIn);

		buttonZoomToFit = new JMenuItem(Translator.get("ZoomFit"), KeyEvent.VK_0);
		buttonZoomToFit.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_0, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		buttonZoomToFit.addActionListener(this);
		menu.add(buttonZoomToFit);

		// help menu
		Log.message("  help...");
		menu = new JMenu(Translator.get("Help"));
		menuBar.add(menu);

		buttonForums = new JMenuItem(Translator.get("MenuForums"));
		buttonForums.addActionListener(this);
		menu.add(buttonForums);
		
		buttonAbout = new JMenuItem(Translator.get("MenuAbout"));
		buttonAbout.addActionListener(this);
		menu.add(buttonAbout);
		
		// finish
		Log.message("  finish...");
		menuBar.updateUI();

		return menuBar;
	}

	/**
	 * Parse https://github.com/MarginallyClever/Makelangelo/releases/latest
	 * redirect notice to find the latest release tag.
	 */
	public void checkForUpdate(boolean announceIfFailure) {
		Log.message("checking for updates...");
		try {
			URL github = new URL("https://github.com/MarginallyClever/Makelangelo-Software/releases/latest");
			HttpURLConnection conn = (HttpURLConnection) github.openConnection();
			conn.setInstanceFollowRedirects(false); // you still need to handle
													// redirect manully.
			HttpURLConnection.setFollowRedirects(false);
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String inputLine = in.readLine();
			if (inputLine == null) {
				throw new Exception("Could not read from update server.");
			}

			// parse the URL in the text-only redirect
			String matchStart = "<a href=\"";
			String matchEnd = "\">";
			int start = inputLine.indexOf(matchStart);
			int end = inputLine.indexOf(matchEnd);
			if (start != -1 && end != -1) {
				String line2 = inputLine.substring(start + matchStart.length(), end);
				// parse the last part of the redirect URL, which contains the
				// release tag (which is the VERSION)
				line2 = line2.substring(line2.lastIndexOf("/") + 1);

				Log.message("latest release: " + line2 + "; this version: " + VERSION);
				// Log.message(inputLine.compareTo(VERSION));

				int comp = line2.compareTo(VERSION);
				String results;
				if (comp > 0) {
					results = Translator.get("UpdateNotice");
					// TODO downloadUpdate(), flashNewFirmwareToRobot();
				} else if (comp < 0)
					results = "This version is from the future?!";
				else
					results = Translator.get("UpToDate");

				JOptionPane.showMessageDialog(mainFrame, results);
			}
			in.close();
		} catch (Exception e) {
			if (announceIfFailure) {
				JOptionPane.showMessageDialog(null, Translator.get("UpdateCheckFailed"));
			}
			e.printStackTrace();
		}
	}

	/**
	 * See
	 * http://www.dreamincode.net/forums/topic/190944-creating-an-updater-in-
	 * java/
	 *//*
		 * private void downloadUpdate() { String[] run =
		 * {"java","-jar","updater/update.jar"}; try {
		 * Runtime.getRuntime().exec(run); } catch (Exception ex) {
		 * ex.printStackTrace(); } System.exit(0); }
		 */

	/**
	 * Rebuild the contents of the menu based on current program state
	 */
	public void updateMenuBar() {
		if (robotPanel != null)
			robotPanel.updateButtonAccess();
	}

	public Container createContentPane() {
		Log.message("create content pane...");

		contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);

		Log.message("  create PreviewPanel...");
		previewPanel = new PreviewPanel();
		previewPanel.setCamera(camera);
		previewPanel.addListener(robot);

		Log.message("  assign panel to robot...");
		robotPanel = robot.createControlPanel(this);

		Log.message("  create log panel...");
		logPanel = new LogPanel(robot);

		// major layout
		Log.message("  vertical split...");
		splitLeftRight = new Splitter(JSplitPane.HORIZONTAL_SPLIT);
		splitLeftRight.add(previewPanel);
		splitLeftRight.add(robotPanel);

		Log.message("  horizontal split...");
		splitUpDown = new Splitter(JSplitPane.VERTICAL_SPLIT);
		splitUpDown.add(splitLeftRight);
		splitUpDown.add(logPanel);

		contentPane.add(splitUpDown, BorderLayout.CENTER);

		Log.message("  tweak...");
		splitUpDown.setResizeWeight(0.9);
		splitUpDown.setOneTouchExpandable(true);
		splitUpDown.setDividerLocation(800);
		// Dimension minimumSize = new Dimension(100, 100);
		// splitLeftRight.setMinimumSize(minimumSize);
		// logPanel.setMinimumSize(minimumSize);

		return contentPane;
	}

	// For thread safety, this method should be invoked from the
	// event-dispatching thread.
	public void createAndShowGUI() {
		Log.message("Creating GUI...");
		
		mainFrame = new JFrame(Translator.get("TitlePrefix")+" "+this.VERSION);
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(this);
		
		JMenuBar bar = createMenuBar();
		Log.message("  adding menu bar...");
		mainFrame.setJMenuBar(bar);
		
		Container contentPane = createContentPane();
		mainFrame.setContentPane(contentPane);
		
		adjustWindowSize();
		
		Log.message("  make visible...");
		mainFrame.setVisible(true);

		camera.zoomToFitPaper();

		Log.message("  adding drag & drop support...");
		mainFrame.setTransferHandler(this);
	}

	private void adjustWindowSize() {
		Log.message("adjust window size...");
		
		int width = preferences.getInt("Default window width", DEFAULT_WINDOW_WIDTH);
		int height = preferences.getInt("Default window height", DEFAULT_WINDOW_HEIGHT);

		// Get default screen size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// Set window size
		if (width > screenSize.width || height > screenSize.height) {
			width = screenSize.width;
			height = screenSize.height;
			preferences.putInt("Default window width", width);
			preferences.putInt("Default window height", height);
		}

		mainFrame.setSize(width, height);

		// by default center the window. Later use preferences.
		int defaultLocationX = (screenSize.width - width) / 2;
		int defaultLocationY = (screenSize.height - height) / 2;
		mainFrame.setLocation(defaultLocationX, defaultLocationY);
		// int locationX = prefs.getInt("Default window location x",
		// defaultLocationX);
		// int locationY = prefs.getInt("Default window location y",
		// defaultLocationY);
		// mainFrame.setLocation(locationX,locationY);
	}

	@Override
	public void portConfirmed(MakelangeloRobot r) {
		if (previewPanel != null)
			previewPanel.repaint();
	}

	@Override
	public void firmwareVersionBad(MakelangeloRobot r, long versionFound) {
		(new DialogBadFirmwareVersion()).display(this.mainFrame, Long.toString(versionFound));
	}

	@Override
	public void dataAvailable(MakelangeloRobot r, String data) {
		if (data.endsWith("\n"))
			data = data.substring(0, data.length() - 1);
		Log.message(data); // #ffa500 = orange
	}

	@Override
	public void sendBufferEmpty(MakelangeloRobot r) {
	}

	@Override
	public void lineError(MakelangeloRobot r, int lineNumber) {
	}

	@Override
	public void disconnected(MakelangeloRobot r) {
		if (previewPanel != null)
			previewPanel.repaint();
		SoundSystem.playDisconnectSound();
	}

	public void settingsChangedEvent(MakelangeloRobotSettings settings) {
		if (previewPanel != null)
			previewPanel.repaint();
	}

	public NetworkConnection requestNewConnection() {
		return connectionManager.requestNewConnection(this.mainFrame);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		onClose();
	}

	private void onClose() {
		int result = JOptionPane.showConfirmDialog(mainFrame, Translator.get("ConfirmQuitQuestion"),
				Translator.get("ConfirmQuitTitle"), JOptionPane.YES_NO_OPTION);

		if (result == JOptionPane.YES_OPTION) {
			previewPanel.removeListener(robot);
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			saveWindowRealEstate();
			robot.getSettings().saveConfig();

			// Log.end() should be the very last call.  mainFrame.dispose() kills the thread, so this is as close as I can get.
			Log.end();

			// Run this on another thread than the AWT event queue to
			// make sure the call to Animator.stop() completes before
			// exiting
			new Thread(new Runnable() {
				public void run() {
					previewPanel.stop();
					mainFrame.dispose();
				}
			}).start();
		}
	}

	/**
	 * save window position and size
	 */
	private void saveWindowRealEstate() {
		Dimension size = this.mainFrame.getSize();
		preferences.putInt("Default window width", size.width);
		preferences.putInt("Default window height", size.height);

		Point location = this.mainFrame.getLocation();
		preferences.putInt("Default window location x", location.x);
		preferences.putInt("Default window location y", location.y);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	public MakelangeloRobot getRobot() {
		return robot;
	}
	
	@Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        // we only import FileList
        if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            Log.message("Does not support files of type(s): "+info.getDataFlavors());
            return false;
        }
        return true;
    }
    
	@Override
    public boolean importData(TransferHandler.TransferSupport info) {
    	// only accept drops
        if (!info.isDrop()) return false;
        
        // recommended to explicitely call canImport from importData (see java documentation)
        if(!canImport(info)) return false;
        
        if(robot.getControlPanel()==null) return false;

        // Get the fileList that is being dropped.
        List<?> data = null;
        try {
        	data = (List<?>)info.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
        } 
        catch (Exception e) {
        	return false;
        }

        if(data==null) return false;
        // accept only one file at a time.
        if(data.size()!=1) return false;
        
        String filename = ((File)data.get(0)).getAbsolutePath();

        // Load the file.
        return robot.getControlPanel().openFileOnDemand(filename);
    }
}

/**
 * This file is part of Makelangelo.
 * <p>
 * Makelangelo is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * Makelangelo is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * Makelangelo. If not, see <http://www.gnu.org/licenses/>.
 */
