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
    public List<GeneratedArmorSet> filterArmorSet(List<GeneratedArmorSet> equipmentList) {
        return equipmentList.stream().filter(generatedArmorSet -> {
            for (Equipment equipment : generatedArmorSet.getEquipments()) {
                if (equipment.getRarity() < rarity) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }
}
