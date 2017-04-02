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
import models.UniquelyGeneratedArmorSet;

public class SearchResultList extends JPanel{

    private static final int LIST_SIZE = 400;
    private JList<UniquelyGeneratedArmorSet> uniquelyGeneratedArmorSetJList;
    private List<UniquelyGeneratedArmorSet> modelList = new ArrayList<>();

    public SearchResultList() {
        super();
        uniquelyGeneratedArmorSetJList = new JList<>(new Vector<>(modelList));
        uniquelyGeneratedArmorSetJList.setCellRenderer(new ArmorResultRenderer());
        JScrollPane scrollPane = new JScrollPane(uniquelyGeneratedArmorSetJList);
        scrollPane.setPreferredSize(new Dimension(LIST_SIZE, LIST_SIZE));
        add(scrollPane);

    }

    private static class ArmorResultRenderer extends JPanel implements ListCellRenderer<UniquelyGeneratedArmorSet> {
        JSeparator separator;
        JPanel container = new JPanel();
        List<EquipmentPanel> equipmentPanels = new ArrayList<>(5);

        public ArmorResultRenderer() {
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            add(container);
            setBorder(new EmptyBorder(1, 1, 1, 1));
            separator = new JSeparator(JSeparator.HORIZONTAL);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends UniquelyGeneratedArmorSet> list, UniquelyGeneratedArmorSet entry, int index, boolean isSelected, boolean cellHasFocus) {

            List<GeneratedArmorSet> generatedArmorSets = entry.getGeneratedArmorSets();
            GeneratedArmorSet generatedArmorSet = generatedArmorSets.get(0);

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
            container.add(separator);
            add(container);
            return this;
        }
    }

    public void update(List<UniquelyGeneratedArmorSet> uniquelyGeneratedArmorSets){
        modelList = uniquelyGeneratedArmorSets;
        uniquelyGeneratedArmorSetJList.setListData(new Vector<>(modelList));
    }

    public void update(UniquelyGeneratedArmorSet uniquelyGeneratedArmorSets){
        modelList.add(uniquelyGeneratedArmorSets);
        uniquelyGeneratedArmorSetJList.setListData(new Vector<>(modelList));
    }

    public void clear(){
        modelList.clear();
        uniquelyGeneratedArmorSetJList.setListData(new Vector<>(modelList));
    }

}
