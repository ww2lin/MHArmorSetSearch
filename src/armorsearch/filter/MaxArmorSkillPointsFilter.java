package armorsearch.filter;

import java.util.ArrayList;
import java.util.List;
import models.ArmorSkill;
import models.Equipment;

/**
 * This Filter will be applied internally.
 *
 * This will select the equipments with MAX skill point from a list of equipments
 * OR
 * It will also select equipments that has MORE SLOTS than the equipment with the max skill points
 *
 * This Filter must be the last filter that is going to be applied, because
 * for example if this filter is applied before Rarity(3)
 * then after this filter is done, some of rare 3 armors might be dropped.
 *
 * So... why am I doing this?
 * Because without a way to cut down the number of armor set possibilities it will take
 * 'forever' to try and find all the combination of armor sets for a set of skills.
 */
public class MaxArmorSkillPointsFilter implements ArmorFilter{

    private String skillKind;

    public MaxArmorSkillPointsFilter(String skillKind) {
        this.skillKind = skillKind;
    }

    @Override
    public List<Equipment> filter(List<Equipment> equipmentList) {
        if (equipmentList.isEmpty()) {
            return equipmentList;
        }

        // Return the armor with the max points in skillkind or has more slots than the maxed skill point equipment
        List<Equipment> maxPointOrSlotsEquipments = new ArrayList<>();

        // Find the equipment with the max skill points first, and use it as a template
        // Then compare other gear's slot with the template, we only select the
        // new gear, if it has more slots.
        Equipment templateEquipment = equipmentList.get(0);
        int maxSkillPoints = findSkillPoint(templateEquipment);

        // find the armor with the max skill...
        for (Equipment equipment : equipmentList) {
            int currentMaxSkill = findSkillPoint(equipment);
            if (currentMaxSkill >= maxSkillPoints) {
                templateEquipment = equipment;
                maxSkillPoints = currentMaxSkill;
            }
        }

        // Find all the armors that has more slots than the template or the same skill points.
        for (Equipment equipment : equipmentList) {
            int currentMaxSkill = findSkillPoint(equipment);
            if (equipment.getSlots() > templateEquipment.getSlots() ||
                currentMaxSkill == maxSkillPoints)  {
                maxPointOrSlotsEquipments.add(equipment);
            }
        }
        return maxPointOrSlotsEquipments;
    }

    private int findSkillPoint(Equipment equipment) {
        for (ArmorSkill armorSkill : equipment.getArmorSkills()){
            if (armorSkill.isKind(skillKind)) {
                return armorSkill.points;
            }
        }
        return 0;
    }

}
