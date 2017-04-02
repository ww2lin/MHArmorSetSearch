package armorsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Equipment;
import models.skillactivation.ActivatedSkill;

class EquipmentNode {
    List<Equipment> armorWithDesiredSkills = new ArrayList<>();
    EquipmentNode next;

    public EquipmentNode(List<Equipment> armorWithDesiredSkills, EquipmentNode next) {
        this.armorWithDesiredSkills = armorWithDesiredSkills;
        this.next = next;
    }

    public int getTotalCombinations() {
        int total = armorWithDesiredSkills.size();
        EquipmentNode runner = next;
        while (runner != null) {
            total = total * runner.armorWithDesiredSkills.size();
            runner = runner.next;
        }
        return total;
    }
}
