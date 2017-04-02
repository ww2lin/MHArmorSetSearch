package ui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import language.StringConstants;
import models.Equipment;

public class EquipmentStatPanel extends JPanel{
    Equipment equipment;

    JLabel nameLabel;

    public EquipmentStatPanel(Equipment equipment) {
        this.equipment = equipment;

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        nameLabel = new JLabel();
        container.add(nameLabel);
        add(container);

        setData(equipment);
    }

    public void setData(Equipment equipment){
        String name = equipment.getName();

        String type = equipment.getEquipmentType().name();
        name = type +": "+name;

        if (equipment.isTorsoUp()) {
            name = name + " " + StringConstants.ANY_TORSO_UP_ARMOR;
        }

        if (equipment.isCanBeSubstitutedForAnyOtherEquipment()) {
            name = name + " " + StringConstants.ANY_ARMOR;
        }
        nameLabel.setText(name);
    }
}
