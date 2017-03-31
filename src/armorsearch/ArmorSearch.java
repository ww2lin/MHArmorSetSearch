package armorsearch;

import armorsearch.armorcache.ArmorSkillCacheTable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import models.Equipment;
import models.GeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;
import models.skillactivation.SkillUtil;

public class ArmorSearch {

    /**
     * run a dfs search for the skill search
     * @param desiredSkills that the user wants to generate
     * @return list of equipment that matches what the user wants
     */
    public List<GeneratedArmorSet> findArmorSetWith(SkillActivationChart skillActivationChart, List<ActivatedSkill> desiredSkills, ArmorSkillCacheTable armorSkillCacheTable) {
        // construct the node structure to use for dfs search
        EquipmentNode leg = new EquipmentNode(null);
        EquipmentNode wst = new EquipmentNode(leg);
        EquipmentNode arm = new EquipmentNode(wst);
        EquipmentNode body = new EquipmentNode(arm);
        EquipmentNode head = new EquipmentNode(body);

        // get the armor table to search through
        head.updateEquipmentListWithDesiredSkills(armorSkillCacheTable.getHeadEquipmentCache(), desiredSkills);
        body.updateEquipmentListWithDesiredSkills(armorSkillCacheTable.getBodyEquipmentCache(), desiredSkills);
        arm.updateEquipmentListWithDesiredSkills(armorSkillCacheTable.getArmEquipmentCache(), desiredSkills);
        wst.updateEquipmentListWithDesiredSkills(armorSkillCacheTable.getWstEquipmentCache(), desiredSkills);
        leg.updateEquipmentListWithDesiredSkills(armorSkillCacheTable.getLegEquipmentCache(), desiredSkills);


        List<GeneratedArmorSet> matchedSets = new ArrayList<>();
        findArmorRecursively(skillActivationChart, new LinkedList<>(), head, matchedSets, desiredSkills);
        return matchedSets;
    }

    private void findArmorRecursively(SkillActivationChart skillActivationChart, List<Equipment> currentSet, EquipmentNode equipmentNode, List<GeneratedArmorSet> matchedSet, List<ActivatedSkill> desiredSkills) {
        if (equipmentNode != null) {
            List<Equipment> equipments = equipmentNode.armorWithDesiredSkills;
            for (Equipment equipment : equipments) {
                currentSet.add(equipment);
                findArmorRecursively(skillActivationChart, currentSet, equipmentNode.next, matchedSet, desiredSkills);

                // back tracking.
                currentSet.remove(equipment);
            }

        } else {
            // we found a potential a full set...

            // check if this set contains the skill desired.
            List<ActivatedSkill> activatedSkills = skillActivationChart.getActiavtedSkill(currentSet);

            if (SkillUtil.containsDesiredSkills(desiredSkills, activatedSkills)) {
                // create a new array reference, so that when back tracking the sets is not modified
                matchedSet.add(new GeneratedArmorSet(activatedSkills, new ArrayList<>(currentSet)));
            }
        }
    }
}
