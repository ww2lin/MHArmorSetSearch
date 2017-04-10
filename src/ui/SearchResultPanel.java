package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import models.Equipment;
import models.GeneratedArmorSet;

public class SearchResultPanel extends JPanel{

    private JList<GeneratedArmorSet> generatedArmorSetJList;
    private DefaultListModel<GeneratedArmorSet> modelList = new DefaultListModel<>();

    public SearchResultPanel() {
        super();
        generatedArmorSetJList = new JList<>(modelList);
        generatedArmorSetJList.setCellRenderer(new ArmorResultRenderer());
        JScrollPane scrollPane = new JScrollPane(generatedArmorSetJList);
        scrollPane.setPreferredSize(new Dimension(700, 600));
        add(scrollPane);

    }

    private static class ArmorResultRenderer extends JPanel implements ListCellRenderer<GeneratedArmorSet> {
        EquipmentsPanel equipmentsPanel = new EquipmentsPanel();
        DecorationPanel decorationPanel = new DecorationPanel();
        EquipmentStatPanel equipmentStatPanel = new EquipmentStatPanel();
        CharmPanel charmPanel = new CharmPanel();

        public ArmorResultRenderer() {
            setBorder(new EmptyBorder(1, 1, 1, 1));

            JPanel container = new JPanel();
            JPanel equipmentAndDecorationContainer = new JPanel();

            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            equipmentAndDecorationContainer.setLayout(new BoxLayout(equipmentAndDecorationContainer, BoxLayout.X_AXIS));

            equipmentAndDecorationContainer.add(equipmentsPanel);
            equipmentAndDecorationContainer.add(decorationPanel);
            equipmentAndDecorationContainer.add(charmPanel);

            container.add(new JSeparator(JSeparator.HORIZONTAL));
            container.add(equipmentAndDecorationContainer);
            container.add(new JSeparator(JSeparator.HORIZONTAL));
            container.add(equipmentStatPanel);
            container.add(new JSeparator(JSeparator.HORIZONTAL));
            add(container);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends GeneratedArmorSet> list, GeneratedArmorSet generatedArmorSet, int index, boolean isSelected, boolean cellHasFocus) {
            List<Equipment> equipments = generatedArmorSet.getEquipments();
            equipmentsPanel.setData(equipments);
            equipmentStatPanel.setData(generatedArmorSet);
            decorationPanel.setData(generatedArmorSet);
            charmPanel.setData(generatedArmorSet.getGeneratedCharm());
            return this;
        }
    }

    public void update(List<GeneratedArmorSet> generatedArmorSets){
        generatedArmorSets.forEach(generatedArmorSet -> {
            modelList.addElement(generatedArmorSet);
        });
    }

    public synchronized void update(GeneratedArmorSet generatedArmorSet){
        modelList.addElement(generatedArmorSet);
    }


    public void clear(){
        modelList.clear();
    }

    public int getArmorSize(){
        return modelList.size();
    }
}
