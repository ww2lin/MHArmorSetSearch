package armorsearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import models.Equipment;

public class tempEquipmentNode {
    public List<Equipment> armorWithDesiredSkills = new ArrayList<>();
    public tempEquipmentNode next;

    public tempEquipmentNode(List<Equipment> armorWithDesiredSkills, tempEquipmentNode next) {
        // Shuffle the array, thus when, to lower the chances of threads working on same sub-problems.
        this.armorWithDesiredSkills = new ArrayList<>(armorWithDesiredSkills);
        Collections.shuffle(armorWithDesiredSkills);

        this.next = next;
    }

    public int getTotalCombinations() {
        int total = armorWithDesiredSkills.size();
        tempEquipmentNode runner = next;
        while (runner != null) {
            total = total * runner.armorWithDesiredSkills.size();
            runner = runner.next;
        }
        return total;
    }
}
