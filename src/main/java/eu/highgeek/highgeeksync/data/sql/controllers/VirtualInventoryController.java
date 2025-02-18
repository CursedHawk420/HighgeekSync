package eu.highgeek.highgeeksync.data.sql.controllers;
import eu.highgeek.highgeeksync.data.sql.entities.VirtualInventories;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.bukkit.entity.Player;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class VirtualInventoryController {
    
	private final SessionFactory sessionFactory;

	public VirtualInventoryController(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public VirtualInventories createNewVirtualInventory(Player player, String name, Integer size, Boolean web) {
		VirtualInventories inventory = new VirtualInventories(UUID.randomUUID().toString(), player.getUniqueId().toString(), player.getName(), name, size, web, "", Instant.now().toString());
		Session session = this.sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.save(inventory);
		session.getTransaction().commit();
		session.close();
		return inventory;
	}



	public List<VirtualInventories> getPlayerVirtualInventories(Player player) {
		Session session = this.sessionFactory.getCurrentSession();
		session.beginTransaction();
		EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<VirtualInventories> criteriaQuery = criteriaBuilder.createQuery(VirtualInventories.class);
		Root<VirtualInventories> inventoriesRoot = criteriaQuery.from(VirtualInventories.class);

		Predicate departmentPredicate = criteriaBuilder.equal(inventoriesRoot.get("playerUuid"), player.getUniqueId().toString());
		Path<Object> sortByPath = inventoriesRoot.get("inventoryName");
		criteriaQuery.orderBy(criteriaBuilder.asc(sortByPath));

		criteriaQuery.select(inventoriesRoot).where(departmentPredicate);

		Query query = entityManager.createQuery(criteriaQuery);
		List<VirtualInventories> ret = query.getResultList();
		entityManager.close();
		session.close();
		return ret;
	}

	public List<VirtualInventories> getAllInventories(){
		Session session = this.sessionFactory.getCurrentSession();
		session.beginTransaction();

		EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<VirtualInventories> criteriaQuery = criteriaBuilder.createQuery(VirtualInventories.class);
		Root<VirtualInventories> inventoriesRoot = criteriaQuery.from(VirtualInventories.class);

		criteriaQuery.select(inventoriesRoot);

		Query query = entityManager.createQuery(criteriaQuery);
		List<VirtualInventories> ret = query.getResultList();
		entityManager.close();
		session.close();
		return ret;
	}

	public void updateVirtualInventory(VirtualInventories virtualInventories){
		Session session = this.sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.update(virtualInventories);
		session.getTransaction().commit();
		session.close();
	}

	public void deleteVirtualInventory(VirtualInventories virtualInventories) {
		Session session = this.sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.delete(virtualInventories);
		session.getTransaction().commit();
		session.close();
	}
}
