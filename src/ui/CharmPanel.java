package ui;

import constants.StringConstants;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import models.Decoration;
import models.GeneratedCharm;

public class CharmPanel extends JPanel {

    private JPanel container = new JPanel();
    private JTextArea charmTextArea = new JTextArea();

    public CharmPanel() {
        JPanel charmSection = new JPanel();
        charmSection.setBorder(BorderFactory.createTitledBorder(StringConstants.CHARM));

        charmSection.add(charmTextArea);

        container.add(charmSection);
        add(container);
    }

    public void setData(GeneratedCharm generatedCharm) {
        if (generatedCharm != null) {
            StringBuilder charmStringBuilder = new StringBuilder();
            charmStringBuilder.append(StringConstants.CHARM_TYPE).append(generatedCharm.getCharmType());
            charmStringBuilder.append(System.lineSeparator());
            charmStringBuilder.append(StringConstants.Charm_Slots).append(generatedCharm.getSlotUsed());
            charmStringBuilder.append(System.lineSeparator());
            generatedCharm.getCharmSkills().forEach(charmSkill -> {
                charmStringBuilder.append(charmSkill.getSkillKind()).append(" ").append(charmSkill.getSkillPoints());
                charmStringBuilder.append(System.lineSeparator());
            });

            if (generatedCharm.getSlotUsed() > 0) {
                charmStringBuilder.append(System.lineSeparator());
                charmStringBuilder.append(StringConstants.DECORATIONS).append(System.lineSeparator());
                for (Map.Entry<Decoration, Integer> decorationSet : generatedCharm.getDecorations().entrySet()) {
                    Decoration decoration = decorationSet.getKey();
                    Integer count = decorationSet.getValue();
                    charmStringBuilder.append(decoration.getName());
                    charmStringBuilder.append(" ").append("x").append(" ").append(count).append(",");
                    charmStringBuilder.append(System.lineSeparator());
                }
            }
            charmTextArea.setText(charmStringBuilder.toString());
        }
        setVisible(generatedCharm != null);
    }
}
