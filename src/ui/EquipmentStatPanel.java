package ui;

import java.awt.FlowLayout;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import language.StringConstants;
import models.Decoration;
import models.GeneratedArmorSet;

public class EquipmentStatPanel extends JPanel{

    private JTextArea armorSkillTextArea = new JTextArea();
    private JTextArea decorationTextArea = new JTextArea();
    private JTextArea rarityTextArea = new JTextArea();
    private JTextArea resistanceTextArea = new JTextArea();
    private JTextArea miscTextArea = new JTextArea();

    private JTextArea[] allTextArea = {armorSkillTextArea, decorationTextArea, rarityTextArea, resistanceTextArea, miscTextArea};

    public EquipmentStatPanel(GeneratedArmorSet generatedArmorSet) {

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

        JPanel armorSkillSection = new JPanel();
        armorSkillSection.setBorder(BorderFactory.createTitledBorder(StringConstants.ARMOR_SKILL));
        armorSkillSection.add(armorSkillTextArea);

        JPanel decorationSection = new JPanel();
        decorationSection.setBorder(BorderFactory.createTitledBorder(StringConstants.DECORATIONS));
        decorationSection.add(decorationTextArea);

        JPanel raritySection = new JPanel();
        raritySection.setBorder(BorderFactory.createTitledBorder(StringConstants.RARITY));
        raritySection.add(rarityTextArea);

        JPanel resistanceSection = new JPanel();
        resistanceSection.setBorder(BorderFactory.createTitledBorder(StringConstants.RESISTANCE));
        resistanceSection.add(resistanceTextArea);

        JPanel miscSection = new JPanel();
        miscSection.setBorder(BorderFactory.createTitledBorder(StringConstants.MISC));
        miscSection.add(miscTextArea);

        for (JTextArea textArea : allTextArea) {
            textArea.setEditable(false);
        }

        container.add(armorSkillSection);
        container.add(decorationSection);
        container.add(raritySection);
        container.add(resistanceSection);
        container.add(miscSection);

        add(container);
        setData(generatedArmorSet);
    }

    public void setData(GeneratedArmorSet generatedArmorSet) {
        StringBuilder armorSkills = new StringBuilder();
        generatedArmorSet.getActivatedSkills().forEach(activatedSkill -> {
            armorSkills.append(activatedSkill.getAccumulatedPoints());
            armorSkills.append(" ");
            armorSkills.append(activatedSkill.getName());
            armorSkills.append(System.lineSeparator());
        });
        armorSkillTextArea.setText(armorSkills.toString());

        StringBuilder decorationsStringBuilder = new StringBuilder();
        generatedArmorSet.getEquipments().forEach(equipment -> {
            decorationsStringBuilder.append(equipment.getEquipmentType().name());
            decorationsStringBuilder.append(" ");
            for (Map.Entry<Decoration, Integer>  decorationSet : equipment.getDecorations().entrySet()){
                Decoration decoration = decorationSet.getKey();
                Integer count = decorationSet.getValue();
                decorationsStringBuilder.append(decoration.getName());
                decorationsStringBuilder.append(" ");
                decorationsStringBuilder.append("x");
                decorationsStringBuilder.append(" ");
                decorationsStringBuilder.append(count);
                decorationsStringBuilder.append(",");
            }
            decorationsStringBuilder.append(System.lineSeparator());
        });
        decorationTextArea.setText(decorationsStringBuilder.toString());

        StringBuilder rarityStringBuilder = new StringBuilder();
        generatedArmorSet.getEquipments().forEach(equipment -> {
            rarityStringBuilder.append(equipment.getEquipmentType().name());
            rarityStringBuilder.append(" ");
            rarityStringBuilder.append(equipment.getRarity());
            rarityStringBuilder.append(System.lineSeparator());
        });
        rarityTextArea.setText(rarityStringBuilder.toString());

        StringBuilder resistanceStringBuilder = new StringBuilder();
        generatedArmorSet.getTotalResistance().forEach(resistance -> {
            switch (resistance.getResistanceType()){
                case FIRE:
                    resistanceStringBuilder.append(StringConstants.RES_FIRE);
                    break;
                case WATER:
                    resistanceStringBuilder.append(StringConstants.RES_WATER);
                    break;
                case THUNDER:
                    resistanceStringBuilder.append(StringConstants.RES_Thunder);
                    break;
                case ICE:
                    resistanceStringBuilder.append(StringConstants.RES_ICE);
                    break;
                case DRAGON:
                    resistanceStringBuilder.append(StringConstants.RES_DRAGON);
                    break;
                default:
                    break;
            }
            resistanceStringBuilder.append(" ");
            resistanceStringBuilder.append(resistance.getValue());
            resistanceStringBuilder.append(System.lineSeparator());
        });
        resistanceTextArea.setText(resistanceStringBuilder.toString());

        StringBuilder miscStringBuilder = new StringBuilder();
        miscStringBuilder.append(StringConstants.DEFENSE);
        miscStringBuilder.append(": ");
        miscStringBuilder.append(generatedArmorSet.getTotalBaseDefense());
        miscStringBuilder.append(" - ");
        miscStringBuilder.append(generatedArmorSet.getTotalMaxDefense());
        miscTextArea.setText(miscStringBuilder.toString());
    }
}
