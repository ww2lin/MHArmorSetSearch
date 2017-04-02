package armorsearch;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.Decoration;
import models.Equipment;
import models.GeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;
import models.skillactivation.SkillUtil;

class DecorationSearch {
    /**
     * DFS - try to see if the desire skills are obtainable given a list of equipments
     * can have multiple decoration list with the same outcome.
     */
    static void findArmorWithJewelRecursively(final int decorationSearchLimit,
                                              Map<String, List<Decoration>> decorationLookupTable,
                                              SkillActivationChart skillActivationChart,
                                              List<Equipment> currentSet,
                                              int equipmentIndex,
                                              List<GeneratedArmorSet> generatedArmorSets,
                                              List<ActivatedSkill> desiredSkills,
                                              List<Decoration> decorationsNeeded) {

        if (desiredSkills.isEmpty() || generatedArmorSets.size() >= decorationSearchLimit || equipmentIndex == currentSet.size()){
            List<ActivatedSkill> activatedSkill = skillActivationChart.getActivatedSkill(currentSet);

            if (SkillUtil.containsDesiredSkills(desiredSkills, activatedSkill)) {
                //if (desiredSkills.isEmpty() && equipmentIndex < currentSet.size()) {
                    // The skill can be obtained with less than 5 armor pieces.
                    // TODO this is check is wrong, fix it later.
                    // Even if we do not use the slots, we might still need this armor's skills
                    //for (int i = 0; i < currentSet.size(); ++i){
                    //    currentSet.get(i).setCanBeSubstitutedForAnyOtherEquipment(true);
                    //}
                //}

                // Deep copy the equipment so the slots, and decorations usage dont get reseted.
                List<Equipment> deepCopyCurrentSet = currentSet.stream().map(Equipment::new).collect(Collectors.toList());
                generatedArmorSets.add(new GeneratedArmorSet(activatedSkill, deepCopyCurrentSet));
            }
            return;
        }

        Equipment equipment = currentSet.get(equipmentIndex);

        for (ActivatedSkill activatedSkill : desiredSkills) {
            List<Decoration> decorationsToTry = decorationLookupTable.get(activatedSkill.getKind());

            if (decorationsToTry != null) {
                for (Decoration decoration : decorationsToTry) {
                    if (!decoration.isAvailable() || !decoration.isPositive(activatedSkill.getKind())){
                        // skip negative or not available jewels.
                        continue;
                    }

                    // TODO if current set contains one of desire skills,
                    // then remove it and back the rest of the desire skill to remove useless search
                    if (equipment.getFreeSlots() >= decoration.getSlotsNeeded()) {
                        // Try a decoration
                        decorationsNeeded.add(decoration);
                        equipment.useSlots(decoration.getSlotsNeeded());
                        equipment.addDecoration(decoration);

                        // Check to see if there is any skill that we can cut out, E.g if we have AuL we do not need anymore attack jewels.
                        List<ActivatedSkill> skillsToFilterOut = skillActivationChart.getActivatedSkill(currentSet);
                        // Find the skill that has been maxed out
                        List<ActivatedSkill> activatedSkillsFilter = skillsToFilterOut.stream().filter(filterOut ->
                            filterOut.getPointsNeededToActivate() >= skillActivationChart.getMaxedActivatedSkill(filterOut.getKind()).getPointsNeededToActivate()
                        ).collect(Collectors.toList());

                        List<ActivatedSkill> filteredDesiredSkills = desiredSkills.stream().collect(Collectors.toList());
                        filteredDesiredSkills.removeAll(activatedSkillsFilter);

                        findArmorWithJewelRecursively(decorationSearchLimit,
                                                      decorationLookupTable,
                                                      skillActivationChart,
                                                      currentSet,
                                                      equipmentIndex + 1,
                                                      generatedArmorSets,
                                                      filteredDesiredSkills,
                                                      decorationsNeeded);
                        //back-tracking.
                        equipment.useSlots(-decoration.getSlotsNeeded());
                        equipment.removeDecoration(decoration);
                        decorationsNeeded.remove(decoration);
                    }
                }
            }

        }
    }
}