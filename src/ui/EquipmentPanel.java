package ui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import constants.StringConstants;
import models.Equipment;

public class EquipmentPanel extends JPanel{

    JLabel nameLabel;

    public EquipmentPanel(Equipment equipment) {

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        nameLabel = new JLabel();
        container.add(nameLabel);
        add(container);

        setData(equipment);
    }

    public void setData(Equipment equipment){
        String name = equipment.getName();

        StringBuilder stringBuilder = new StringBuilder(equipment.getEquipmentType().name());
        stringBuilder.append(": ").append(name);

        if (equipment.isTorsoUp()) {
            stringBuilder.append(" ").append(StringConstants.ANY_TORSO_UP_ARMOR);
        }

        if (equipment.isCanBeSubstitutedForAnyOtherThreeSlotEquipment()) {
            stringBuilder.append(" ").append(StringConstants.ANY_ARMOR);
        }
        nameLabel.setText(stringBuilder.toString());
    }
}
