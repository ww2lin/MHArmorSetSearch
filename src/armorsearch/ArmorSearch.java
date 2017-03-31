package armorsearch;

import armorsearch.armorcache.ArmorSkillCacheTable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Decoration;
import models.Equipment;
import models.GeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.ActivatedSkillWithDecoration;
import models.skillactivation.SkillActivationChart;

public class ArmorSearch {

    /**
     * run a dfs search for the skill search
     * @param desiredSkills that the user wants to generate
     * @return list of equipment that matches what the user wants
     */
    public List<GeneratedArmorSet> findArmorSetWith(Map<String, List<Decoration>> decorationLookupTable,
                                                    final int searchLimit,
                                                    SkillActivationChart skillActivationChart,
                                                    List<ActivatedSkill> desiredSkills,
                                                    ArmorSkillCacheTable armorSkillCacheTable) {
        // construct the node structure to use for dfs search
        EquipmentNode leg = new EquipmentNode(null);
        EquipmentNode wst = new EquipmentNode(leg);
        EquipmentNode arm = new EquipmentNode(wst);
        EquipmentNode body = new EquipmentNode(arm);
        EquipmentNode head = new EquipmentNode(body);

        // get all the potential armor to search through
        head.updateEquipmentListWithDesiredSkills(armorSkillCacheTable.getHeadEquipmentCache(), desiredSkills);
        body.updateEquipmentListWithDesiredSkills(armorSkillCacheTable.getBodyEquipmentCache(), desiredSkills);
        arm.updateEquipmentListWithDesiredSkills(armorSkillCacheTable.getArmEquipmentCache(), desiredSkills);
        wst.updateEquipmentListWithDesiredSkills(armorSkillCacheTable.getWstEquipmentCache(), desiredSkills);
        leg.updateEquipmentListWithDesiredSkills(armorSkillCacheTable.getLegEquipmentCache(), desiredSkills);

        List<GeneratedArmorSet> matchedSets = new ArrayList<>();
        findArmorRecursively(decorationLookupTable,
                             searchLimit,
                             skillActivationChart,
                             new ArrayList<>(5),
                             head,
                             matchedSets,
                             desiredSkills);
        return matchedSets;
    }

    private void findArmorRecursively(Map<String, List<Decoration>> decorationLookupTable,
                                      final int searchLimit,
                                      SkillActivationChart skillActivationChart,
                                      List<Equipment> currentSet,
                                      EquipmentNode equipmentNode,
                                      List<GeneratedArmorSet> matchedSet,
                                      List<ActivatedSkill> desiredSkills) {
        // check to see if we hit the limit of armor search
        if (matchedSet.size() >= searchLimit) {
            return;
        } else if (equipmentNode != null) {
            List<Equipment> equipments = equipmentNode.armorWithDesiredSkills;
            for (Equipment equipment : equipments) {
                currentSet.add(equipment);
                findArmorRecursively(decorationLookupTable, searchLimit, skillActivationChart, currentSet, equipmentNode.next, matchedSet, desiredSkills);

                // back tracking.
                currentSet.remove(equipment);
            }
        } else {

            // we found a potential full set...
            List<ActivatedSkillWithDecoration> activatedSkillWithDecoration = new ArrayList<>();
            DecoratoinSearch.findArmorWithJewelRecursively(decorationLookupTable,
                                                           skillActivationChart,
                                                           currentSet,
                                                           0,
                                                           activatedSkillWithDecoration,
                                                           desiredSkills,
                                                           new ArrayList<>());

            if (!activatedSkillWithDecoration.isEmpty()) {
                // create a new array reference for current set, so that when back tracking the list is not modified
                matchedSet.add(new GeneratedArmorSet(activatedSkillWithDecoration, new ArrayList<>(currentSet)));
            }
        }
    }


}
