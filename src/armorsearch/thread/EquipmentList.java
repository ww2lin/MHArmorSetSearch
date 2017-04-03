package armorsearch.thread;

import java.util.ArrayList;
import java.util.List;

public class EquipmentList {

    List<EquipmentNode> equipmentNodes = new ArrayList<>();

    public EquipmentList(List<EquipmentNode> equipmentNodes) {
        this.equipmentNodes = equipmentNodes;
    }

    public EquipmentList() {
    }

    public void add(EquipmentNode equipmentNode) {
        equipmentNodes.add(equipmentNode);
    }

    public int size(){
        return equipmentNodes.size();
    }
}
