package armorsearch.memorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Decoration;
import models.Equipment;

public class MemorizationCache {

    // Maps Key1 - > Key2 -> skill chart
    // How the keys are created is below.
    Map<String, Map<String, CacheData>> cache = new HashMap<>();

    /**
     * The function will extract the ids from the objects: equipment and decoration
     * and then sort it. Then append the equipmentId to create key1 and append deocration id to create key2
     *
     * @param equipments List of Equipment that correspond to the tempEquipmentNode Structure
     * @param decorations decorations that matches the desired skills from the user.
     * @return
     */
    public CacheData getActivatedSkill(String key1, String key2) {
        Map<String, CacheData> equipmentCache = cache.get(key1);
        if (equipmentCache != null) {
            return equipmentCache.get(key2);
        }
        return null;
    }

    public synchronized void putActivatedSkill(String key1, String key2,  CacheData cacheData) {
        Map<String, CacheData> equipmentCache = cache.get(key1);
        if (equipmentCache == null) {
            equipmentCache = new HashMap<>();
        }
        equipmentCache.put(key2, cacheData);
        cache.put(key1, equipmentCache);
    }

    public static String createEquipmentIdkey(List<Equipment> equipments) {
        // sort the equipment ids, and and appends it.
        return equipments.stream().map(Equipment::getId).sorted(Integer::compareTo).map(Object::toString).reduce("", String::concat);
    }

    public static String createDecorationIdKey(List<Decoration> decorations) {
        return decorations.stream().map(Decoration::getId).sorted(Integer::compareTo).map(Object::toString).reduce("", String::concat);
    }
}
