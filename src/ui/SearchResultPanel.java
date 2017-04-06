package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import models.Equipment;
import models.GeneratedArmorSet;

public class SearchResultPanel extends JPanel{

    private static final int LIST_SIZE = 400;
    private JList<GeneratedArmorSet> generatedArmorSetJList;
    private List<GeneratedArmorSet> modelList = new ArrayList<>();

    public SearchResultPanel() {
        super();
        generatedArmorSetJList = new JList<>(new Vector<>(modelList));
        generatedArmorSetJList.setCellRenderer(new ArmorResultRenderer());
        JScrollPane scrollPane = new JScrollPane(generatedArmorSetJList);
        scrollPane.setPreferredSize(new Dimension(LIST_SIZE, LIST_SIZE));
        add(scrollPane);

    }

    private static class ArmorResultRenderer extends JPanel implements ListCellRenderer<GeneratedArmorSet> {
        JSeparator separator;
        JPanel container = new JPanel();
        List<EquipmentPanel> equipmentPanels = new ArrayList<>(5);
        EquipmentStatPanel equipmentStatPanel;

        public ArmorResultRenderer() {
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            add(container);
            setBorder(new EmptyBorder(1, 1, 1, 1));
            separator = new JSeparator(JSeparator.HORIZONTAL);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends GeneratedArmorSet> list, GeneratedArmorSet generatedArmorSet, int index, boolean isSelected, boolean cellHasFocus) {
            List<Equipment> equipments = generatedArmorSet.getEquipments();
            for (int i = 0; i < equipments.size(); ++i) {
                Equipment equipment = equipments.get(i);
                if (i >= equipmentPanels.size()) {
                    EquipmentPanel equipmentPanel = new EquipmentPanel(equipment);
                    equipmentPanels.add(equipmentPanel);
                    container.add(equipmentPanel);
                } else {
                    EquipmentPanel equipmentPanel = equipmentPanels.get(i);
                    equipmentPanel.setData(equipment);
                }

            }
            if (equipmentStatPanel == null){
                equipmentStatPanel = new EquipmentStatPanel(generatedArmorSet);
            } else {
                equipmentStatPanel.setData(generatedArmorSet);
            }

            container.add(separator);
            container.add(equipmentStatPanel);
            container.add(separator);
            add(container);
            return this;
        }
    }

    public void update(List<GeneratedArmorSet> generatedArmorSets){
        modelList = generatedArmorSets;
    }

    public synchronized void update(GeneratedArmorSet generatedArmorSet){
        modelList.add(generatedArmorSet);
    }

    public synchronized void updateUi(){
        generatedArmorSetJList.setListData(new Vector<>(modelList));
    }

    public void clear(){
        modelList.clear();
        generatedArmorSetJList.setListData(new Vector<>(modelList));
    }

}
