package ui;

import armorsearch.ArmorSearchWrapper;
import interfaces.OnSearchResultProgress;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import language.StringConstants;
import models.ClassType;
import models.Gender;
import models.UniquelyGeneratedArmorSet;
import utils.WorkerThread;

public class MonsterHunterArmorSearcher extends JFrame {

    private int uniqueSetSearchLimit = 200;
    private int decorationSearchLimit = 1;
    private Gender gender = Gender.MALE;
    private ClassType classType = ClassType.BLADEMASTER;

    private ArmorSearchWrapper armorSearchWrapper;

    /**
     * Ui components
     */
    private JProgressBar progressBar = new JProgressBar(0, uniqueSetSearchLimit);
    private JButton addDesireSkillButton = new JButton(StringConstants.ADD_SKILL);
    private JButton removeDesireSkillButton = new JButton(StringConstants.REMOVE_SKILL);
    private JButton clearAllDesireSkills = new JButton(StringConstants.CLEAR_ALL_SKILL);
    private JButton search = new JButton(StringConstants.SEARCH_SKILL);
    private JButton stop = new JButton(StringConstants.STOP_SKILL_SEARCH);

    private JComboBox<Gender> genderJComboBox = new JComboBox<>(new Gender[]{Gender.MALE, Gender.FEMALE});
    private JComboBox<ClassType> classTypeJComboBox = new JComboBox<>(new ClassType[]{ClassType.BLADEMASTER, ClassType.GUNNER});

    private ArmorSkillPanel searchArmorSkillPanel;
    private ArmorSkillPanel desiredArmorSkillPanel;
    private SearchResultPanel searchResultPanel;

    private WorkerThread workerThread;

    public void init() throws IOException {
        setSize(new Dimension(800, 800));
        setTitle(StringConstants.TITLE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gender = Gender.MALE;
        classType = ClassType.BLADEMASTER;
        armorSearchWrapper = new ArmorSearchWrapper(classType, gender, Collections.emptyList());

        // Main container
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        // search section
        container.add(buildSearchSection());

        // skill section
        container.add(buildSkillSection());

        // progress bar
        container.add(progressBar);

        // search Result
        add(buildArmorSearchResultSection(), BorderLayout.CENTER);

        add(container, BorderLayout.WEST);


        pack();

        setIdleState();

        setupListeners();

        setVisible(true);

    }

    private void setupListeners() {
        addDesireSkillButton.addActionListener(e -> {
            desiredArmorSkillPanel.add(searchArmorSkillPanel.getSelectedValues());
        });

        removeDesireSkillButton.addActionListener(e -> {
            desiredArmorSkillPanel.remove();
        });

        clearAllDesireSkills.addActionListener(e -> {
            desiredArmorSkillPanel.removeAll();
        });

        classTypeJComboBox.addActionListener(e -> {
            ClassType tempClassType = (ClassType) classTypeJComboBox.getSelectedItem();
            if (classType != tempClassType) {
                classType = tempClassType;
                armorSearchWrapper.setClassType(classType);
                armorSearchWrapper.refreshSkillList();
                searchArmorSkillPanel.reset(armorSearchWrapper.getSkillList());
                desiredArmorSkillPanel.removeAll();
            }

        });

        genderJComboBox.addActionListener(e -> {
            Gender tempGender = (Gender) genderJComboBox.getSelectedItem();
            if (gender != tempGender) {
                gender = tempGender;
                armorSearchWrapper.setGender(gender);
                armorSearchWrapper.refreshSkillList();
                searchArmorSkillPanel.reset(armorSearchWrapper.getSkillList());
                desiredArmorSkillPanel.removeAll();
            }
        });

        stop.addActionListener(e -> {
            armorSearchWrapper.stopSearching();
            setIdleState();
        });

        search.addActionListener(e -> {
            //ActivatedSkill[] lookupSkills1 = {new ActivatedSkill("攻撃力UP【小】", "攻撃", 10)};
            //ActivatedSkill[] lookupSkills2 = {new ActivatedSkill("攻撃力UP【小】", "攻撃", 10), new ActivatedSkill("ガード性能+1","ガード性能", 10)};

            workerThread = new WorkerThread(new OnSearchResultProgressImpl(),
                                            armorSearchWrapper,
                                            desiredArmorSkillPanel.getAll(),
                                            uniqueSetSearchLimit,
                                            decorationSearchLimit,
                                            Collections.emptyList());
            setInSearchState();
            workerThread.start();
        });

    }

    private JPanel buildArmorSearchResultSection(){
        JPanel container = new JPanel();
        searchResultPanel = new SearchResultPanel();
        container.add(searchResultPanel);
        return container;
    }

    private JPanel buildSearchSection(){
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createTitledBorder(StringConstants.SEARCH_HEADER));

        container.add(classTypeJComboBox);
        container.add(genderJComboBox);
        container.add(stop);
        container.add(search);

        return container;
    }

    private JPanel buildSkillSection(){
        // Set up layout
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createTitledBorder(StringConstants.SKILL_HEADER));
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel rightPanel = new JPanel(new BorderLayout());

        container.add(leftPanel);
        container.add(rightPanel);

        JPanel rightPanelBottomHorizontalMenu = new JPanel();
        rightPanelBottomHorizontalMenu.setLayout(new BoxLayout(rightPanelBottomHorizontalMenu, BoxLayout.X_AXIS));

        JLabel allSkills = new JLabel(StringConstants.ALL_SKILL_BY_CLASS_GENDER);
        JLabel skillToSearch = new JLabel(StringConstants.SKILL_TO_SEARCH);

        // adding components.
        searchArmorSkillPanel = new ArmorSkillPanel(armorSearchWrapper.getPositiveSkillList());
        leftPanel.add(allSkills, BorderLayout.NORTH);
        leftPanel.add(searchArmorSkillPanel, BorderLayout.CENTER);
        leftPanel.add(addDesireSkillButton, BorderLayout.SOUTH);

        desiredArmorSkillPanel = new ArmorSkillPanel(new ArrayList<>());
        rightPanel.add(skillToSearch, BorderLayout.NORTH);
        rightPanel.add(desiredArmorSkillPanel, BorderLayout.CENTER);
        rightPanel.add(rightPanelBottomHorizontalMenu, BorderLayout.SOUTH);
        rightPanelBottomHorizontalMenu.add(removeDesireSkillButton);
        rightPanelBottomHorizontalMenu.add(clearAllDesireSkills);

        return container;
    }

    private void setIdleState() {
        search.setEnabled(true);
        stop.setEnabled(false);
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
    }

    private void setInSearchState() {
        searchResultPanel.clear();
        search.setEnabled(false);
        stop.setEnabled(true);
        progressBar.setStringPainted(true);
    }

    private class OnSearchResultProgressImpl implements OnSearchResultProgress {
        @Override
        public void onProgress(UniquelyGeneratedArmorSet uniquelyGeneratedArmorSet, int current, int max) {
            //searchResultPanel.update(uniquelyGeneratedArmorSet);
            progressBar.setValue(current);
            System.out.println(current+"  "+max);
        }

        @Override
        public void onComplete(List<UniquelyGeneratedArmorSet> uniquelyGeneratedArmorSets) {
            setIdleState();
            searchResultPanel.update(uniquelyGeneratedArmorSets);
            System.out.println(uniquelyGeneratedArmorSets.size());
        }
    }
}
