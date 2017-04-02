package armorsearch;

import java.util.HashMap;
import java.util.Map;
import models.Decoration;

public class EquipmentSlots {
    final Map<Decoration, Integer> decorations = new HashMap<>();
    final int slots;
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
}
