package armorsetsearch.decorationsearch;

import armorsetsearch.armorsearch.thread.EquipmentNode;
import constants.Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import models.ArmorSkill;
import models.Decoration;
import models.Equipment;
import armorsetsearch.skillactivation.ActivatedSkill;
import armorsetsearch.skillactivation.SkillActivationChart;

public class DecorationSearch {

    private Map<Integer, List<Decoration>> slotToDecorationMap = new HashMap<>();
    private SkillChartDataList[] decorationSkillTable;

    public DecorationSearch(List<ActivatedSkill> desiredSkills, Map<String, List<Decoration>> decorationLookupTable) {
        Set<String> wantedSkills = new HashSet<>();
        for (ActivatedSkill activatedSkill : desiredSkills) {
            wantedSkills.add(activatedSkill.getKind());
        }
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
                        sameSlotDecorations.add(decoration);
                        slotToDecorationMap.put(decoration.getSlotsNeeded(), sameSlotDecorations);
                    } else {
                        for (ArmorSkill armorSkill : decoration.getArmorSkills()) {
                            for (Decoration sameSlotDecoration : sameSlotDecorations) {
                                for (ArmorSkill sameSlotArmorSkill : sameSlotDecoration.getArmorSkills()) {
                                    if (armorSkill.isKind(sameSlotArmorSkill.kind)
                                        && wantedSkills.contains(armorSkill.kind)
                                        && armorSkill.points > sameSlotArmorSkill.points) {
                                        sameSlotDecorations.remove(sameSlotDecoration);
                                        sameSlotDecorations.add(decoration);
                                        slotToDecorationMap.put(decoration.getSlotsNeeded(), sameSlotDecorations);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Building Decoration data");
        long timeStamp = System.currentTimeMillis();
        decorationSkillTable = initDecorationSkillChart();
        timeStamp = System.currentTimeMillis() - timeStamp;
        System.out.println("Decoration data finished, time elapsed(ms): "+timeStamp);
    }

    /**
     * Find all the decoration that has the skillkind
     * @param skillKinds
     * @param slots
     * @return
     */
    public List<SkillChartWithDecoration> getSkillListBySlot(Set<String> skillKinds, int slots) {
        SkillChartDataList skillChartDataList = decorationSkillTable[slots];
        if (skillChartDataList != null) {
            // Only return the result with a decoration that has the wanted skill.
            return skillChartDataList.getSkillChartWithDecorations().stream().filter(skillChartWithDecoration -> {
                int decorationWithDesireSkill = 0;
                for (Decoration decoration : skillChartWithDecoration.decorations) {
                    for (ArmorSkill armorSkill : decoration.getArmorSkills()) {
                        if (skillKinds.contains(armorSkill.kind) && armorSkill.isPositive()) {
                            ++decorationWithDesireSkill;
                        }
                    }
                }
                return decorationWithDesireSkill == skillChartWithDecoration.decorations.size();
            }).collect(Collectors.toList());
        }

        return Collections.emptyList();
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
            return new EquipmentNode(copied, skillChartWithEquipment);
        }
        return new EquipmentNode(equipment, equipmentskillChart);
    }
}
