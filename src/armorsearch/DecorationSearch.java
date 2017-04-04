package armorsearch;

import armorsearch.thread.EquipmentNode;
import constants.Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Decoration;
import models.Equipment;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;

public class DecorationSearch {

    private List<Decoration> decorationsToTry = new ArrayList<>();
    private Map<Integer, List<Decoration>> slotToDecorationMap = new HashMap<>();
    private SkillChartDataList[] decorationSkillTable;

    public DecorationSearch(List<ActivatedSkill> desiredSkills, Map<String, List<Decoration>> decorationLookupTable) {

        for (ActivatedSkill activatedSkill : desiredSkills) {
            List<Decoration> decorations = decorationLookupTable.get(activatedSkill.getKind());
            if (decorations != null) {
                for (Decoration decoration : decorations) {
                    if (!decoration.isAvailable() || !decoration.isPositive(activatedSkill.getKind())) {
                        // skip negative or not available jewels.
                        continue;
                    }
                    List<Decoration> sameSlotDecorations = slotToDecorationMap.get(decoration.getSlotsNeeded());
                    if (sameSlotDecorations == null) {
                        sameSlotDecorations = new ArrayList<>();
                    }
                    sameSlotDecorations.add(decoration);
                    slotToDecorationMap.put(decoration.getSlotsNeeded(), sameSlotDecorations);
                    decorationsToTry.add(decoration);
                }
            }
        }
        decorationSkillTable = initDecorationSkillChart();
    }

    /**
     * Use DP to build a decoration skill chart based on desired skill (decorations)
     *
     * @return DP table use to look up skill chart by slot number.
     */
    private SkillChartDataList[] initDecorationSkillChart() {
        final int slots = Constants.MAX_SLOTS;
        SkillChartDataList[] table = new SkillChartDataList[slots + 1];

        // base case:
        // no slots, then no skill activated from slots.
        table[0] = new SkillChartDataList(Collections.emptyList());

        // iterative case:
        // use all the i-th slots
        for (int i = 1; i <= slots; ++i) {
            SkillChartDataList skillChartDataList = new SkillChartDataList();
            List<Decoration> decorations = slotToDecorationMap.get(i);
            if (decorations != null) {
                decorations.forEach(decoration -> {
                    Map<String, Integer> skillChart = SkillActivationChart.getSkillChart(decoration);
                    List<Decoration> useDecoration = new ArrayList<>();
                    useDecoration.add(decoration);
                    skillChartDataList.add(new SkillChartWithDecoration(useDecoration, skillChart));
                });
            }

            // calculate sub-problems, e.g all the sub slots that makes slot i
            for (int j = 1; i > j && j <= Math.ceil((float)i / 2); ++j) {
                SkillChartDataList subProblem1 = table[i-j];
                SkillChartDataList subProblem2 = table[j];
                // cross product the two sets.

                SkillChartDataList result = SkillChartDataList.cartesianProduct(subProblem1, subProblem2);

                skillChartDataList.addAll(result);
            }

            table[i] = skillChartDataList;
        }
        return table;
    }

    public EquipmentNode findArmorWithDecoration(Equipment equipment){
        int slots = equipment.getSlots();
        SkillChartDataList skillChartDataList = decorationSkillTable[slots];
        Map<String, Integer> equipmentskillChart = SkillActivationChart.getActivatedSkillChart(equipment);

        for (SkillChartWithDecoration skillChartWithDecoration : skillChartDataList.getSkillChartWithDecorations()) {
            Map<String, Integer> skillChartWithEquipment = SkillActivationChart.add(equipmentskillChart, skillChartWithDecoration.skillChart);
            Equipment copied = new Equipment(equipment);
            copied.addAllDecorations(skillChartWithDecoration.decorations);

            // Since we always use the max slots possible, we will have 0 slots left
            copied.setSlots(0);

            return new EquipmentNode(copied, skillChartWithEquipment);
        }
        return new EquipmentNode(equipment, equipmentskillChart);
    }

    public List<EquipmentNode> findArmorWithDecorationRecursively(Equipment equipment) {
        Equipment newEquipment = new Equipment(equipment);
        List<EquipmentNode> equipmentNodes = new ArrayList<>();
        findEquipmentWithDecoration(newEquipment, equipmentNodes);
        return equipmentNodes;
    }

    private void findEquipmentWithDecoration(Equipment equipment, List<EquipmentNode> equipmentNodes) {
        if (equipment.hasFreeSlots()) {
            for (Decoration decoration : decorationsToTry) {
                if (equipment.useSlots(decoration)) {
                    findEquipmentWithDecoration(equipment, equipmentNodes);
                    equipment.freeSlots(decoration);
                }
            }
        }
        Equipment newEquipment = new Equipment(equipment);
        Map<String, Integer> skillTable = SkillActivationChart.getActivatedSkillChart(newEquipment);
        List<Equipment> tempList = new ArrayList<>();
        tempList.add(newEquipment);
        equipmentNodes.add(new EquipmentNode(tempList, skillTable));
    }

    // wrapper for the below object.
    private static class SkillChartDataList {
        private List<SkillChartWithDecoration> skillChartWithDecorations = new ArrayList<>();

        public SkillChartDataList(List<SkillChartWithDecoration> skillChartWithDecorations) {
            this.skillChartWithDecorations = skillChartWithDecorations;
        }

        public SkillChartDataList() {
        }

        public void add(SkillChartWithDecoration skillChartWithDecoration){
            skillChartWithDecorations.add(skillChartWithDecoration);
        }

        public void addAll(SkillChartDataList skillChartDataList){
            skillChartWithDecorations.addAll(skillChartDataList.skillChartWithDecorations);
        }

        public SkillChartWithDecoration get(int index){
            return skillChartWithDecorations.get(index);
        }

        public List<SkillChartWithDecoration> getSkillChartWithDecorations() {
            return skillChartWithDecorations;
        }

        public static SkillChartDataList cartesianProduct(SkillChartDataList list1, SkillChartDataList list2){
            SkillChartDataList skillChartDataList = new SkillChartDataList();
            for (SkillChartWithDecoration skillChartWithDecoration1 : list1.skillChartWithDecorations) {
                for (SkillChartWithDecoration skillChartWithDecoration2 : list2.skillChartWithDecorations) {
                    SkillChartWithDecoration newSkillChart = SkillChartWithDecoration.add(skillChartWithDecoration1, skillChartWithDecoration2);
                    skillChartDataList.add(newSkillChart);
                }
            }
            return skillChartDataList;
        }
    }

    /**
     * Each skill chart corresponds to a list of decorations.
     */
    private static class SkillChartWithDecoration {
        // TODO change this into decoration -> frequency
        List<Decoration> decorations = new ArrayList<>();
        Map<String, Integer> skillChart = new HashMap<>();

        public SkillChartWithDecoration(List<Decoration> decorations, Map<String, Integer> skillChart) {
            this.decorations = decorations;
            this.skillChart = skillChart;
        }

        public SkillChartWithDecoration() {
        }

        public static SkillChartWithDecoration add(SkillChartWithDecoration skillChart1, SkillChartWithDecoration skillChart2){
            SkillChartWithDecoration newSkillChart = new SkillChartWithDecoration();
            newSkillChart.decorations.addAll(skillChart1.decorations);
            newSkillChart.decorations.addAll(skillChart2.decorations);
            newSkillChart.skillChart = SkillActivationChart.add(skillChart1.skillChart, skillChart2.skillChart);
            return newSkillChart;
        }
    }
}
