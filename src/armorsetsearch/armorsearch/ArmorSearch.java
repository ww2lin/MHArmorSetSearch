package armorsetsearch.armorsearch;

import armorsetsearch.ArmorSkillCacheTable;
import armorsetsearch.charmsearch.CharmSearch;
import armorsetsearch.decorationsearch.DecorationSearch;
import armorsetsearch.filter.ArmorSetFilter;
import armorsetsearch.armorsearch.thread.ArmorSearchWorkerThread;
import armorsetsearch.armorsearch.thread.EquipmentList;
import armorsetsearch.armorsearch.thread.EquipmentNode;
import armorsetsearch.skillactivation.SkillActivationChart;
import constants.Constants;
import interfaces.OnSearchResultProgress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import models.Equipment;
import models.EquipmentType;
import models.GeneratedArmorSet;
import armorsetsearch.skillactivation.ActivatedSkill;
import utils.StopWatch;

import static constants.Constants.GENERATED_EQUIPMENT_ID;
import static constants.Constants.THREAD_COUNT;

public class ArmorSearch {

    private ArmorSkillCacheTable armorSkillCacheTable;
    private final int uniqueSetSearchLimit;
    private OnSearchResultProgress onSearchResultProgress;

    // use to stop the threads.
    private boolean stop = false;
    private ArmorSearchWorkerThread[] workerThreads;
    private int weapSlots;

    public ArmorSearch(int weapSlots, ArmorSkillCacheTable armorSkillCacheTable, int uniqueSetSearchLimit, OnSearchResultProgress onSearchResultProgress) {
        this.weapSlots = weapSlots;
        this.armorSkillCacheTable = armorSkillCacheTable;
        this.uniqueSetSearchLimit = uniqueSetSearchLimit;
        this.onSearchResultProgress = onSearchResultProgress;
    }

    /**
     * run a dfs search for the skill search
     * @param desiredSkills that the user wants to generate
     * @return list of equipment that matches what the user wants
     */
    public EquipmentList findArmorSetWith(List<ActivatedSkill> desiredSkills) {
        Map<EquipmentType, List<Equipment>> equipments = armorSkillCacheTable.getEquipmentCache(desiredSkills);
        return searchArmor(desiredSkills, equipments);
    }

    /**
     * DP implementation of finding if a possible armor set exists.
     * @return
     */
    private EquipmentList searchArmor(List<ActivatedSkill> desiredSkills, Map<EquipmentType, List<Equipment>> equipmentsToSearch) {
        // Do the body last since we need to know the previous skill point need to adjust for torso ups.
        EquipmentType[] equipmentTypes;

        // Check if we have slot for wep.
        if (weapSlots == 0) {
            equipmentTypes = new EquipmentType[]{EquipmentType.HEAD, EquipmentType.ARM, EquipmentType.WST, EquipmentType.LEG, EquipmentType.BODY};
        } else {
            equipmentTypes = new EquipmentType[]{EquipmentType.HEAD, EquipmentType.ARM, EquipmentType.WST, EquipmentType.LEG, EquipmentType.BODY, EquipmentType.WEP};
            // Smuggle in a weap with slots.
            Equipment wep = Equipment.Builder()
                .setId(GENERATED_EQUIPMENT_ID)
                .setName("")
                .setEquipmentType(EquipmentType.WEP)
                .setSlots(weapSlots);
            equipmentsToSearch.put(EquipmentType.WEP, Collections.singletonList(wep));
        }

        // Offset one for searching for charms.
        int progressChuck = Constants.MAX_PROGRESS_BAR / (equipmentTypes.length + 1);
        int progressBar = progressChuck;

        List<GeneratedArmorSet> results = new ArrayList<>();
        int size = equipmentTypes.length;
        EquipmentList[] table = new EquipmentList[size];

        // Base case.
        EquipmentType currentType = equipmentTypes[0];
        List<Equipment> equipments = equipmentsToSearch.get(currentType);
        EquipmentList currentEquipmentList = new EquipmentList();
        for (Equipment equipment : equipments) {
            currentEquipmentList.add(new EquipmentNode(equipment, SkillActivationChart.getActivatedSkillChart(equipment)));
        }
        table[0] = currentEquipmentList;
        System.out.println("0   "+table[0].size());

        // iterative case
        for (int i = 1; i < size; ++i){
            currentType = equipmentTypes[i];
            equipments = equipmentsToSearch.get(currentType);

            currentEquipmentList = new EquipmentList();
            // construct all the table for the i element first.
            for (Equipment equipment : equipments) {
                currentEquipmentList.add(new EquipmentNode(equipment, SkillActivationChart.getActivatedSkillChart(equipment)));
            }

            // update the all the values for the current i from i-1
            // add it to sumEquipmentList - this is to avoid value getting updated after one iteration
            EquipmentList previousEquipmentList = table[i-1];

            final float maxPossiblePercentage = (float)progressChuck / (previousEquipmentList.size() * currentEquipmentList.size());

            // divide up the list into multiple parts and use multiple threads to do the calculation
            EquipmentList[] dataSet = new EquipmentList[THREAD_COUNT];
            workerThreads = new ArmorSearchWorkerThread[THREAD_COUNT];
            for (int j = 0; j < currentEquipmentList.size(); ++j){
                int index = j % THREAD_COUNT;
                if (dataSet[index] == null){
                    dataSet[index] = new EquipmentList();
                }
                dataSet[index].add(currentEquipmentList.getEquipmentNodes().get(j));
            }

            // to store the results.
            EquipmentList updatedEquipmentSkillList = new EquipmentList();
            AtomicInteger setsFound = new AtomicInteger(results.size());
            for (int j = 0; j < THREAD_COUNT; ++j){
                workerThreads[j] = new ArmorSearchWorkerThread(j,
                                                               setsFound,
                                                               progressBar,
                                                               maxPossiblePercentage,
                                                               onSearchResultProgress,
                                                               uniqueSetSearchLimit,
                                                               equipmentTypes[i],
                                                               previousEquipmentList,
                                                               dataSet[j],
                                                               desiredSkills,
                                                               updatedEquipmentSkillList,
                                                               results);
            }

            for (int  j = 0; j < THREAD_COUNT; ++j) {
                workerThreads[j].start();
            }

            for (int  j = 0; j < THREAD_COUNT; ++j) {
                try {
                    workerThreads[j].join();
                } catch (InterruptedException e) {
                }
            }

            progressBar+=progressChuck;

            // place the sumNode back in i-th index
            table[i] = updatedEquipmentSkillList;

            // free up the memory that we dont need anymore.
            table[i-1] = null;

            System.out.println(i+"  "+table[i].size());
        }

        if (onSearchResultProgress != null) {
            onSearchResultProgress.onProgress(progressBar);
        }


        // For testing.
        //String[] test = new String[] {"グリードXRヘルム", "グリードXRアーム", "グリードXRフォールド", "グリードXRグリーヴ", "グリードXRメイル"};
        //List<String> test1 = new ArrayList<>(Arrays.asList(test));

        //List<EquipmentNode> equipmentNodes = table[size - 1].getEquipmentNodes();
        //for (int i = 0; i < equipmentNodes.size(); ++i) {
        //    EquipmentNode equipmentNode = equipmentNodes.get(i);
        //    List<Equipment> equipments1 = equipmentNode.getEquipments();
        //    boolean sameSet = true;
        //    for (int j = 0; j < equipments1.size(); ++j) {
        //        if (!test1.contains(equipments1.get(j).getName())) {
        //            sameSet = false;
        //            break;
        //        }
        //    }
        //
        //    if (sameSet) {
        //        // same set found
        //        System.out.println("some test "+i);
        //        //return new EquipmentList(equipmentNode);
        //    }
        //}

        return table[size - 1];
    }

    public void stop() {
        stop = true;
        for (int i = 0; i < THREAD_COUNT; ++i){
            workerThreads[i].interrupt();
            workerThreads[i].exit();
        }
    }
}
