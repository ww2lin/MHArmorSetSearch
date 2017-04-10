package ui;

import constants.StringConstants;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import models.Decoration;
import models.Equipment;
import models.EquipmentType;
import models.GeneratedArmorSet;

public class DecorationPanel extends JPanel{

    private JPanel container = new JPanel();
    private JTextArea decorationTextArea = new JTextArea();

    public DecorationPanel() {
        JPanel decorationSection = new JPanel();
        decorationSection.setBorder(BorderFactory.createTitledBorder(StringConstants.DECORATIONS));

        decorationSection.add(decorationTextArea);

        container.add(decorationSection);
        add(container);
    }

    public void setData(GeneratedArmorSet generatedArmorSet){
        StringBuilder decorationsStringBuilder = new StringBuilder();
        generatedArmorSet.getEquipments().forEach(equipment -> {
            if (!equipment.getDecorations().isEmpty()) {
                decorationsStringBuilder.append(equipment.getEquipmentType().name());
                decorationsStringBuilder.append(" ");
                for (Map.Entry<Decoration, Integer> decorationSet : equipment.getDecorations().entrySet()) {
                    Decoration decoration = decorationSet.getKey();
                    Integer count = decorationSet.getValue();
                    decorationsStringBuilder.append(decoration.getName());
                    decorationsStringBuilder.append(" ").append("x").append(" ").append(count).append(",");
                }
                decorationsStringBuilder.append(System.lineSeparator());
            }
        });
        decorationTextArea.setText(decorationsStringBuilder.toString());
    }
}
