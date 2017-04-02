package armorsearch;

import java.util.List;
import models.Equipment;

public class AllEquipments {

    final List<Equipment> headEquipments;
    final List<Equipment> bodyEquipments;
    final List<Equipment> armEquipments;
    final List<Equipment> wstEquipments;
    final List<Equipment> legEquipments;

    public AllEquipments(List<Equipment> headEquipments, List<Equipment> bodyEquipments, List<Equipment> armEquipments, List<Equipment> wstEquipments, List<Equipment> legEquipments) {
        this.headEquipments = headEquipments;
        this.bodyEquipments = bodyEquipments;
        this.armEquipments = armEquipments;
        this.wstEquipments = wstEquipments;
        this.legEquipments = legEquipments;
    }

    public List<Equipment> getHeadEquipments() {
        return headEquipments;
    }

    public List<Equipment> getBodyEquipments() {
        return bodyEquipments;
    }

    public List<Equipment> getArmEquipments() {
        return armEquipments;
    }

    public List<Equipment> getWstEquipments() {
        return wstEquipments;
    }

    public List<Equipment> getLegEquipments() {
        return legEquipments;
    }
}
