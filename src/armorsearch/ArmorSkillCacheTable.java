package armorsearch;

import armorsearch.filter.ArmorFilter;
import armorsearch.filter.MaxArmorSkillPointsFilter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import models.ArmorSkill;
import models.ClassType;
import models.Equipment;
import models.Gender;
import models.skillactivation.SkillActivationChart;

public class ArmorSkillCacheTable {
    // Build a table from kind -> All equipment has that kind of skill
    private Map<String, List<Equipment>> headEquipmentCache = new HashMap<>();
    private Map<String, List<Equipment>> bodyEquipmentCache = new HashMap<>();
    private Map<String, List<Equipment>> armEquipmentCache = new HashMap<>();
    private Map<String, List<Equipment>> wstEquipmentCache = new HashMap<>();
    private Map<String, List<Equipment>> legEquipmentCache = new HashMap<>();

    private List<ArmorFilter> armorFilters;
    private ClassType classType;
    private Gender gender;
    /**
     * Build a look up table by skill, for faster lookup time. E.g
     * headEquipmentCache: skillKind -> All head armor that has this skill.
     * This construction should be moved into the csv while generating the List of equipments
     */
    //TODO fix torso up armors, and 3 slotted armors
    public ArmorSkillCacheTable(SkillActivationChart skillActivationChart, AllEquipments allEquipments, List<ArmorFilter> armorFilters, ClassType classType, Gender gender) {
        this.armorFilters = armorFilters;
        this.classType = classType;
        this.gender = gender;

        Set<String> skillKinds = skillActivationChart.getSkillKind();

        for (String skillkind : skillKinds) {
            updateCacheBySkillKind(allEquipments.getHeadEquipments(), headEquipmentCache, skillkind);
            updateCacheBySkillKind(allEquipments.getBodyEquipments(), bodyEquipmentCache, skillkind);
            updateCacheBySkillKind(allEquipments.getArmEquipments(), armEquipmentCache, skillkind);
            updateCacheBySkillKind(allEquipments.getWstEquipments(), wstEquipmentCache, skillkind);
            updateCacheBySkillKind(allEquipments.getLegEquipments(), legEquipmentCache, skillkind);
        }
    }

    private void updateCacheBySkillKind(final List<Equipment> equipmentData, Map<String, List<Equipment>> currentCache, String skillKind){
        MaxArmorSkillPointsFilter maxArmorSkillPointsFilter = new MaxArmorSkillPointsFilter(skillKind);

        List<Equipment> equipmentsByKind = currentCache.get(skillKind);
        if (equipmentsByKind == null){
            equipmentsByKind = new LinkedList<>();
        }

        List<Equipment> filterBySkillKind = getEquipmentBySkillKind(equipmentData, skillKind);

        // Apply the filter Function and add it to the list
        equipmentsByKind.addAll(maxArmorSkillPointsFilter.filter(doFilter(filterBySkillKind)));

        currentCache.put(skillKind, equipmentsByKind);
    }

    /**
     * @param equipments a list of equipment, head, body...
     * @param skillkind which skill we are trying to search for
     * @return all the equipment that matches the skill kind
     */
    private List<Equipment> getEquipmentBySkillKind(List<Equipment> equipments, String skillkind) {
        return equipments.stream().filter(
            (equipment) -> {
                boolean isArmorAvailable = equipment.isAvailable();
                boolean validGender = (equipment.getGender() == Gender.BOTH || equipment.getGender() == gender);
                boolean validClassType = (equipment.getClassType() == ClassType.ALL || equipment.getClassType() == classType);
                if (validGender && validClassType && isArmorAvailable) {
                    for (ArmorSkill armorSkill : equipment.getArmorSkills()) {
                        //TODO remove negative check?
                        if (armorSkill.isKind(skillkind) && armorSkill.points > 0) {
                            return true;
                        }
                        // keep TorsoUp Pieces, or armor with no skills but 3 slots
                        if (equipment.isTorsoUp()) {
                            return true;
                        }

                        if (equipment.getArmorSkills().size() == 0 && equipment.getSlots() == 3 ){
                            return true;
                        }

                    }
                }
                return false;
            }).collect(Collectors.toList());
    }

    private List<Equipment> doFilter(List<Equipment> equipments){
        for (ArmorFilter armorFilter : armorFilters) {
            equipments = armorFilter.filter(equipments);
        }

        return equipments;
    }

    public Map<String, List<Equipment>> getHeadEquipmentCache() {
        return headEquipmentCache;
    }

    public Map<String, List<Equipment>> getBodyEquipmentCache() {
        return bodyEquipmentCache;
    }

    public Map<String, List<Equipment>> getArmEquipmentCache() {
        return armEquipmentCache;
    }

    public Map<String, List<Equipment>> getWstEquipmentCache() {
        return wstEquipmentCache;
    }

    public Map<String, List<Equipment>> getLegEquipmentCache() {
        return legEquipmentCache;
    }
}
