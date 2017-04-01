package armorsearch;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    static void findArmorWithJewelRecursively(Map<String, List<Decoration>> decorationLookupTable,
                                                     SkillActivationChart skillActivationChart,
                                                     List<Equipment> currentSet,
                                                     int equipmentIndex,
                                                     List<ActivatedSkillWithDecoration> activatedSkillWithDecoration,
                                                     List<ActivatedSkill> desiredSkills,
                                                     List<Decoration> decorationsNeeded){

        if (equipmentIndex == currentSet.size()){
            ActivatedSkillWithDecoration activatedSkill = skillActivationChart.getActivatedSkill(currentSet, decorationsNeeded);

            if (SkillUtil.containsDesiredSkills(desiredSkills, activatedSkill.getActivatedSkills())) {
                // create an new array reference for current set, so that when back tracking the list is not modified
                activatedSkillWithDecoration.add(activatedSkill);
            }
            return;
        }

        Equipment equipment = currentSet.get(equipmentIndex);

        for (ActivatedSkill activatedSkill : desiredSkills) {
            List<Decoration> decorationsToTry = decorationLookupTable.get(activatedSkill.getKind());
            for (Decoration decoration : decorationsToTry) {

                // TODO if current set contains one of desire skills,
                // then remove it and back the rest of the desire skill to remove useless search
                if (equipment.getFreeSlots() >= decoration.getSlotsNeeded()) {
                    decorationsNeeded.add(decoration);
                    equipment.useSlots(decoration.getSlotsNeeded());

                    ActivatedSkillWithDecoration skillsToFilterOut = skillActivationChart.getActivatedSkill(currentSet, decorationsNeeded);
                    // Find the skill that has been maxed out
                    List<ActivatedSkill> activatedSkillsFilter = skillsToFilterOut.getActivatedSkills().stream().filter(filterOut ->
                        filterOut.getPointsNeededToActivate() >= skillActivationChart.getMaxedActivatedSkill(filterOut.getKind()).getAccumulatedPoints()
                    ).collect(Collectors.toList());

                    List<ActivatedSkill> filteredDesiredSkills = desiredSkills.stream().collect(Collectors.toList());
                    filteredDesiredSkills.removeAll(activatedSkillsFilter);

                    findArmorWithJewelRecursively(decorationLookupTable,
                                                  skillActivationChart,
                                                  currentSet,
                                                  equipmentIndex + 1,
                                                  activatedSkillWithDecoration,
                                                  filteredDesiredSkills,
                                                  decorationsNeeded);
                    //back-tracking.
                    equipment.useSlots(-decoration.getSlotsNeeded());
                    decorationsNeeded.remove(decoration);
                }
            }

        }
    }
}
