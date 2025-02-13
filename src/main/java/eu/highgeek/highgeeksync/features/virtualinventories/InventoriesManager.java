package eu.highgeek.highgeeksync.features.virtualinventories;

import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;

public class InventoriesManager {

    @Getter
    private HashMap<String, List<HighgeekPlayer>> openedInventories = new HashMap<>();



    public InventoriesManager(){

    }
}
