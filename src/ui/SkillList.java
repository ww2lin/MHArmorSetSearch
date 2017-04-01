package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import models.skillactivation.SkillActivationRequirement;

public class SkillList extends JPanel{

    private static final int LIST_SIZE = 250;
    private JList<SkillActivationRequirement> skillActivationRequirementJList;
    private List<SkillActivationRequirement> modelList;

    public SkillList(List<SkillActivationRequirement> listData) {
        super();
        modelList = listData;
        skillActivationRequirementJList = new JList<>(new Vector<>(modelList));
        skillActivationRequirementJList.setCellRenderer(new SkillListRender());
        JScrollPane scrollPane = new JScrollPane(skillActivationRequirementJList);
        scrollPane.setPreferredSize(new Dimension(LIST_SIZE, LIST_SIZE));
        add(scrollPane);

    }

    private static class SkillListRender extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (renderer instanceof JLabel && value instanceof SkillActivationRequirement) {
                ((JLabel) renderer).setText(((SkillActivationRequirement) value).getName());
            }
            return renderer;
        }
    }

    public List<SkillActivationRequirement> getSelectedValues(){
        return skillActivationRequirementJList.getSelectedValuesList();
    }

    public void add(List<SkillActivationRequirement> skillActivationRequirements){
        skillActivationRequirements.forEach(skillActivationRequirement -> {
            if (!modelList.contains(skillActivationRequirement)){
                modelList.add(skillActivationRequirement);
            }
        });
        skillActivationRequirementJList.setListData(new Vector<>(modelList));
    }

    public void remove(){
        List<SkillActivationRequirement> filterOut = getSelectedValues();
        modelList = modelList.stream().filter(skillActivationRequirement -> !filterOut.contains(skillActivationRequirement)).collect(Collectors.toList());
        skillActivationRequirementJList.setListData(new Vector<>(modelList));
    }

    public void removeAll(){
        modelList.clear();
        skillActivationRequirementJList.setListData(new Vector<>(modelList));
    }

}
