package armorsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Equipment;
import models.skillactivation.ActivatedSkill;

class EquipmentNode {
    List<Equipment> armorWithDesiredSkills;
    EquipmentNode next;

    public EquipmentNode(EquipmentNode next) {
        this.armorWithDesiredSkills = new ArrayList<>();
        this.next = next;
    }

    public void updateEquipmentListWithDesiredSkills(Map<String, List<Equipment>> cache, List<ActivatedSkill> desiredSkills) {
        for (ActivatedSkill activatedSkill : desiredSkills) {
            List<Equipment> equipments = cache.get(activatedSkill.getKind());
            if (equipments != null && !equipments.isEmpty()) {
                armorWithDesiredSkills.addAll(equipments);
            }
        }
    }
}
