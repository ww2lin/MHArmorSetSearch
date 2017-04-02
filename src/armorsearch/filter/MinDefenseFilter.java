package armorsearch.filter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import models.Equipment;
import models.GeneratedArmorSet;

public class MinDefenseFilter implements ArmorSetFilter, ArmorFilter {

    private int minDefense;

    public MinDefenseFilter(int minDefense) {
        this.minDefense = minDefense;
    }

    @Override
    public List<Equipment> filter(List<Equipment> equipmentList) {
        int defense = 0;
        for (Equipment equipment : equipmentList) {
            defense+=equipment.getMaxDefense();
        }
        return defense >= minDefense ? equipmentList : Collections.emptyList();
    }

    @Override
    public boolean isArmorValid(List<Equipment> currentSet) {
        return currentSet.stream().mapToInt(Equipment::getMaxDefense).sum() >= minDefense;
    }
}
