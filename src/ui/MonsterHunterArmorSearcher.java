package ui;

import armorsetsearch.ArmorSearchWrapper;
import armorsetsearch.skillactivation.SkillActivationRequirement;
import com.sun.javafx.binding.StringConstant;
import com.sun.tools.javac.code.Attribute;
import constants.Constants;
import interfaces.OnSearchResultProgress;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import constants.StringConstants;
import models.ClassType;
import models.Gender;
import models.GeneratedArmorSet;
import utils.WorkerThread;

public class MonsterHunterArmorSearcher extends JFrame {


    private static final int UI_UPDATE_THRESHOLD = 1000;

    private int uniqueSetSearchLimit = 200;
    private int decorationSearchLimit = 1;
    private Gender gender = Gender.MALE;
    private ClassType classType = ClassType.BLADEMASTER;

    private ArmorSearchWrapper armorSearchWrapper;

    private long lastUpdateUiTimeStamp = 0;
    private int currentProgress = 0;

    /**
     * Ui components
     */
    private JProgressBar progressBar = new JProgressBar(0, Constants.MAX_PROGRESS_BAR);
    private JButton addDesireSkillButton = new JButton(StringConstants.ADD_SKILL);
    private JButton removeDesireSkillButton = new JButton(StringConstants.REMOVE_SKILL);
    private JButton clearAllDesireSkills = new JButton(StringConstants.CLEAR_ALL_SKILL);
    private JButton search = new JButton(StringConstants.SEARCH_SKILL);
    private JButton stop = new JButton(StringConstants.STOP_SKILL_SEARCH);

    private JComboBox<Gender> genderJComboBox = new JComboBox<>(new Gender[]{Gender.MALE, Gender.FEMALE});
    private JComboBox<ClassType> classTypeJComboBox = new JComboBox<>(new ClassType[]{ClassType.BLADEMASTER, ClassType.GUNNER});
    private JComboBox<Integer> weaponSlotJComboBox = new JComboBox<>(IntStream.range(0, Constants.MAX_SLOTS + 1).boxed().toArray(Integer[]::new));
    private JComboBox<String> charmJComboBox = new JComboBox<>(new String[]{StringConstants.CHARM_ANY});

    private ArmorSkillPanel searchArmorSkillPanel;
    private ArmorSkillPanel desiredArmorSkillPanel;
    private SearchResultPanel searchResultPanel;
    private JLabel numberOfSetsFound = new JLabel(StringConstants.NUMBER_OF_SETS_FOUND+0);

    private WorkerThread workerThread;

    public void init() throws IOException {
        setSize(new Dimension(1400, 800));
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
        //// For testing purposes.
        //int[] ids = new int[]{83, 84, 145, 146, 149};
        //
        //for (int i : ids) {
        //    for (SkillActivationRequirement skillActivationRequirement : armorSearchWrapper.getPositiveSkillList()) {
        //        if (skillActivationRequirement.getId() == i) {
        //            List<SkillActivationRequirement> skillActivationRequirements = new ArrayList<>();
        //            skillActivationRequirements.add(skillActivationRequirement);
        //            desiredArmorSkillPanel.add(skillActivationRequirements);
        //        }
        //    }
        //}

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
                searchArmorSkillPanel.reset(armorSearchWrapper.getPositiveSkillList());
                desiredArmorSkillPanel.removeAll();
            }

        });

        genderJComboBox.addActionListener(e -> {
            Gender tempGender = (Gender) genderJComboBox.getSelectedItem();
            if (gender != tempGender) {
                gender = tempGender;
                armorSearchWrapper.setGender(gender);
                armorSearchWrapper.refreshSkillList();
                searchArmorSkillPanel.reset(armorSearchWrapper.getPositiveSkillList());
                desiredArmorSkillPanel.removeAll();
            }
        });

        stop.addActionListener(e -> {
            if (workerThread != null) {
                workerThread.interrupt();
            }
            armorSearchWrapper.stopSearching();
            setIdleState();
        });

        search.addActionListener(e -> {
            armorSearchWrapper.setWeapSlot((int)weaponSlotJComboBox.getSelectedItem());
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
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        searchResultPanel = new SearchResultPanel();
        container.add(numberOfSetsFound);
        container.add(searchResultPanel);
        return container;
    }

    private JPanel buildSearchSection(){
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createTitledBorder(StringConstants.SEARCH_HEADER));

        JPanel searchOption = new JPanel();
        searchOption.setLayout(new BoxLayout(searchOption, BoxLayout.Y_AXIS));

        JPanel searchRow1 = new JPanel();
        JPanel searchRow2 = new JPanel();

        searchOption.add(searchRow1);
        searchOption.add(searchRow2);

        searchRow1.add(new JLabel(StringConstants.CLASS));
        searchRow1.add(classTypeJComboBox);
        searchRow1.add(new JLabel(StringConstants.GENDER));
        searchRow1.add(genderJComboBox);
        searchRow2.add(new JLabel(StringConstants.WEP_SLOT));
        searchRow2.add(weaponSlotJComboBox);
        searchRow2.add(new JLabel(StringConstants.CHARM));
        searchRow2.add(charmJComboBox);

        container.add(searchOption);
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
    }

    private void setInSearchState() {
        currentProgress = 0;
        searchResultPanel.clear();
        search.setEnabled(false);
        stop.setEnabled(true);
        numberOfSetsFound.setText(StringConstants.NUMBER_OF_SETS_FOUND);
        progressBar.setStringPainted(true);
    }

    private class OnSearchResultProgressImpl implements OnSearchResultProgress {
        @Override
        public void onStart(int max) {
            progressBar.setMaximum(max);
        }

        @Override
        public void onProgress(GeneratedArmorSet generatedArmorSet) {
            SwingUtilities.invokeLater(() -> {
                searchResultPanel.update(generatedArmorSet);
                numberOfSetsFound.setText(StringConstants.NUMBER_OF_SETS_FOUND + searchResultPanel.getArmorSize());
            });
        }

        @Override
        public void onProgress(int current) {
            long time = System.currentTimeMillis();
            if (current > currentProgress && time - lastUpdateUiTimeStamp > UI_UPDATE_THRESHOLD) {
                currentProgress = current;

                System.out.println("Current Progress: " + current);
                lastUpdateUiTimeStamp = time;

                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(currentProgress);
                });
            }
        }

        @Override
        public void onComplete(List<GeneratedArmorSet> generatedArmorSets) {
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(Constants.MAX_PROGRESS_BAR);
                if (generatedArmorSets.size() >= uniqueSetSearchLimit) {
                    numberOfSetsFound.setText(StringConstants.TOO_MANY_SETS+generatedArmorSets.size());
                } else if (generatedArmorSets.isEmpty()) {
                    numberOfSetsFound.setText(StringConstants.NO_SETS_FOUND);
                } else {
                    numberOfSetsFound.setText(StringConstants.NUMBER_OF_SETS_FOUND+generatedArmorSets.size());
                }
            });
            System.out.println(generatedArmorSets.size());
            setIdleState();
        }
    }
}
