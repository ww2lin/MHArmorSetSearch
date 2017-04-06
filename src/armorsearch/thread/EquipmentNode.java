package armorsearch.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Equipment;
import models.EquipmentType;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;

public class EquipmentNode {
    private List<Equipment> equipments = new ArrayList<>(5);
    private Map<String, Integer> skillTable;
    private List<ActivatedSkill> activatedSkills;
    private int skillMultiplier = 0;

    private EquipmentNode(List<Equipment> equipments, Map<String, Integer> skillTable, int skillMultiplier) {
        this.equipments = equipments;
        this.skillTable = skillTable;
        activatedSkills =  SkillActivationChart.getActivatedSkills(skillTable);
        this.skillMultiplier = skillMultiplier;
    }

    public EquipmentNode(Equipment equipment, Map<String, Integer> skillTable) {
        equipments.add(equipment);
        this.skillTable = skillTable;
        activatedSkills =  SkillActivationChart.getActivatedSkills(skillTable);
        skillMultiplier =  equipment.isTorsoUp() ? 1 : 0;
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

    public static EquipmentNode add(EquipmentNode node1, EquipmentNode curEquipmentNode, EquipmentType equipmentType){
        Map<String, Integer> armorSkill = curEquipmentNode.skillTable;
        if (equipmentType == EquipmentType.BODY) {
            // handle torso up
            armorSkill = SkillActivationChart.multiply(curEquipmentNode.skillTable, curEquipmentNode.skillMultiplier + 1);
        }
        Map<String, Integer> sumTable = SkillActivationChart.add(node1.skillTable, armorSkill);
        List<Equipment> equipments = new ArrayList<>(node1.equipments);
        equipments.addAll(curEquipmentNode.equipments);
        return new EquipmentNode(equipments, sumTable, node1.skillMultiplier + curEquipmentNode.skillMultiplier);
    }

}
