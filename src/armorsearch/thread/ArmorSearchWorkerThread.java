package armorsearch.thread;

import armorsearch.DecorationSearch;
import armorsearch.filter.ArmorSetFilter;
import armorsearch.memorization.MemorizationCache;
import interfaces.ArmorSearchWorkerProgress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Decoration;
import models.Equipment;
import models.EquipmentType;
import models.GeneratedArmorSet;
import models.UniquelyGeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;
import models.skillactivation.SkillUtil;

public class ArmorSearchWorkerThread extends Thread {

    private int id;
    private Map<EquipmentType, List<Equipment>> equipmentsToSearch;
    private ArmorSearchWorkerProgress armorSearchWorkerProgress;
    private List<ArmorSetFilter> armorSetFilters;

    private Map<EquipmentType, EquipmentSlots> decorationsForCurrentSet;
    private DecorationSearch decorationSearch;
    SkillActivationChart skillActivationChart;
    List<ActivatedSkill> desiredSkills;

    public ArmorSearchWorkerThread(int id,
                                   Map<EquipmentType, List<Equipment>> equipmentsToSearch,
                                   ArmorSearchWorkerProgress armorSearchWorkerProgress,
                                   Map<String, List<Decoration>> decorationLookupTable,
                                   List<ArmorSetFilter> armorSetFilters,
                                   int decorationSearchLimit,
                                   SkillActivationChart skillActivationChart,
                                   List<ActivatedSkill> desiredSkills) {
        this.id = id;
        this.equipmentsToSearch = equipmentsToSearch;
        this.armorSearchWorkerProgress = armorSearchWorkerProgress;
        this.armorSetFilters = armorSetFilters;


        decorationsForCurrentSet = new HashMap<>();
        for (EquipmentType equipmentType : EquipmentType.values()) {
            decorationsForCurrentSet.put(equipmentType, new EquipmentSlots(0));
        }

        decorationSearch = new DecorationSearch(decorationSearchLimit, desiredSkills, skillActivationChart, decorationLookupTable);
        this.skillActivationChart = skillActivationChart;
        this.desiredSkills = desiredSkills;
    }

    @Override
    public void run() {
        List<UniquelyGeneratedArmorSet> results = searchArmor();
        if (armorSearchWorkerProgress != null){
            armorSearchWorkerProgress.onCompleted(id, results);
        }
    }

    private List<UniquelyGeneratedArmorSet> searchArmor() {

        List<UniquelyGeneratedArmorSet> results = new ArrayList<>();
        int setsTried = 0;

        EquipmentType[] equipmentTypes = EquipmentType.values();
        int size = equipmentTypes.length;
        EquipmentList[] table = new EquipmentList[size];

        // Base case.
        EquipmentType currentType = equipmentTypes[0];
        List<Equipment> equipments = equipmentsToSearch.get(currentType);
        for (Equipment equipment : equipments) {
            List<EquipmentNode> equipmentNodes = decorationSearch.findArmorWithDecorationRecursively(equipment);
            table[0] = new EquipmentList(equipmentNodes);
        }

        // iterative case
        for (int i = 1; i < size; ++i){
            currentType = equipmentTypes[i];
            equipments = equipmentsToSearch.get(currentType);

            // construct all the table for the i element first.
            for (Equipment equipment : equipments) {
                List<EquipmentNode> equipmentNodes = decorationSearch.findArmorWithDecorationRecursively(equipment);
                table[i] = new EquipmentList(equipmentNodes);
            }

            // update the all the values for the current i from i-1
            // add it to sumEquipmentList - this is to avoid value getting updated after one iteration
            EquipmentList previousEquipmentList = table[i-1];
            EquipmentList currentEquipmentList = table[i];
            EquipmentList sumEquipmentList = new EquipmentList();

            for (EquipmentNode preEquipmentNode : previousEquipmentList.equipmentNodes) {
                for (EquipmentNode curEquipmentNode : currentEquipmentList.equipmentNodes) {
                    EquipmentNode sumNode = EquipmentNode.add(preEquipmentNode, curEquipmentNode);
                    sumEquipmentList.add(sumNode);

                    if (!armorSearchWorkerProgress.shouldContinueSearching()) {
                        return results;
                    }

                    // Check if this table satisfy the desire skills.
                    List<ActivatedSkill> activatedSkills = skillActivationChart.getActivatedSkills(sumNode.skillTable);
                    if (SkillUtil.containsDesiredSkills(desiredSkills, activatedSkills)) {
                        sumNode.setActivatedSkills(activatedSkills);

                        // ugly wrappers, need to clean this up.
                        GeneratedArmorSet generatedArmorSet = new GeneratedArmorSet(sumNode);
                        List<GeneratedArmorSet> generatedArmorSets = new ArrayList<>();
                        generatedArmorSets.add(generatedArmorSet);
                        UniquelyGeneratedArmorSet uniquelyGeneratedArmorSet = new UniquelyGeneratedArmorSet(generatedArmorSets);

                        results.add(uniquelyGeneratedArmorSet);

                        if (armorSearchWorkerProgress != null){
                            armorSearchWorkerProgress.onProgress(id, uniquelyGeneratedArmorSet, setsTried);
                        }
                    }
                    if (armorSearchWorkerProgress != null){
                        for (int k = 0; k < i; ++k) {
                            System.out.print(i + " " + table[k].size() + " --  ");
                        }
                        System.out.print(i + " " + sumEquipmentList.size() + " --  ");
                        armorSearchWorkerProgress.onProgress(id, null, setsTried);
                    }
                    ++setsTried;
                }
            }

            // place the sumNode back in i-th index
            table[i] = sumEquipmentList;
        }

        return results;
    }
}
