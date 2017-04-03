package armorsearch;

import armorsearch.memorization.MemorizationCache;
import armorsearch.thread.EquipmentSlots;
import armorsearch.thread.EquipmentNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.Decoration;
import models.Equipment;
import models.EquipmentType;
import models.GeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;
import models.skillactivation.SkillUtil;

public class DecorationSearch {

    /**
     * DFS - try to see if the desire skills are obtainable given a list of equipments
     * can have multiple decoration list with the same outcome.
     */
    static void findArmorWithJewelRecursively(Map<EquipmentType, EquipmentSlots> decorationsForCurrentSet,
                                       final int decorationSearchLimit,
                                       Map<String, List<Decoration>> decorationLookupTable,
                                       SkillActivationChart skillActivationChart,
                                       Map<EquipmentType, Equipment> currentSet,
                                       int equipmentIndex,
                                       List<GeneratedArmorSet> generatedArmorSets,
                                       List<ActivatedSkill> desiredSkills,
                                       List<Decoration> decorationsNeeded) {
        EquipmentType equipmentTypes[] = EquipmentType.values();
        if (desiredSkills.isEmpty() || generatedArmorSets.size() >= decorationSearchLimit || equipmentIndex == currentSet.size()) {
            List<ActivatedSkill> activatedSkill = skillActivationChart.getActivatedSkills(skillActivationChart.getActivatedSkillChart(currentSet, decorationsForCurrentSet));
            if (SkillUtil.containsDesiredSkills(desiredSkills, activatedSkill)) {
                //if (desiredSkills.isEmpty() && equipmentIndex < currentSet.size()) {
                // The skill can be obtained with less than 5 armor pieces.
                // TODO this check is wrong, fix it later. since even if we do not use the slots, we might still need this armor's skills
                //for (int i = 0; i < currentSet.size(); ++i){
                //    currentSet.get(i).setCanBeSubstitutedForAnyOtherEquipment(true);
                //}
                //}

                // Deep copy the equipment so the slots, and decorations usage dont get reseted.
                List<Equipment> deepCopyCurrentSet = new ArrayList<>(5);
                for (EquipmentType equipmentType : equipmentTypes) {
                    Equipment currentEquipment = currentSet.get(equipmentType);
                    //Equipment newEquipment = new Equipment(currentEquipment, decorationsForCurrentSet.get(equipmentType));
                    //deepCopyCurrentSet.add(newEquipment);
                }
                generatedArmorSets.add(new GeneratedArmorSet(activatedSkill, deepCopyCurrentSet));
            }
            return;
        }

        for (ActivatedSkill activatedSkill : desiredSkills) {
            List<Decoration> decorationsToTry = decorationLookupTable.get(activatedSkill.getKind());

            if (decorationsToTry != null) {
                for (Decoration decoration : decorationsToTry) {

                    if (generatedArmorSets.size() >= decorationSearchLimit) {
                        return;
                    }

                    if (!decoration.isAvailable() || !decoration.isPositive(activatedSkill.getKind())) {
                        // skip negative or not available jewels.
                        continue;
                    }

                    EquipmentSlots equipmentSlots = decorationsForCurrentSet.get(equipmentTypes[equipmentIndex]);
                    // then remove it and back the rest of the desire skill to remove useless search
                    if (equipmentSlots.getFreeSlots() >= decoration.getSlotsNeeded()) {
                        // Try a decoration
                        decorationsNeeded.add(decoration);
                        equipmentSlots.useSlots(decoration.getSlotsNeeded());
                        equipmentSlots.addDecoration(decoration);

                        // Check to see if there is any skill that we can cut out, E.g if we have AuL we do not need anymore attack jewels.
                        List<ActivatedSkill> skillsToFilterOut = skillActivationChart.getActivatedSkills(skillActivationChart.getActivatedSkillChart(currentSet, decorationsForCurrentSet));
                        // Find the skill that has been maxed out
                        List<ActivatedSkill> activatedSkillsFilter = skillsToFilterOut.stream().filter(filterOut ->
                                                                                                           filterOut.getPointsNeededToActivate() >= skillActivationChart.getMaxedActivatedSkill(filterOut.getKind()).getPointsNeededToActivate()
                        ).collect(Collectors.toList());

                        List<ActivatedSkill> filteredDesiredSkills = desiredSkills.stream().collect(Collectors.toList());
                        filteredDesiredSkills.removeAll(activatedSkillsFilter);

                        findArmorWithJewelRecursively(decorationsForCurrentSet,
                                                      decorationSearchLimit,
                                                      decorationLookupTable,
                                                      skillActivationChart,
                                                      currentSet,
                                                      equipmentIndex + 1,
                                                      generatedArmorSets,
                                                      filteredDesiredSkills,
                                                      decorationsNeeded);
                        //back-tracking.
                        equipmentSlots.useSlots(-decoration.getSlotsNeeded());
                        equipmentSlots.removeDecoration(decoration);
                        decorationsNeeded.remove(decoration);
                    }
                }
            }
        }
    }

    final int decorationSearchLimit;
    List<ActivatedSkill> desiredSkills;
    SkillActivationChart skillActivationChart;
    Map<String, List<Decoration>> decorationLookupTable;

    Map<EquipmentType, Equipment> currentSet;
    List<Equipment> currentSetWithNodeOrdering;
    MemorizationCache memorizationCache;

    List<Decoration> decorationsToTry = new ArrayList<>();

    public DecorationSearch(int decorationSearchLimit, List<ActivatedSkill> desiredSkills, SkillActivationChart skillActivationChart, Map<String, List<Decoration>> decorationLookupTable) {
        this.decorationSearchLimit = decorationSearchLimit;
        this.desiredSkills = desiredSkills;
        this.skillActivationChart = skillActivationChart;
        this.decorationLookupTable = decorationLookupTable;

        for (ActivatedSkill activatedSkill : desiredSkills) {
            List<Decoration> decorations = decorationLookupTable.get(activatedSkill.getKind());
            decorationsToTry.addAll(decorations);
        }
    }

    public List<EquipmentNode> findArmorWithDecorationRecursively(Equipment equipment){
        Equipment newEquipment = new Equipment(equipment);
        List<EquipmentNode> equipmentNodes = new ArrayList<>();
        findEquipmentWithDecoration(newEquipment, equipmentNodes);
        return equipmentNodes;
    }

    private void findEquipmentWithDecoration(Equipment equipment, List<EquipmentNode> equipmentNodes){
        for (Decoration decoration : decorationsToTry){
            if (equipment.useSlots(decoration)) {
                findEquipmentWithDecoration(equipment, equipmentNodes);
                equipment.freeSlots(decoration);
            }
        }
        Equipment newEquipment = new Equipment(equipment);
        Map<String, Integer> skillTable = SkillActivationChart.getActivatedSkillChart(newEquipment);
        List<Equipment> tempList = new ArrayList<>();
        tempList.add(newEquipment);
        equipmentNodes.add(new EquipmentNode(tempList, skillTable));
    }

    public List<GeneratedArmorSet> findArmorWithJewelRecursivelyWithMemorization(Map<EquipmentType, EquipmentSlots> decorationsForCurrentSet){
        List<GeneratedArmorSet> sameArmorDifferentDecoration = new ArrayList<>();
        for (Map.Entry<EquipmentType, Equipment> currentEquip : currentSet.entrySet()) {
            decorationsForCurrentSet.get(currentEquip.getKey()).setSlots(currentEquip.getValue().getSlots());
        }
        findArmorWithJewelRecursivelyWithMemorization(decorationsForCurrentSet, sameArmorDifferentDecoration, 0, desiredSkills);
        return sameArmorDifferentDecoration;
    }

    private void findArmorWithJewelRecursivelyWithMemorization(Map<EquipmentType, EquipmentSlots> decorationsForCurrentSet,
                                                       List<GeneratedArmorSet> generatedArmorSets,
                                                       int equipmentIndex,
                                                       List<ActivatedSkill> filteredDesiredSkills ){


    }

    public void setCurrentSet(Map<EquipmentType, Equipment> currentSet) {
        this.currentSet = currentSet;
    }

    public void setCurrentSetWithNodeOrdering(List<Equipment> currentSetWithNodeOrdering) {
        this.currentSetWithNodeOrdering = currentSetWithNodeOrdering;
    }


    static class Key {
        String equipmentKey;
        String decorationKey;
        List<Equipment> currentSetWithNodeOrdering;
        int equipmentIndex;
        List<Equipment> equipments;
        Map<EquipmentType, EquipmentSlots> tempDecorationMap;
        Map<EquipmentType, Equipment> tempEquipmentMap;
        Map<EquipmentType, EquipmentSlots> decorationsForCurrentSet;
        Map<String, Integer> skillChartAfterEquipIndex;

        public Key(List<Equipment> currentSetWithNodeOrdering, int equipmentIndex, Map<EquipmentType, EquipmentSlots> decorationsForCurrentSet) {
            this.currentSetWithNodeOrdering = currentSetWithNodeOrdering;
            this.equipmentIndex = equipmentIndex;
            this.decorationsForCurrentSet = decorationsForCurrentSet;

            calculateKey();
        }

        public void calculateKey(){
            equipments = currentSetWithNodeOrdering.subList(equipmentIndex, currentSetWithNodeOrdering.size());
            tempDecorationMap = new HashMap<>();
            tempEquipmentMap = new HashMap<>();

            equipments.forEach(equipment -> {
                tempDecorationMap.put(equipment.getEquipmentType(), decorationsForCurrentSet.get(equipment.getEquipmentType()));
                tempEquipmentMap.put(equipment.getEquipmentType(), equipment);
            });

            skillChartAfterEquipIndex = SkillActivationChart.getActivatedSkillChart(tempEquipmentMap, tempDecorationMap);

            equipmentKey = MemorizationCache.createEquipmentIdkey(equipments);
            decorationKey = MemorizationCache.createDecorationIdKey(EquipmentSlots.getDecorationFromMap(tempDecorationMap));

        }
    }
}
