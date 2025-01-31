package eu.highgeek.highgeeksync.data.sql.entities;
import org.hibernate.SessionFactory;

public class InventoryController {
    
	private final SessionFactory sessionFactory;

	public InventoryController(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
/*
	public PlayerData createNewPlayerData(Player player) {
		PlayerData playerData = new PlayerData(player.getUniqueId().toString(), 0, 0, 0.0);

		Session session = this.sessionFactory.getCurrentSession();

		session.beginTransaction();
		session.save(playerData);
		session.getTransaction().commit();
		session.close();

		return playerData;
	}

	public PlayerData getPlayerData(Player player) {
		Session session = this.sessionFactory.getCurrentSession();
		session.beginTransaction();
		PlayerData data = session.get(PlayerData.class, player.getUniqueId().toString());
		session.getTransaction().commit();
		session.close();
		return data;
	}

	public void updatePlayerData(PlayerData playerData) {
		Session session = this.sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.update(playerData);
		session.getTransaction().commit();
		session.close();
	}

	public void deletePlayerData(PlayerData playerData) {
		Session session = this.sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.delete(playerData);
		session.getTransaction().commit();
		session.close();
	}
    */
}
