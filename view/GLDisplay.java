package view;




import com.sun.opengl.util.FPSAnimator;

import javax.swing.*;
import javax.media.opengl.*;



import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

//http://www.opengl.org/documentation/specs/man_pages/hardcopy/GL/html/
//The above link is the OpenGL API, description of all gl methods used here should be described there.

public class GLDisplay {
	private static final int DEFAULT_WIDTH = 1024;
	private static final int DEFAULT_HEIGHT = 768;

	private static final int DONT_CARE = -1;

	private JFrame frame;
	private GLCanvas glCanvas;
	private FPSAnimator animator;
	private boolean fullscreen;
	private int width;
	private int height;
	private GraphicsDevice usedDevice;
	private int fps = 40;

	static {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
	}

	private MyHelpOverlayGLEventListener helpOverlayGLEventListener = new MyHelpOverlayGLEventListener();

	public static GLDisplay createGLDisplay( String title ) throws IOException {
		return createGLDisplay( title, new GLCapabilities() );
	}

	public static GLDisplay createGLDisplay( String title, GLCapabilities caps ) throws IOException {
		boolean fullscreen = false;
		return new GLDisplay( title, DEFAULT_WIDTH, DEFAULT_HEIGHT, fullscreen, caps );
	}



	protected GLDisplay(String title, int width, int height, boolean fullscreen, GLCapabilities caps) throws IOException {
		glCanvas = new GLCanvas(caps);
		this.fullscreen = fullscreen;
		if(fullscreen){
			this.width = width;
			this.height = height;
		} else {
			this.width = width;
			this.height = height;
		}
		glCanvas.setSize(this.width, this.height);
		glCanvas.setIgnoreRepaint(true);
		glCanvas.addGLEventListener(helpOverlayGLEventListener);

		frame = new JFrame(title);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(glCanvas, BorderLayout.CENTER);

		addKeyListener( new MyKeyAdapter() );

		// Sets the FPS
		animator = new FPSAnimator(glCanvas, fps);
		animator.setRunAsFastAsPossible(false);
	}
	public GLCanvas getCanvas()
	{
		return glCanvas;
	}

	public JFrame getFrame(){
		return frame;
	}

	public void start() {
		try {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setUndecorated( fullscreen );

			frame.addWindowListener( new MyWindowAdapter() );

			if ( fullscreen ) {
				usedDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				usedDevice.setFullScreenWindow( frame );
				usedDevice.setDisplayMode(
						findDisplayMode(
								usedDevice.getDisplayModes(),
								width, height,
								usedDevice.getDisplayMode().getBitDepth(),
								usedDevice.getDisplayMode().getRefreshRate()
						)
				);
			} else {
				frame.setSize( frame.getContentPane().getPreferredSize() );
				frame.setLocation(
						( screenSize.width - frame.getWidth() ) / 2,
						( screenSize.height - frame.getHeight() ) / 2
				);
				//frame.setVisible( true );
			}

			glCanvas.requestFocus();

			animator.start();
		} catch ( Exception e ) {
			System.err.println(e.toString());
			System.exit(-1);
		}
	}

	public void stop() {
		try {
			animator.stop();
			if ( fullscreen ) {
				usedDevice.setFullScreenWindow( null );
				usedDevice = null;
			}
			frame.dispose();
		} catch ( Exception e ) {
			System.err.println(e.toString());
		} finally {
			System.exit( 0 );
		}
	}

	public static void makeJFrameFullscreen(JFrame frame, int width, int height)
	{
		//frame.setVisible(false);
		frame.dispose();
		frame.setUndecorated( true );
		GraphicsDevice usedDevice;
		usedDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		usedDevice.setFullScreenWindow( frame );
		DisplayMode displayMode = findDisplayMode(
								usedDevice.getDisplayModes(),
								width, height,
								usedDevice.getDisplayMode().getBitDepth(),
								usedDevice.getDisplayMode().getRefreshRate()
						);
		frame.setVisible(true);
		try{
			usedDevice.setDisplayMode(displayMode);
		} catch(UnsupportedOperationException e)
		{
			//cant change display mode.
		}
	}
	public static void unfullscreen(JFrame frame)
	{
		frame.dispose();
		frame.setUndecorated(false);
		

		GraphicsDevice usedDevice;
		usedDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		usedDevice.setFullScreenWindow( null );
		frame.setVisible(true);
	}

	private static DisplayMode findDisplayMode( DisplayMode[] displayModes, int requestedWidth, int requestedHeight, int requestedDepth, int requestedRefreshRate ) {
		// Try to find an exact match
		DisplayMode displayMode = findDisplayModeInternal( displayModes, requestedWidth, requestedHeight, requestedDepth, requestedRefreshRate );

		// Try again, ignoring the requested bit depth
		if ( displayMode == null )
			displayMode = findDisplayModeInternal( displayModes, requestedWidth, requestedHeight, DONT_CARE, DONT_CARE );

		// Try again, and again ignoring the requested bit depth and height
		if ( displayMode == null )
			displayMode = findDisplayModeInternal( displayModes, requestedWidth, DONT_CARE, DONT_CARE, DONT_CARE );

		// If all else fails try to get any display mode
		if ( displayMode == null )
			displayMode = findDisplayModeInternal( displayModes, DONT_CARE, DONT_CARE, DONT_CARE, DONT_CARE );

		return displayMode;
	}

	private static DisplayMode findDisplayModeInternal( DisplayMode[] displayModes, int requestedWidth, int requestedHeight, int requestedDepth, int requestedRefreshRate ) {
		DisplayMode displayModeToUse = null;
		for ( int i = 0; i < displayModes.length; i++ ) {
			DisplayMode displayMode = displayModes[i];
			if ( ( requestedWidth == DONT_CARE || displayMode.getWidth() == requestedWidth ) &&
					( requestedHeight == DONT_CARE || displayMode.getHeight() == requestedHeight ) &&
					( requestedHeight == DONT_CARE || displayMode.getRefreshRate() == requestedRefreshRate ) &&
					( requestedDepth == DONT_CARE || displayMode.getBitDepth() == requestedDepth ) )
				displayModeToUse = displayMode;
		}

		return displayModeToUse;
	}

	public void addGLEventListener( GLEventListener glEventListener ) {
		this.helpOverlayGLEventListener.addGLEventListener( glEventListener );
	}

	public void removeGLEventListener( GLEventListener glEventListener ) {
		this.helpOverlayGLEventListener.removeGLEventListener( glEventListener );
	}

	public void addKeyListener( KeyListener l ) {
		glCanvas.addKeyListener( l );
	}

	public void addMouseListener( MouseListener l ) {
		glCanvas.addMouseListener( l );
	}

	public void addMouseMotionListener( MouseMotionListener l ) {
		glCanvas.addMouseMotionListener( l );
	}

	public void removeKeyListener( KeyListener l ) {
		glCanvas.removeKeyListener( l );
	}

	public void removeMouseListener( MouseListener l ) {
		glCanvas.removeMouseListener( l );
	}

	public void removeMouseMotionListener( MouseMotionListener l ) {
		glCanvas.removeMouseMotionListener( l );
	}

	public String getTitle() {
		return frame.getTitle();
	}

	public void setTitle( String title ) {
		frame.setTitle( title );
	}


	private class MyKeyAdapter extends KeyAdapter {

		public void keyReleased( KeyEvent e ) {
			switch ( e.getKeyCode() ) {
			case KeyEvent.VK_ESCAPE:
				stop();
				break;
			}
		}
	}

	private class MyWindowAdapter extends WindowAdapter {
		public void windowClosing( WindowEvent e ) {
			stop();
		}
	}

	private static class MyHelpOverlayGLEventListener implements GLEventListener {
		@SuppressWarnings("unchecked")
		private java.util.List eventListeners = new ArrayList();

		@SuppressWarnings("unchecked")
		public void addGLEventListener( GLEventListener glEventListener ) {
			eventListeners.add( glEventListener );
		}

		public void removeGLEventListener( GLEventListener glEventListener ) {
			eventListeners.remove( glEventListener );
		}

		public void display( GLAutoDrawable glDrawable ) {
			for ( int i = 0; i < eventListeners.size(); i++ ) {
				( (GLEventListener) eventListeners.get( i ) ).display( glDrawable );
			}
		}

		public void displayChanged( GLAutoDrawable glDrawable, boolean b, boolean b1 ) {
			for ( int i = 0; i < eventListeners.size(); i++ ) {
				( (GLEventListener) eventListeners.get( i ) ).displayChanged( glDrawable, b, b1 );
			}
		}

		public void init( GLAutoDrawable glDrawable ) {
			for ( int i = 0; i < eventListeners.size(); i++ ) {
				( (GLEventListener) eventListeners.get( i ) ).init( glDrawable );
			}
		}

		public void reshape( GLAutoDrawable glDrawable, int i0, int i1, int i2, int i3 ) {
			for ( int i = 0; i < eventListeners.size(); i++ ) {
				( (GLEventListener) eventListeners.get( i ) ).reshape( glDrawable, i0, i1, i2, i3 );
			}
		}
	}
}
