package armorsetsearch.armorsearch;

import armorsetsearch.ArmorSkillCacheTable;
import armorsetsearch.charmsearch.CharmSearch;
import armorsetsearch.decorationsearch.DecorationSearch;
import armorsetsearch.filter.ArmorSetFilter;
import armorsetsearch.armorsearch.thread.ArmorSearchWorkerThread;
import armorsetsearch.armorsearch.thread.EquipmentList;
import armorsetsearch.armorsearch.thread.EquipmentNode;
import constants.Constants;
import interfaces.OnSearchResultProgress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import models.Equipment;
import models.EquipmentType;
import models.GeneratedArmorSet;
import armorsetsearch.skillactivation.ActivatedSkill;

import static constants.Constants.GENERATED_EQUIPMENT_ID;
import static constants.Constants.THREAD_COUNT;

public class ArmorSearch {

    private ArmorSkillCacheTable armorSkillCacheTable;
    private List<ArmorSetFilter> armorSetFilters;
    private final int uniqueSetSearchLimit;
    private OnSearchResultProgress onSearchResultProgress;
    private DecorationSearch decorationSearch;
    private CharmSearch charmSearch;

    // use to stop the threads.
    private boolean stop = false;
    private ArmorSearchWorkerThread[] workerThreads;
    private int weapSlots;

    public ArmorSearch(int weapSlots, ArmorSkillCacheTable armorSkillCacheTable, List<ArmorSetFilter> armorSetFilters, int uniqueSetSearchLimit, DecorationSearch decorationSearch, CharmSearch charmSearch, OnSearchResultProgress onSearchResultProgress) {
        this.weapSlots = weapSlots;
        this.armorSkillCacheTable = armorSkillCacheTable;
        this.armorSetFilters = armorSetFilters;
        this.uniqueSetSearchLimit = uniqueSetSearchLimit;
        this.onSearchResultProgress = onSearchResultProgress;
        this.decorationSearch = decorationSearch;
        this.charmSearch = charmSearch;
    }

    /**
     * run a dfs search for the skill search
     * @param desiredSkills that the user wants to generate
     * @return list of equipment that matches what the user wants
     */
    public List<GeneratedArmorSet> findArmorSetWith(List<ActivatedSkill> desiredSkills) {
        Map<EquipmentType, List<Equipment>> equipments = armorSkillCacheTable.getEquipmentCache(desiredSkills);
        return searchArmor(desiredSkills, equipments);
    }

    /**
     * DP implementation of finding if a possible armor set exists.
     * @return
     */
    private List<GeneratedArmorSet> searchArmor(List<ActivatedSkill> desiredSkills, Map<EquipmentType, List<Equipment>> equipmentsToSearch) {
        System.out.println("Number Of threads going to be spawned: "+THREAD_COUNT);
        long timeStamp = System.currentTimeMillis();

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
            EquipmentNode equipmentNodes = decorationSearch.findArmorWithDecoration(equipment);
            currentEquipmentList.add(equipmentNodes);
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
                // TODO create a filter for desired skill, if a skill is maxed, then filter out the
                // sets with it that contains the jewel.
                EquipmentNode equipmentNodes = decorationSearch.findArmorWithDecoration(equipment);
                currentEquipmentList.add(equipmentNodes);
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
                    return results;
                }
            }

            if (stop || setsFound.get() > uniqueSetSearchLimit) {
                return results;
            }

            progressBar+=progressChuck;

            // place the sumNode back in i-th index
            table[i] = updatedEquipmentSkillList;

            // free up the memory that we dont need anymore.
            table[i-1] = null;

            System.out.println(i+"  "+table[i].size());
        }
        timeStamp = System.currentTimeMillis() - timeStamp;
        System.out.println("armor search time elapsed(ms): "+timeStamp);

        if (onSearchResultProgress != null) {
            onSearchResultProgress.onProgress(progressBar);
        }

        System.out.println("Starting Charm Search");
        timeStamp = System.currentTimeMillis();

        results.addAll(charmSearch.findAValidCharmWithArmorSkill(desiredSkills, table[size-1], progressBar));

        timeStamp = System.currentTimeMillis() - timeStamp;
        System.out.println("charm search time elapsed(ms): "+timeStamp);

        return results;
    }

    public void stop() {
        stop = true;
        for (int i = 0; i < THREAD_COUNT; ++i){
            workerThreads[i].interrupt();
            workerThreads[i].exit();
        }
    }
}
