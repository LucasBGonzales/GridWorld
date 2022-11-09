package krythos.gridworld;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import krythos.gridworld.map.Entity;
import krythos.gridworld.map.GridMap;
import krythos.gridworld.map.Location;
import krythos.util.abstract_interfaces.AbsMouseListener;
import krythos.util.logger.Log;
import krythos.util.swing.kcontextmenu.KContextMenu;

public class GridWorld {
	public class GridWindow {
		private static final String TAG = "GridWorld.GridMap";
		public static final int UNKNOWN_COMPONENT = 0;
		public static final int STEP_BUTTON = 1;
		public static final int RUN_BUTTON = 2;
		public static final int STOP_BUTTON = 3;


		private JFrame m_frame;
		private JPanel m_contentPane, m_gridPane, m_controlPane;
		private JButton m_step, m_run, m_stop;
		private List<ArrayList<JLabel>> m_labels;
		private int m_width, m_height;
		private int m_run_speed;
		private JSlider m_speedSlider;

		private GridMap m_map;

		private Color m_enabledColor = Color.LIGHT_GRAY;
		private Color m_disabledColor = Color.DARK_GRAY;


		public GridWindow(int width, int height) {
			setSize(width, height);
			toggleRunning(true);
		}


		public void addActionListener(ActionListener l) {
			m_step.addActionListener(l);
			m_run.addActionListener(l);
			m_stop.addActionListener(l);
		}


		public int getComponentID(Component c) {
			if (c.equals(m_step))
				return STEP_BUTTON;

			if (c.equals(m_stop))
				return STOP_BUTTON;

			if (c.equals(m_run))
				return RUN_BUTTON;

			return UNKNOWN_COMPONENT;

		}


		public int getHeight() {
			return m_height;
		}


		public int getRunSpeed() {
			return m_run_speed;
		}


		public int getWidth() {
			return m_width;
		}


		public void setEnabled(int x, int y, boolean enabled) {
			JLabel label = m_labels.get(x).get(y);
			label.setEnabled(enabled);

			if (enabled)
				label.setBackground(m_enabledColor);
			else
				label.setBackground(m_disabledColor);
		}


		public void setMap(GridMap map) {
			m_map = map;
		}


		public void setSize(int width, int height) {
			m_width = width;
			m_height = height;
			m_run_speed = 1;
			setupWindow();
		}


		public void setupWindow() {
			boolean previously_initialized = false;
			// Setup Frame
			if (m_frame == null) {
				m_frame = new JFrame("GridWindow");
				m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				m_frame.setBounds(100, 100, 700, 600);
				m_contentPane = new JPanel();
				m_contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
				m_contentPane.setLayout(new BoxLayout(m_contentPane, BoxLayout.Y_AXIS));
				m_frame.setContentPane(m_contentPane);
			} else {
				previously_initialized = true;
				m_frame.getContentPane().removeAll();
				m_frame.repaint();
			}

			// Setup GridPane
			m_gridPane = new JPanel();
			m_gridPane.setLayout(new GridLayout(m_width, m_height));
			
			AbsMouseListener mouselistener = new AbsMouseListener() {
				private KContextMenu contextMenu;
				
				@Override
				public void mouseClicked(MouseEvent e) {
					JLabel label = (JLabel) e.getSource();
					Log.get().info(TAG, "Clicked Label:" + label.getName());
					if (m_map != null) {
						int w, h;
						w = Integer.valueOf(label.getName().split(",")[0].trim());
						h = Integer.valueOf(label.getName().split(",")[1].trim());
						Entity entity = m_map.get(new Location(w, h));
						if (entity != null) {
							String[] arr_options = entity.getCommands();
							if (arr_options != null) {
								String display = "";
								for (String s : arr_options)
									display += s + ", ";
								display = display.substring(0, display.length() - 2);
								// TODO Create a DropSelection for each JLabel, store that. Toggle
								// visibility
								// when right-clicked as opposed to creating a new DropSelection every
								// time.
								// Potential for memory leak.
								contextMenu = new KContextMenu(m_frame, (Object[]) arr_options);
								contextMenu.addContextListener(dle -> {
									entity.processCommand(dle.getSource().toString());
									contextMenu.setVisible(false);
								});
								contextMenu.setVisible(true);
								// entity.processCommand(Dialogs.showInputAreaDialog(null, display,
								// ""));
							}
						}
					}
				}


				@Override
				public void mouseEntered(MouseEvent e) {
					JLabel label = (JLabel) e.getSource();
					if (label.isEnabled())
						label.setBorder(BorderFactory.createLineBorder(Color.black, 2));
				}


				@Override
				public void mouseExited(MouseEvent e) {
					JLabel label = (JLabel) e.getSource();
					label.setBorder(BorderFactory.createLineBorder(Color.black, 1));
				}
			};

			// Generate Grid Labels
			m_labels = new ArrayList<ArrayList<JLabel>>();
			for (int h = 0; h < m_height; h++) {
				for (int w = 0; w < m_width; w++) {
					if (h == 0)
						m_labels.add(new ArrayList<JLabel>());
					JLabel label = new JLabel();

					// Init Label
					label.setBackground(Color.LIGHT_GRAY);
					label.setOpaque(true);
					label.setBorder(BorderFactory.createLineBorder(Color.black, 1));
					label.setName(w + ", " + h);
					label.addMouseListener(mouselistener);

					m_gridPane.add(label);
					m_labels.get(w).add(label);
				}
			}

			// Assign GridPane
			m_gridPane.setBackground(Color.gray);
			m_contentPane.add(m_gridPane, BorderLayout.CENTER);

			// Setup ControlPane
			m_controlPane = new JPanel();
			m_controlPane.setLayout(new FlowLayout());
			m_controlPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

			// step button
			m_step = new JButton("Step");
			m_controlPane.add(m_step);

			// run button
			m_run = new JButton("Run");
			m_controlPane.add(m_run);

			// Speed Label
			JTextArea speedLabel = new JTextArea("1");
			speedLabel.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
			speedLabel.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					update();
				}


				@Override
				public void insertUpdate(DocumentEvent e) {
					update();
				}


				@Override
				public void removeUpdate(DocumentEvent e) {
					update();
				}


				private void update() {
					try {
						if (!speedLabel.getText().equals("")) {
							int value = Integer.valueOf(speedLabel.getText());
							if (value > 0)
								m_run_speed = value;
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			});
			m_controlPane.add(speedLabel);

			// Speed Slider
			m_speedSlider = new JSlider(1, 10, 1);
			m_speedSlider.addChangeListener(e -> {
				m_run_speed = ((JSlider) e.getSource()).getValue();
				speedLabel.setText("" + m_run_speed);
			});
			m_controlPane.add(m_speedSlider);

			m_stop = new JButton("Stop");
			m_controlPane.add(m_stop);

			// Assign ControlPane
			m_controlPane.setAlignmentX(Component.CENTER_ALIGNMENT);
			m_contentPane.add(m_controlPane);/* , BorderLayout.PAGE_END); */
			// m_contentPane.add(m_controlPane , BorderLayout.PAGE_END);


			// Configure GridPane Aspect Ratio Sizing
			if (!previously_initialized)
				m_contentPane.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						int w = m_frame.getWidth();
						int h = m_frame.getHeight() - ((int) (m_step.getHeight() * 3.1));
						int size = Math.min(w, h);
						m_gridPane.setPreferredSize(new Dimension(size, size));
						m_gridPane.setMaximumSize(new Dimension(size, size));
						m_gridPane.setMinimumSize(new Dimension(size, size));
						m_contentPane.revalidate();
					}
				});

			/// Finalize
			m_frame.setVisible(true);
		}


		public void toggleRunning(boolean running) {
			m_run.setEnabled(running);
			m_step.setEnabled(running);
			m_stop.setEnabled(!running);
		}
	}
	
	// GridWorld
	private GridMap m_map;
	private Thread m_runThread;
	
	// GridWindow
	private int m_view_x, m_view_y;
	private GridWindow m_window;


	public GridWorld(GridMap map, int width, int height) {
		if (map == null)
			throw new RuntimeException("GridMap can not be null");
		m_view_x = 0;
		m_view_y = 0;

		m_window = new GridWindow(width, height);

		m_runThread = null;

		m_window.addActionListener(e -> {
			if (m_window.getComponentID((Component) e.getSource()) == GridWindow.STEP_BUTTON)
				step();

			if (m_window.getComponentID((Component) e.getSource()) == GridWindow.RUN_BUTTON) {
				if (m_runThread == null) {
					m_runThread = new Thread() {
						@Override
						public void run() {
							while (m_runThread != null) {
								step();
								try {
									Thread.sleep(1000 / m_window.getRunSpeed());
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					};
					m_runThread.start();
					m_window.toggleRunning(false);
				}
			}

			if (m_window.getComponentID((Component) e.getSource()) == GridWindow.STOP_BUTTON) {
				m_runThread = null;
				m_window.toggleRunning(true);
			}
		});

		setMap(map);

		updateWindow();
	}


	public void addEntity(Entity e) {
		m_map.addEntity(e);
		updateWindow();
	}


	public GridMap getMap() {
		return m_map;
	}


	/**
	 * Modifies the position of the viewpoint of this GridWorld by the
	 * given
	 * coordinates.
	 * 
	 * @param move_x
	 * @param move_y
	 */
	public void moveView(int move_x, int move_y) {
		Log.get().info("moveView(): " + move_x + ", " + move_y);
		m_view_x += move_x;
		m_view_y += move_y;
	}


	public void setMap(GridMap newMap) {
		m_map = newMap;
		m_window.setMap(m_map);
	}


	/**
	 * Sets the position of the viewpoint of this GridWorld to the given
	 * coordinates.
	 * 
	 * @param move_x
	 * @param move_y
	 */
	public void setView(int move_x, int move_y) {
		Log.get().info("setView(): " + move_x + ", " + move_y);
		m_view_x = move_x;
		m_view_y = move_y;
	}


	public void step() {
		m_map.step();
		updateWindow();
	}


	public void updateWindow() {
		int width, height;
		width = window().getWidth();
		height = window().getHeight();
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				Entity e;
				if (x < 0 || y < 0 || x >= m_map.getWidth() || y >= m_map.getHeight()) {

					e = null;
					JLabel label = window().m_labels.get(x).get(y);
					label.setIcon(null);
					window().setEnabled(x, y, false);
				} else {
					e = m_map.get(new Location(x, y));
					JLabel label = window().m_labels.get(x).get(y);
					window().setEnabled(x, y, true);
					if (e == null)
						label.setIcon(null);
					else
						label.setIcon(new ImageIcon(e.getImage()));
				}
			}
	}


	public GridWindow window() {
		return m_window;
	}
}
