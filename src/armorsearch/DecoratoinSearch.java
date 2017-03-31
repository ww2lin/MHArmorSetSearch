package armorsearch;

import java.util.List;
import java.util.Map;
import models.Decoration;
import models.Equipment;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.ActivatedSkillWithDecoration;
import models.skillactivation.SkillActivationChart;
import models.skillactivation.SkillUtil;

class DecoratoinSearch {
    /**
     * DFS - try to see if the desire skills are obtainable given a list of equipments
     * can have multiple decoration list with the same outcome.
     */
    public static void findArmorWithJewelRecursively(Map<String, List<Decoration>> decorationLookupTable,
                                                     SkillActivationChart skillActivationChart,
                                                     List<Equipment> currentSet,
                                                     int equipmentIndex,
                                                     List<ActivatedSkillWithDecoration> activatedSkillWithDecoration,
                                                     List<ActivatedSkill> desiredSkills,
                                                     List<Decoration> decorationsNeeded){

        if (equipmentIndex == currentSet.size()){
            ActivatedSkillWithDecoration activatedSkill = skillActivationChart.getActivatedSkill(currentSet, decorationsNeeded);

            if (SkillUtil.containsDesiredSkills(desiredSkills, activatedSkill.getActivatedSkills())) {
                // create a new array reference for current set, so that when back tracking the list is not modified
                activatedSkillWithDecoration.add(activatedSkill);
            }
            return;
        }

        for (ActivatedSkill activatedSkill : desiredSkills) {
            List<Decoration> decorationsToTry = decorationLookupTable.get(activatedSkill.getKind());
            for (Decoration decoration : decorationsToTry) {
                Equipment equipment = currentSet.get(equipmentIndex);

                // TODO if current set contains one of desire skills,
                // then remove it and backk the rest of the desire skill to remove useless search

                if (equipment.getFreeSlots() >= decoration.getSlotsNeeded()) {
                    decorationsNeeded.add(decoration);
                    equipment.useSlots(decoration.getSlotsNeeded());

                    findArmorWithJewelRecursively(decorationLookupTable,
                                                  skillActivationChart,
                                                  currentSet,
                                                  equipmentIndex + 1,
                                                  activatedSkillWithDecoration,
                                                  desiredSkills,
                                                  decorationsNeeded);
                    //back-tracking.
                    equipment.useSlots(-decoration.getSlotsNeeded());
                    decorationsNeeded.remove(decoration);
                }
            }

        }
    }
}
