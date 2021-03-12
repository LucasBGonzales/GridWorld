package krythos.gridworld.map;

import java.awt.image.BufferedImage;

public interface Entity {
	Location getLocation();
	void step();
	BufferedImage getImage();
	String[] getCommands();
	void processCommand(String option);
}
