package armorsearch.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Decoration;
import models.EquipmentType;

public class EquipmentSlots {
    final Map<Decoration, Integer> decorations = new HashMap<>();
    int slots;
    int slotsUsed;

    public EquipmentSlots(int slots) {
        this.slots = slots;
        slotsUsed = 0;
    }

    public int getSlotsUsed() {
        return slotsUsed;
    }

    public void useSlots(int numberOfSlots) {
        slotsUsed+=numberOfSlots;
    }

    public int getFreeSlots() {
        return slots - slotsUsed;
    }

    public Map<Decoration, Integer> getDecorations() {
        return decorations;
    }

    public void removeDecoration(Decoration decoration){
        Integer frequency = decorations.get(decoration);
        if (frequency == null || frequency <= 0){
            return;
        }
        --frequency;
        if (frequency == 0){
            decorations.remove(decoration);
        } else {
            decorations.put(decoration, frequency);
        }
    }

    public void addDecoration(Decoration decoration){
        Integer frequency = decorations.get(decoration);
        if (frequency == null){
            frequency = 0;
        }
        ++frequency;
        decorations.put(decoration, frequency);
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    private static List<Decoration> getDecorationList(EquipmentSlots equipmentSlots){
        List<Decoration> decorationsList = new ArrayList<>();
        for (Map.Entry<Decoration, Integer> decorationSet : equipmentSlots.decorations.entrySet()) {
            Decoration decoration = decorationSet.getKey();
            int count = decorationSet.getValue();
            for (int i = 0; i < count; ++i){
                decorationsList.add(decoration);
            }
        }
        return decorationsList;
    }

    public static List<Decoration> getDecorationFromMap(Map<EquipmentType, EquipmentSlots> equipmentSlotsMap) {
        List<Decoration> decorationsList = new ArrayList<>();
        for (Map.Entry<EquipmentType, EquipmentSlots> equipmentSlotsEntry : equipmentSlotsMap.entrySet()) {
            EquipmentSlots equipmentSlots = equipmentSlotsEntry.getValue();
            decorationsList.addAll(getDecorationList(equipmentSlots));
        }
        return decorationsList;
    }
}
