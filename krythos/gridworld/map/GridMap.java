package krythos.gridworld.map;

import java.util.ArrayList;

import krythos.util.logger.Log;


public class GridMap {
	private final String TAG = "GridMap";
	private boolean m_bounded;
	private ArrayList<Entity> m_entities;
	private int m_width, m_height;

	/**
	 * Initializes GridMap.
	 * 
	 * @param w       Width of the GridMap. Must be > 0.
	 * @param h       Height of the GridMap. Must be > 0.
	 * @param bounded if {@code true} then the GridMap will remain at the given size
	 *                of ({@code w}, {@code h}). If {@code false} then the GridMap
	 *                will automatically expand when an out-of-bounds location is
	 *                accessed.
	 */
	public GridMap(int width, int height, boolean bounded) {
		if (width < 1 || height < 1)
			throw new RuntimeException("GridMap: Invalid size: (" + width + ", " + height + ")");
		m_bounded = bounded;
		m_entities = new ArrayList<Entity>();
		m_width = width;
		m_height = height;
	}


	public void addEntity(Entity e) {
		m_entities.add(e);
	}


	public ArrayList<Entity> entities() {
		return m_entities;
	}


	/**
	 * If cols is negative, will expand everything by abs(cols) and then shift
	 * everything down by abs(cols). Else, will expand cols in the map to match the
	 * the number given.
	 * 
	 * @param cols
	 */
	public void expandColumns(int cols) {
		// TODO
	}


	/**
	 * If rows is negative, will expand everything by abs(rows) and then shift
	 * everything to the right by abs(rows). Else, will expand rows in the map to
	 * match the the number given.
	 * 
	 * @param rows
	 */
	public void expandRows(int rows) {
		int expandLength = Math.abs(rows);

		/*
		 * for (int i = 0; i < expandLength; i++) m_entities.addRow();
		 */

		if (rows < 0)
			for (int i = 0; i < expandLength; i++)
				shiftRight(false);
	}


	/**
	 * 
	 * @param loc
	 * @return Returns the Entity at the given Location, or {@code null} if no
	 *         Entity is found.
	 */
	public Entity get(Location loc) {
		int x = loc.getX();
		int y = loc.getY();

		if (x < 0 || y < 0 || x >= m_width || y >= m_height) {
			Log.get().throwRuntimeException(TAG, "get(Location): Location Out of Bounds");
			return null;
		}

		for (Entity entity : m_entities)
			if (entity.getLocation().equals(loc))
				return entity;

		return null;
	}


	public int getHeight() {
		return m_height;
	}


	public int getWidth() {
		return m_width;
	}


	public boolean isBounded() {
		return m_bounded;
	}


	public void setBounded(boolean b) {
		m_bounded = b;
	}


	/**
	 * Will attempt to shift everything to the right. If {@code allowClipping} ==
	 * {@code false}, then the function will check for clipping first. If it finds a
	 * clipping issue will occur, it doesn't shift anything and returns
	 * {@code false}. Clipping is when a map tile that is non-null with a non-null
	 * {@code Entity} and non-null {@code Terrain} would out-of-bounds after the
	 * shift, and would thus be lost. The GridMap size will not change.
	 * 
	 * @param allowClipping
	 * @return False if the shift failed. This would occur if {@code allowClipping}
	 *         is true and an entity would have been moved Out-Of-Bounds. Otherwise,
	 *         true is returned.
	 */
	public boolean shiftRight(boolean allowClipping) {
		// Check for clipping
		if (!allowClipping)
			for (Entity entity : m_entities)
				if (entity.getLocation().getX() + 1 >= m_width)
					return false;

		// Shift right
		for (Entity entity : m_entities)
			entity.getLocation().modX(1);
		return true;
	}


	public void step() {
		for (Entity entity : m_entities) {
			entity.step();
		}
	}

}
