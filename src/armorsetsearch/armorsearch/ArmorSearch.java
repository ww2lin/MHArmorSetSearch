package armorsetsearch.armorsearch;

import armorsetsearch.ArmorSkillCacheTable;
import armorsetsearch.decorationsearch.DecorationSearch;
import armorsetsearch.filter.ArmorSetFilter;
import armorsetsearch.thread.ArmorSearchWorkerThread;
import armorsetsearch.thread.EquipmentList;
import armorsetsearch.thread.EquipmentNode;
import interfaces.OnSearchResultProgress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import models.Equipment;
import models.EquipmentType;
import models.GeneratedArmorSet;
import armorsetsearch.skillactivation.ActivatedSkill;

import static constants.Constants.THREAD_COUNT;

public class ArmorSearch {

    private ArmorSkillCacheTable armorSkillCacheTable;
    private List<ArmorSetFilter> armorSetFilters;
    private final int uniqueSetSearchLimit;
    private OnSearchResultProgress onSearchResultProgress;
    private DecorationSearch decorationSearch;

    // use to stop the threads.
    private boolean stop = false;
    private ArmorSearchWorkerThread[] workerThreads;
    private int weapSlots;

    public ArmorSearch(int weapSlots, ArmorSkillCacheTable armorSkillCacheTable, List<ArmorSetFilter> armorSetFilters, int uniqueSetSearchLimit, DecorationSearch decorationSearch, OnSearchResultProgress onSearchResultProgress) {
        this.weapSlots = weapSlots;
        this.armorSkillCacheTable = armorSkillCacheTable;
        this.armorSetFilters = armorSetFilters;
        this.uniqueSetSearchLimit = uniqueSetSearchLimit;
        this.onSearchResultProgress = onSearchResultProgress;
        this.decorationSearch = decorationSearch;
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

        EquipmentType[] equipmentTypes = {EquipmentType.HEAD, EquipmentType.ARM, EquipmentType.WST, EquipmentType.LEG, EquipmentType.BODY};

        int progressChuck = 100 / (equipmentTypes.length);
        int progressBar = progressChuck;

        List<GeneratedArmorSet> results = new ArrayList<>();
        // Do the body last since we need to know the previous skill point need to adjust for torso ups.



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

            //if (stop || setsFound.get() > uniqueSetSearchLimit) {
                //return results;
            //}

            progressBar+=progressChuck;

            // place the sumNode back in i-th index
            table[i] = updatedEquipmentSkillList;
            System.out.println(i+"  "+table[i].size());
        }
        timeStamp = System.currentTimeMillis() - timeStamp;
        System.out.println("armor search time elapsed(ms): "+timeStamp);

        if (onSearchResultProgress != null) {
            onSearchResultProgress.onProgress(null, progressBar);
        }

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
