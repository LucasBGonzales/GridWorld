package krythos.gridworld.map;

public class GridMapTile {
	private Terrain m_terrain;
	private Entity m_entity;

	public GridMapTile() {


	}


	public void setTerrain(Terrain t) {
		m_terrain = t;
	}


	public void setEntity(Entity e) {
		m_entity = e;
	}


	public Terrain getTerrain() {
		return m_terrain;
	}


	public Entity getEntity() {
		return m_entity;
	}
}
