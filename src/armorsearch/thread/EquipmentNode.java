package armorsearch.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Equipment;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;

public class EquipmentNode {
    List<Equipment> equipments;
    Map<String, Integer> skillTable;
    List<ActivatedSkill> activatedSkills;

    public EquipmentNode(List<Equipment> equipments, Map<String, Integer> skillTable) {
        this.equipments = equipments;
        this.skillTable = skillTable;
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
}
