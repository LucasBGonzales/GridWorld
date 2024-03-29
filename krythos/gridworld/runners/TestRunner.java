package krythos.gridworld.runners;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JOptionPane;

import krythos.gridworld.GridWorld;
import krythos.gridworld.map.Entity;
import krythos.gridworld.map.GridMap;
import krythos.gridworld.map.Location;
import krythos.util.logger.Log;

public class TestRunner {

	public static void main(String[] args) throws IOException {
		(new TestRunner()).run();
	}


	public void run() {
		new Log();

		int width = 10, height = 10;
		GridWorld gw = new GridWorld(new GridMap(width, height, true), 10, 10);
		Entity e = new GreenEntity(gw);
		gw.addEntity(e);
	}


	public class GreenEntity implements Entity {
		private Location l;
		private BufferedImage image;
		private GridWorld gw;
		private int moveDirection = 1;
		private boolean moveVertical = false;
		private final String[] m_commands = { "Go Down", "Go Up", "Go Right", "Go Left", "Inverse Direction",
				"Get Direction", "Step", "Set Direction" };


		public GreenEntity(GridWorld gw) {
			this.gw = gw;
			l = new Location(0, 0);
			int size = 100;
			image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
			for (int x = 0; x < size; x++)
				for (int y = 0; y < size; y++)
					image.setRGB(x, y, Color.GREEN.getRGB());
		}


		@Override
		public Location getLocation() {
			return l;
		}


		@Override
		public void step() {
			if (moveVertical) {
				if (l.getY() >= gw.getMap().getHeight() - 1 || (l.getY() <= 0 && moveDirection < 0))
					inverseDirection();
				l.modY(moveDirection);
			} else {
				if (l.getX() >= gw.getMap().getWidth() - 1 || (l.getX() <= 0 && moveDirection < 0))
					inverseDirection();
				l.modX(moveDirection);
			}
		}


		private void inverseDirection() {
			moveDirection *= -1;
		}


		private void setDirection(String dir) {
			if (dir.toLowerCase().equals("up")) {
				moveDirection = 1;
				moveVertical = true;
			} else if (dir.toLowerCase().equals("down")) {
				moveDirection = -1;
				moveVertical = true;
			} else if (dir.toLowerCase().equals("left")) {
				moveDirection = -1;
				moveVertical = false;
			} else if (dir.toLowerCase().equals("right")) {
				moveDirection = 1;
				moveVertical = false;
			}
		}


		@Override
		public BufferedImage getImage() {
			return image;
		}


		@Override
		public String[] getCommands() {
			return m_commands;
		}


		@Override
		public void processCommand(String command) {
			if (command.toLowerCase().equals(m_commands[0].toLowerCase()))
				l.modY(1);
			else if (command.toLowerCase().equals(m_commands[1].toLowerCase()))
				l.modY(-1);
			else if (command.toLowerCase().equals(m_commands[2].toLowerCase()))
				l.modX(1);
			else if (command.toLowerCase().equals(m_commands[3].toLowerCase()))
				l.modX(-1);
			else if (command.toLowerCase().equals(m_commands[4].toLowerCase()))
				inverseDirection();
			else if (command.toLowerCase().equals(m_commands[5].toLowerCase()))
				Log.get().showMessageDialog("Direction: " + (moveDirection == 1 ? "Right" : "Left"));
			else if (command.toLowerCase().equals(m_commands[6].toLowerCase()))
				step();
			else if (command.toLowerCase().equals(m_commands[7].toLowerCase())) {
				String option = (String) JOptionPane.showInputDialog(null, "Enter The Direction", "Enter Direction",
						JOptionPane.PLAIN_MESSAGE, null, new Object[] { "Up", "Down", "Left", "Right" }, "Up");
				setDirection(option);
			}
			gw.updateWindow();
		}

	}
}
