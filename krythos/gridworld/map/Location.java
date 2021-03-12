package krythos.gridworld.map;

public class Location {
	private int m_x, m_y;


	/**
	 * Initializes to (0, 0).
	 */
	public Location() {
		this(0, 0);
	}


	/**
	 * Initializes Location to given coordinates.
	 * 
	 * @param x
	 * @param y
	 */
	public Location(int x, int y) {
		setX(x);
		setY(y);
	}


	/**
	 * Set X coordinate.
	 * 
	 * @param x
	 */
	public void setX(int x) {
		m_x = x;
	}


	/**
	 * Set Y coordinate.
	 * 
	 * @param y
	 */
	public void setY(int y) {
		m_y = y;
	}


	/**
	 * @return X coordinate of this Location
	 */
	public int getX() {
		return m_x;
	}


	/**
	 * @return Y coordinate of this Location
	 */
	public int getY() {
		return m_y;
	}


	/**
	 * Modify this Location's X & Y value. Will indifidually execute
	 * {@link #modX(int) modX(x)} and {@link #modY(int) modY(y)}.
	 * 
	 * @param x
	 * @param y
	 */
	public void mod(int x, int y) {
		modX(x);
		modY(y);
	}


	/**
	 * Modify this Location's X value. Essentially {@code X = X + dX}
	 * 
	 * @param x
	 */
	public void modX(int x) {
		setX(getX() + x);
	}


	/**
	 * Modify this Location's Y value. Essentially {@code Y = Y + dY}
	 * 
	 * @param y
	 */
	public void modY(int y) {
		setY(getY() + y);
	}


	/**
	 * Return's this Location's coordinates in a {@code (X, Y)} format.
	 */
	@Override
	public String toString() {
		return String.format("(%d, %d)", m_x, m_y);
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Location) {
			Location other = (Location) obj;
			return other.getX() == getX() && other.getY() == getY();
		}
		return false;
	}
}
