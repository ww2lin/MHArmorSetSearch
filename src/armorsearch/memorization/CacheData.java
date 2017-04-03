package armorsearch.memorization;

import armorsearch.thread.EquipmentSlots;
import java.util.Map;
import models.EquipmentType;

public class CacheData {

    public Map<String, Integer> skillChart;
    public Map<EquipmentType, EquipmentSlots> equipmentSlotsMap;

    public CacheData(Map<String, Integer> skillChart, Map<EquipmentType, EquipmentSlots> equipmentSlotsMap) {
        this.skillChart = skillChart;
        this.equipmentSlotsMap = equipmentSlotsMap;
    }
}
