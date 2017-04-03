package armorsearch;

import armorsearch.thread.EquipmentNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Decoration;
import models.Equipment;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;

public class DecorationSearch {

    List<ActivatedSkill> desiredSkills;
    List<Decoration> decorationsToTry = new ArrayList<>();

    public DecorationSearch(int decorationSearchLimit, List<ActivatedSkill> desiredSkills, SkillActivationChart skillActivationChart, Map<String, List<Decoration>> decorationLookupTable) {
        this.desiredSkills = desiredSkills;

        for (ActivatedSkill activatedSkill : desiredSkills) {
            List<Decoration> decorations = decorationLookupTable.get(activatedSkill.getKind());
            for (Decoration decoration : decorations) {
                if (!decoration.isAvailable() || !decoration.isPositive(activatedSkill.getKind())) {
                    // skip negative or not available jewels.
                    continue;
                }
                decorationsToTry.add(decoration);
            }
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
}
