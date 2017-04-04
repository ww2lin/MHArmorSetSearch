package armorsearch.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Equipment;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;

public class EquipmentNode {
    private List<Equipment> equipments = new ArrayList<>(5);
    private Map<String, Integer> skillTable;
    private List<ActivatedSkill> activatedSkills;

    public EquipmentNode(List<Equipment> equipments, Map<String, Integer> skillTable) {
        this.equipments = equipments;
        this.skillTable = skillTable;
        activatedSkills =  SkillActivationChart.getActivatedSkills(skillTable);
    }

    public EquipmentNode(Equipment equipment, Map<String, Integer> skillTable) {
        equipments.add(equipment);
        this.skillTable = skillTable;
        activatedSkills =  SkillActivationChart.getActivatedSkills(skillTable);
    }

    public List<ActivatedSkill> getActivatedSkills() {
        return activatedSkills;
    }

    public List<Equipment> getEquipments() {
        return equipments;
    }

    public void setActivatedSkills(List<ActivatedSkill> activatedSkills) {
        this.activatedSkills = activatedSkills;
    }

    public static EquipmentNode add(EquipmentNode node1, EquipmentNode node2){
        Map<String, Integer> sumTable = SkillActivationChart.add(node1.skillTable, node2.skillTable);
        List<Equipment> equipments = new ArrayList<>(node1.equipments);
        equipments.addAll(node2.equipments);
        return new EquipmentNode(equipments, sumTable);
    }

    public static Equipment addDecoration(Equipment template, EquipmentNode node1, EquipmentNode node2){

        if (node1.equipments.size() != 1 || node2.equipments.size() != 1) {
            System.err.println("trying to add two equipments > 0");
            return null;
        }

        Equipment Equipment1 = node1.equipments.get(0);
        Equipment Equipment2 = node2.equipments.get(0);
        if (!Equipment1.equals(Equipment2)) {
            System.err.println("try to add equipment with different id");
            return null;
        }

        Equipment copied = new Equipment(template);
        copied.addAllDecorations(Equipment2.getDecorations());
        copied.addAllDecorations(Equipment1.getDecorations());
        return copied;
    }
}
