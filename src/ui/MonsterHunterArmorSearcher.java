package ui;

import armorsearch.ArmorSearchWrapper;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import language.StringConstants;
import models.ClassType;
import models.Gender;
import models.skillactivation.SkillActivationRequirement;

public class MonsterHunterArmorSearcher extends JFrame {

    private int uniqueSetSearchLimit = 200;
    private int decorationSearchLimit = 5;
    private Gender gender = Gender.MALE;
    private ClassType classType = ClassType.BLADEMASTER;

    private ArmorSearchWrapper armorSearchWrapper;
    private List<SkillActivationRequirement> desireSkills = new ArrayList<>();

    /**
     * Ui components
     */
    private JButton addDesireSkillButton = new JButton(StringConstants.ADD_SKILL);
    private JButton removeDesireSkillButton = new JButton(StringConstants.REMOVE_SKILL);
    private JButton clearAllDesireSkills = new JButton(StringConstants.CLEAR_ALL_SKILL);
    private JButton search = new JButton(StringConstants.SEARCH_SKILL);

    private SkillList searchSkillList;
    private SkillList desiredSkillList;

    public void init() throws IOException {
        setSize(new Dimension(760, 600));
        setTitle(StringConstants.TITLE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        gender = Gender.MALE;
        classType = ClassType.BLADEMASTER;
        armorSearchWrapper = new ArmorSearchWrapper(classType, gender, Collections.emptyList());
        // Main container
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        // skill section
        JPanel skillContainer = new JPanel(new FlowLayout());
        skillContainer.add(renderSkillSection());
        container.add(skillContainer);



        add(container);
        pack();
        setVisible(true);
        setupListeners();

    }

    private void setupListeners() {
        addDesireSkillButton.addActionListener(e -> {
            desiredSkillList.add(searchSkillList.getSelectedValues());
        });

        removeDesireSkillButton.addActionListener(e -> {
            desiredSkillList.remove();
        });

        clearAllDesireSkills.addActionListener(e -> {
            desiredSkillList.removeAll();
        });

        search.addActionListener(e -> {

        });
    }


    private JPanel renderSkillSection(){
        // Set up layout
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        //JPanel middlePanel = new JPanel();
        //middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        container.add(leftPanel);
        //container.add(middlePanel);
        container.add(rightPanel);

        JPanel leftPanelBottomHorizontalMenu = new JPanel();
        leftPanelBottomHorizontalMenu.setLayout(new BoxLayout(leftPanelBottomHorizontalMenu, BoxLayout.X_AXIS));

        JPanel rightPanelBottomHorizontalMenu = new JPanel();
        rightPanelBottomHorizontalMenu.setLayout(new BoxLayout(rightPanelBottomHorizontalMenu, BoxLayout.X_AXIS));

        JLabel allSkills = new JLabel(StringConstants.ALL_SKILL_BY_CLASS_GENDER);
        JLabel skillToSearch = new JLabel(StringConstants.SKILL_TO_SEARCH);

        // adding components.
        searchSkillList = new SkillList(armorSearchWrapper.getSkillList());
        leftPanel.add(allSkills);
        leftPanel.add(searchSkillList);
        leftPanel.add(leftPanelBottomHorizontalMenu);
        leftPanelBottomHorizontalMenu.add(addDesireSkillButton);
        leftPanelBottomHorizontalMenu.add(search);

        desiredSkillList = new SkillList(desireSkills);
        rightPanel.add(skillToSearch);
        rightPanel.add(desiredSkillList);
        rightPanel.add(rightPanelBottomHorizontalMenu);
        rightPanelBottomHorizontalMenu.add(removeDesireSkillButton);
        rightPanelBottomHorizontalMenu.add(clearAllDesireSkills);

        return container;
    }


}
