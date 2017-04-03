package armorsearch;

import armorsearch.filter.ArmorSetFilter;
import armorsearch.thread.ArmorSearchWorkerThread;
import interfaces.ArmorSearchWorkerProgress;
import interfaces.OnSearchResultProgress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Decoration;
import models.Equipment;
import models.EquipmentType;
import models.UniquelyGeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;
import utils.WorkerThread;

class ArmorSearch {

    private static final int THREAD_COUNT = 1;// Runtime.getRuntime().availableProcessors() / 2;

    private ArmorSkillCacheTable armorSkillCacheTable;
    private Map<String, List<Decoration>>decorationLookupTable;
    private List<ArmorSetFilter> armorSetFilters;
    private final int uniqueSetSearchLimit;
    private final int decorationSearchLimit;
    private OnSearchResultProgress onSearchResultProgress;
    private SkillActivationChart skillActivationChart;

    private int armorSetsFound = 0;
    private int armorSetsTried = 0;
    private int maxArmorSetsToSearch = 0;

    List<List<UniquelyGeneratedArmorSet>> matchedSet = new ArrayList<>();

    private boolean shouldStop = false;

    public ArmorSearch(ArmorSkillCacheTable armorSkillCacheTable, Map<String, List<Decoration>> decorationLookupTable, List<ArmorSetFilter> armorSetFilters, int uniqueSetSearchLimit, int decorationSearchLimit, SkillActivationChart skillActivationChart, OnSearchResultProgress onSearchResultProgress) {
        this.armorSkillCacheTable = armorSkillCacheTable;
        this.decorationLookupTable = decorationLookupTable;
        this.armorSetFilters = armorSetFilters;
        this.uniqueSetSearchLimit = uniqueSetSearchLimit;
        this.decorationSearchLimit = decorationSearchLimit;
        this.skillActivationChart = skillActivationChart;
        this.onSearchResultProgress = onSearchResultProgress;
    }

    /**
     * run a dfs search for the skill search
     * @param desiredSkills that the user wants to generate
     * @return list of equipment that matches what the user wants
     */
    public List<UniquelyGeneratedArmorSet> findArmorSetWith(List<ActivatedSkill> desiredSkills) {
        shouldStop = false;

        List<List<Equipment>> equipmentsWithDesiredSkills = new ArrayList<>(5);
        List<Equipment> headList = getEquipmentsWithDesiredSkills(armorSkillCacheTable.getHeadEquipmentCache(), desiredSkills);
        List<Equipment> bodyList = getEquipmentsWithDesiredSkills(armorSkillCacheTable.getBodyEquipmentCache(), desiredSkills);
        List<Equipment> armList = getEquipmentsWithDesiredSkills(armorSkillCacheTable.getArmEquipmentCache(), desiredSkills);
        List<Equipment> wstList = getEquipmentsWithDesiredSkills(armorSkillCacheTable.getWstEquipmentCache(), desiredSkills);
        List<Equipment> legList = getEquipmentsWithDesiredSkills(armorSkillCacheTable.getLegEquipmentCache(), desiredSkills);

        //equipmentsWithDesiredSkills.add(lst1);
        //equipmentsWithDesiredSkills.add(lst2);
        //equipmentsWithDesiredSkills.add(lst3);
        //equipmentsWithDesiredSkills.add(lst4);
        //equipmentsWithDesiredSkills.add(lst5);
        //
        //equipmentsWithDesiredSkills.sort((o1, o2) -> o2.size() - o1.size());
        //
        //// Split of the work base on the thread count available.
        //List<Equipment> mostWorkLoadList = equipmentsWithDesiredSkills.get(0);
        //int maxSize = mostWorkLoadList.size();

        Map<EquipmentType, List<Equipment>> equipments = new HashMap<>();
        equipments.put(EquipmentType.HEAD, headList);
        equipments.put(EquipmentType.BODY, bodyList);
        equipments.put(EquipmentType.ARM, armList);
        equipments.put(EquipmentType.WST, wstList);
        equipments.put(EquipmentType.LEG, legList);


        for (int i = 0; i < THREAD_COUNT; ++i){
            matchedSet.add(new ArrayList<>());
        }

        ArmorSearchWorkerThread[] workers = new ArmorSearchWorkerThread[THREAD_COUNT];

        workers[0] = new ArmorSearchWorkerThread(0,
                                                 equipments,
                                                 new ArmorSearchWorkerProgressImpl(),
                                                 decorationLookupTable,
                                                 armorSetFilters,
                                                 decorationSearchLimit,
                                                 skillActivationChart,
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

        List<UniquelyGeneratedArmorSet> joinedResultSet = new ArrayList<>();
        matchedSet.forEach(joinedResultSet::addAll);
        return joinedResultSet;
    }

    private List<Equipment> getEquipmentsWithDesiredSkills(Map<String, List<Equipment>> cache, List<ActivatedSkill> desiredSkills) {
        List<Equipment> equipments = new ArrayList<>();
        for (ActivatedSkill activatedSkill : desiredSkills) {
            List<Equipment> equipmentsWithDesiredSkills = cache.get(activatedSkill.getKind());
            if (equipmentsWithDesiredSkills != null && !equipmentsWithDesiredSkills.isEmpty()) {
                equipments.addAll(equipmentsWithDesiredSkills);
            }
            // TODO add in torso up/3 slots pieces
        }
        return equipments;
    }

    private class ArmorSearchWorkerProgressImpl implements ArmorSearchWorkerProgress {

        @Override
        public boolean shouldContinueSearching() {
            return armorSetsFound < uniqueSetSearchLimit && !shouldStop;
        }

        @Override
        public void onProgress(int workerId, UniquelyGeneratedArmorSet uniquelyGeneratedArmorSet, int armorSetsTried) {
            //matchedSet.set(workerId, uniquelyGeneratedArmorSet);
            if (uniquelyGeneratedArmorSet != null) {
                ++ArmorSearch.this.armorSetsFound;
            } else {
                ArmorSearch.this.armorSetsTried+=armorSetsTried;
            }

            if (onSearchResultProgress != null){
                onSearchResultProgress.onProgress(uniquelyGeneratedArmorSet, ArmorSearch.this.armorSetsTried, maxArmorSetsToSearch);
            }
        }

        @Override
        public void onCompleted(int workerId, List<UniquelyGeneratedArmorSet> uniquelyGeneratedArmorSets) {
            matchedSet.get(workerId).addAll(uniquelyGeneratedArmorSets);
        }
    }

    public void stop() {
        this.shouldStop = true;
    }
}
