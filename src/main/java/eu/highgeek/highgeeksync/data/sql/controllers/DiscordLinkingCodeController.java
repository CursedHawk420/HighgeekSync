package eu.highgeek.highgeeksync.data.sql.controllers;

import eu.highgeek.highgeeksync.data.sql.entities.DiscordLinkingCode;
import eu.highgeek.highgeeksync.data.sql.entities.VirtualInventories;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class DiscordLinkingCodeController {

    private final SessionFactory sessionFactory;

    public DiscordLinkingCodeController(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    public DiscordLinkingCode createNewDiscordLinkingCode(Player player, String code) {
        DiscordLinkingCode discordLinkingCode = new DiscordLinkingCode(code, player.getUniqueId().toString());
        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(discordLinkingCode);
        session.getTransaction().commit();
        session.close();
        return discordLinkingCode;
    }



    public DiscordLinkingCode getPlayerDiscordLinkingCode(Player player) {
        Session session = this.sessionFactory.getCurrentSession();
        session.beginTransaction();
        EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<DiscordLinkingCode> criteriaQuery = criteriaBuilder.createQuery(DiscordLinkingCode.class);
        Root<DiscordLinkingCode> linkingCodeRoot = criteriaQuery.from(DiscordLinkingCode.class);

        Predicate departmentPredicate = criteriaBuilder.equal(linkingCodeRoot.get("uuid"), player.getUniqueId().toString());
        Path<Object> sortByPath = linkingCodeRoot.get("uuid");
        criteriaQuery.orderBy(criteriaBuilder.asc(sortByPath));

        criteriaQuery.select(linkingCodeRoot).where(departmentPredicate);

        Query query = entityManager.createQuery(criteriaQuery);
        DiscordLinkingCode ret = (DiscordLinkingCode) query.getSingleResult();
        entityManager.close();
        session.close();
        return ret;
    }
}
