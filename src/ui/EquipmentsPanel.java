package ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import models.Equipment;
import models.EquipmentType;

public class EquipmentsPanel extends JPanel{

    private List<EquipmentPanel> equipmentPanels = new ArrayList<>(EquipmentType.values().length);
    private JPanel container = new JPanel();

    public EquipmentsPanel() {
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        for (int i = 0; i < EquipmentType.values().length; ++i) {
            EquipmentPanel equipmentPanel = new EquipmentPanel();
            equipmentPanels.add(equipmentPanel);
            container.add(equipmentPanel);
        }
        add(container);
    }

    public void setData(List<Equipment> equipments){
        for (int i = 0; i < EquipmentType.values().length; ++i) {
            if (i < equipments.size()) {
                Equipment equipment = equipments.get(i);
                equipmentPanels.get(i).setData(equipment);
            } else {
                equipmentPanels.get(i).resetData();
            }
        }
    }
}
