package armorsearch.filter;

import java.util.List;
import java.util.stream.Collectors;
import models.Equipment;
import models.GeneratedArmorSet;

public class RarityFilter implements ArmorFilter, ArmorSetFilter {

    private int rarity;

    public RarityFilter(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public List<Equipment> filter(List<Equipment> equipmentList) {
        return equipmentList.stream().filter(equipment -> equipment.getRarity() >= rarity).collect(Collectors.toList());
    }

    @Override
    public boolean isArmorValid(List<Equipment> currentSet) {
        return filter(currentSet).size() > 0;
    }
}
