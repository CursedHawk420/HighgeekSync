package eu.highgeek.highgeeksync.data.sql.daos;

import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.sql.entities.VirtualInventories;

import org.jetbrains.annotations.NotNull;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class VirtualInventoryDao extends BaseDao<VirtualInventories>{

    private final Logger logger;

    public VirtualInventoryDao(
            final @NotNull HighgeekSync highgeekSync
    ) {
        super(highgeekSync);
        this.logger = highgeekSync.getLogger();
    }


    public Optional<VirtualInventories> findByUUID(
            final @NotNull String uuid
    ) {
        Query<VirtualInventories> query = this.createNamedQuery("VirtualInventories.findByUUID");
        query.setParameter("inventory_uuid", uuid);
        return Optional.ofNullable(query.getSingleResultOrNull());
    }

    public List<VirtualInventories> findAll() {
        Query<VirtualInventories> query = this.createNamedQuery("AOPlayer.findAll");
        return query.getResultList();
    }


    @Override
    protected Class<VirtualInventories> getClazzType() {
        return VirtualInventories.class;
    }
}
