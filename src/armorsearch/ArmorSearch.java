package armorsearch;

import armorsearch.filter.ArmorSetFilter;
import armorsearch.thread.ArmorSearchWorkerThread;
import interfaces.ArmorSearchWorkerProgress;
import interfaces.OnSearchResultProgress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Equipment;
import models.EquipmentType;
import models.GeneratedArmorSet;
import models.skillactivation.ActivatedSkill;

class ArmorSearch {

    private static final int THREAD_COUNT = 1;// Runtime.getRuntime().availableProcessors() / 2;

    private ArmorSkillCacheTable armorSkillCacheTable;
    private List<ArmorSetFilter> armorSetFilters;
    private final int uniqueSetSearchLimit;
    private OnSearchResultProgress onSearchResultProgress;
    private DecorationSearch decorationSearch;

    private int armorSetsFound = 0;
    private int armorSetsTried = 0;
    private int maxArmorSetsToSearch = 0;

    List<List<GeneratedArmorSet>> matchedSet = new ArrayList<>();

    private boolean shouldStop = false;

    public ArmorSearch(ArmorSkillCacheTable armorSkillCacheTable, List<ArmorSetFilter> armorSetFilters, int uniqueSetSearchLimit, DecorationSearch decorationSearch, OnSearchResultProgress onSearchResultProgress) {
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
        shouldStop = false;

        Map<EquipmentType, List<Equipment>> equipments = armorSkillCacheTable.getEquipmentCache(desiredSkills);

        for (int i = 0; i < THREAD_COUNT; ++i){
            matchedSet.add(new ArrayList<>());
        }

        // Need to figure out a way to see if the DP implementation can be run on multiple threads.
        ArmorSearchWorkerThread[] workers = new ArmorSearchWorkerThread[THREAD_COUNT];
        workers[0] = new ArmorSearchWorkerThread(0,
                                                 equipments,
                                                 new ArmorSearchWorkerProgressImpl(),
                                                 armorSetFilters,
                                                 decorationSearch,
                                                 desiredSkills);

        for (int i = 0; i < THREAD_COUNT; ++i){
            workers[i].start();
        }

        for (int i = 0; i < THREAD_COUNT; ++i) {
            try {
                workers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<GeneratedArmorSet> joinedResultSet = new ArrayList<>();
        matchedSet.forEach(joinedResultSet::addAll);
        return joinedResultSet;
    }

    private class ArmorSearchWorkerProgressImpl implements ArmorSearchWorkerProgress {

        @Override
        public boolean shouldContinueSearching() {
            return armorSetsFound < uniqueSetSearchLimit && !shouldStop;
        }

        @Override
        public void onProgress(int workerId, GeneratedArmorSet generatedArmorSet, int armorSetsTried) {
            //matchedSet.set(workerId, uniquelyGeneratedArmorSet);
            if (generatedArmorSet != null) {
                ++ArmorSearch.this.armorSetsFound;
            } else {
                ArmorSearch.this.armorSetsTried+=armorSetsTried;
            }

            if (onSearchResultProgress != null){
                onSearchResultProgress.onProgress(generatedArmorSet, ArmorSearch.this.armorSetsTried, maxArmorSetsToSearch);
            }
        }

        @Override
        public void onCompleted(int workerId, List<GeneratedArmorSet> generatedArmorSets) {
            matchedSet.get(workerId).addAll(generatedArmorSets);
        }
    }

    public void stop() {
        this.shouldStop = true;
    }
}
