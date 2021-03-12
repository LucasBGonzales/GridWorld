package krythos.gridworld;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import krythos.gridworld.map.GridMap;
import krythos.util.abstract_interfaces.AbsMouseListener;
import krythos.util.dimensional_arraylist.ArrayList2D;
import krythos.util.logger.Log;

public class GridWorld_OLD {
	private static String TAG = "GridWord";

	// GridWorld
	private GridMap m_map;

	// GridWindow
	private int m_view_x, m_view_y;
	private GridWindow m_window;

	// KeyListenrs
	private KeyListener m_keyListener;
	private ArrayList<KeyListener> m_listeners;

	public GridWorld_OLD(GridMap map) {
		if (map == null)
			throw new RuntimeException("GridMap can not be null");

		// Allow others to listen to keys. GridWorld wants to control this because it
		// may need to do some work to allow changes to happen.
		m_listeners = new ArrayList<KeyListener>(0);
		m_keyListener = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				for (KeyListener kl : m_listeners)
					kl.keyPressed(arg0);
			}


			@Override
			public void keyReleased(KeyEvent arg0) {
				for (KeyListener kl : m_listeners)
					kl.keyReleased(arg0);
			}


			@Override
			public void keyTyped(KeyEvent arg0) {
				for (KeyListener kl : m_listeners)
					kl.keyTyped(arg0);
			}
		};

		m_map = map;
		m_view_x = 0;
		m_view_y = 0;

		//m_window = new GridWindow(m_map.getWidth(), m_map.getHeight(0), 50);
		m_window.addKeyListener(m_keyListener);
	}


	public void addKeyListener(KeyListener kl) {
		m_listeners.add(kl);
	}


	/**
	 * Modifies the position of the viewpoint of this GridWorld by the given
	 * coordinates.
	 * 
	 * @param move_x
	 * @param move_y
	 */
	public void moveView(int move_x, int move_y) {
		Log.info(TAG, "moveView(): " + move_x + ", " + move_y);
		m_view_x += move_x;
		m_view_y += move_y;
	}


	/**
	 * Sets the position of the viewpoint of this GridWorld to the given
	 * coordinates.
	 * 
	 * @param move_x
	 * @param move_y
	 */
	public void setView(int move_x, int move_y) {
		Log.info(TAG, "setView(): " + move_x + ", " + move_y);
		m_view_x = move_x;
		m_view_y = move_y;
	}


	public GridMap getMap() {
		return m_map;
	}


	public void setMap(GridMap newMap) {
		m_map = newMap;
	}

	private class GridWindow {
		private JFrame m_frame;
		private JPanel m_contentPane, m_gridPane, m_controlPane;
		private JButton m_step, m_run;
		private ArrayList2D<JLabel> m_labels;

		public GridWindow(int width, int height, int boxSize) {
			// Setup Frame
			m_frame = new JFrame("GridWindow");
			m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			m_frame.setBounds(100, 100, 700, 600);
			m_contentPane = new JPanel();
			m_contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			m_contentPane.setLayout(new BoxLayout(m_contentPane, BoxLayout.Y_AXIS));
			m_frame.setContentPane(m_contentPane);

			// Setup GridPane
			SpringLayout sl_gridPane = new SpringLayout();
			m_gridPane = new JPanel();
			m_gridPane.setLayout(sl_gridPane);

			// Generate Grid Labels
			m_labels = new ArrayList2D<JLabel>(width, height);
			for (int w = 0; w < width; w++) {
				m_labels.addRow();
				for (int h = 0; h < height; h++) {
					JLabel label = new JLabel();

					// Establish Relative Constraints and edges.
					Component northContraint = h == 0 ? m_gridPane : m_labels.get(w, h - 1);
					Component westConstraint = w == 0 ? m_gridPane : m_labels.get(w - 1, h);
					String northContraintEdge = northContraint == m_gridPane ? SpringLayout.NORTH : SpringLayout.SOUTH;
					String westConstraintEdge = westConstraint == m_gridPane ? SpringLayout.WEST : SpringLayout.EAST;

					// Put Constraints
					sl_gridPane.putConstraint(SpringLayout.NORTH, label, 0, northContraintEdge, northContraint);
					sl_gridPane.putConstraint(SpringLayout.WEST, label, 0, westConstraintEdge, westConstraint);
					sl_gridPane.putConstraint(SpringLayout.SOUTH, label, boxSize, SpringLayout.NORTH, label);
					sl_gridPane.putConstraint(SpringLayout.EAST, label, boxSize, SpringLayout.WEST, label);

					// Init Label
					label.setBackground(Color.LIGHT_GRAY);
					label.setOpaque(true);
					label.setBorder(BorderFactory.createLineBorder(Color.black, 1));
					label.setName(w + ", " + h);
					// TODO This can share a single MouseListener and use ((JLabel)e.getSource())
					// instead of a direct reference to the label. Change that later when you
					// actually start implementing functionality for clicking the grid.
					label.addMouseListener(new AbsMouseListener() {
						@Override
						public void mouseEntered(MouseEvent e) {
							label.setBorder(BorderFactory.createLineBorder(Color.black, 2));
						}


						@Override
						public void mouseExited(MouseEvent e) {
							label.setBorder(BorderFactory.createLineBorder(Color.black, 1));
						}


						@Override
						public void mouseClicked(MouseEvent e) {
							Log.printDialog("Clicked Label:" + label.getName());
						}
					});

					m_gridPane.add(label);
					m_labels.add(w, label);
				}
			}

			// Assign GridPane
			m_gridPane.setMaximumSize(new Dimension(width*boxSize, height*boxSize));
			m_gridPane.setMinimumSize(m_gridPane.getMaximumSize());
			m_gridPane.setBackground(Color.gray);
			m_gridPane.setAlignmentX(Component.CENTER_ALIGNMENT);
			m_contentPane.add(m_gridPane);
			/// m_contentPane.add(m_gridPane, BorderLayout.CENTER);

			// Setup ControlPane
			m_controlPane = new JPanel();
			m_controlPane.setLayout(new FlowLayout());
			m_controlPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

			// step button
			JButton m_step = new JButton("Step");
			m_controlPane.add(m_step);

			// run button
			JButton m_run = new JButton("Run");
			m_controlPane.add(m_run);

			// run speed thing

			// Assign ControlPane
			m_controlPane.setAlignmentX(Component.CENTER_ALIGNMENT);
			m_contentPane.add(m_controlPane);/* , BorderLayout.PAGE_END); */


			/// Finalize
			m_frame.setVisible(true);
		}


		public void addKeyListener(KeyListener kl) {
			m_frame.addKeyListener(kl);
		}
	}
}
