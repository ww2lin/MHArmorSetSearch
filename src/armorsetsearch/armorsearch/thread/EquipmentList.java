package armorsetsearch.armorsearch.thread;

import java.util.ArrayList;
import java.util.List;

public class EquipmentList {

    private List<EquipmentNode> equipmentNodes = new ArrayList<>();

    public EquipmentList(List<EquipmentNode> equipmentNodes) {
        this.equipmentNodes = equipmentNodes;
    }

    public EquipmentList(EquipmentNode equipmentNode) {
        equipmentNodes.add(equipmentNode);
    }

    public EquipmentList() {
    }

    public void add(EquipmentNode equipmentNode) {
        equipmentNodes.add(equipmentNode);
    }

    public void add(EquipmentList equipmentList) {
        equipmentNodes.addAll(equipmentList.equipmentNodes);
    }

    public int size(){
        return equipmentNodes.size();
    }

    public List<EquipmentNode> getEquipmentNodes() {
        return equipmentNodes;
    }
}
