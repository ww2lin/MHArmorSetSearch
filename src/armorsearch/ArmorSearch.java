package armorsearch;

import armorsearch.filter.ArmorSetFilter;
import interfaces.ArmorSearchWorkerProgress;
import interfaces.OnSearchResultProgress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Decoration;
import models.Equipment;
import models.UniquelyGeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;

class ArmorSearch {

    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

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

    List<List<UniquelyGeneratedArmorSet>> matchedSet;

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
        List<Equipment> lst1 = getEquipmentsWithDesiredSkills(armorSkillCacheTable.getHeadEquipmentCache(), desiredSkills);
        List<Equipment> lst2 = getEquipmentsWithDesiredSkills(armorSkillCacheTable.getBodyEquipmentCache(), desiredSkills);
        List<Equipment> lst3 = getEquipmentsWithDesiredSkills(armorSkillCacheTable.getArmEquipmentCache(), desiredSkills);
        List<Equipment> lst4 = getEquipmentsWithDesiredSkills(armorSkillCacheTable.getWstEquipmentCache(), desiredSkills);
        List<Equipment> lst5 = getEquipmentsWithDesiredSkills(armorSkillCacheTable.getLegEquipmentCache(), desiredSkills);

        equipmentsWithDesiredSkills.add(lst1);
        equipmentsWithDesiredSkills.add(lst2);
        equipmentsWithDesiredSkills.add(lst3);
        equipmentsWithDesiredSkills.add(lst4);
        equipmentsWithDesiredSkills.add(lst5);

        equipmentsWithDesiredSkills.sort((o1, o2) -> o2.size() - o1.size());

        // Split of the work base on the thread count available.
        List<Equipment> mostWorkLoadList = equipmentsWithDesiredSkills.get(0);
        int maxSize = mostWorkLoadList.size();

        List<List<Equipment>> splittedHead = new ArrayList<>(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; ++i){
            splittedHead.add(new ArrayList<>());
        }


        // distribute the workload of the head node.
        for (int i = 0; i < maxSize; ++i){
            int bucket = i % THREAD_COUNT;
            splittedHead.get(bucket).add(mostWorkLoadList.get(i));
        }


        // This is where the result will be stored.
        matchedSet = new ArrayList<>(THREAD_COUNT);
        EquipmentNode[] equipmentNodes = new EquipmentNode[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; ++i){
            matchedSet.add(new ArrayList<>());
            equipmentNodes[i] = constructEquipmentNode(splittedHead.get(i), equipmentsWithDesiredSkills);
        }

        maxArmorSetsToSearch = equipmentsWithDesiredSkills.stream().mapToInt(List::size).reduce(1, Math::multiplyExact);

        ArmorSearchWorkerThread[] workers = new ArmorSearchWorkerThread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; ++i){
            workers[i] = new ArmorSearchWorkerThread(i,
                                                     equipmentNodes[i],
                                                     new ArmorSearchWorkerProgressImpl(),
                                                     decorationLookupTable,
                                                     armorSetFilters,
                                                     decorationSearchLimit,
                                                     skillActivationChart,
                                                     desiredSkills);
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
        }
        return equipments;
    }

    /**
     * builds the node backwards, since we want the node with the most item to be the head
     * @param splittedHead
     * @param lists
     * @return
     */
    private EquipmentNode constructEquipmentNode(List<Equipment> splittedHead, List<List<Equipment>> lists){
        EquipmentNode head = null;
        // Skip the first item, as that is the splitted head which is passed in
        for (int i = lists.size() - 1; i > 0; --i){
           head = new EquipmentNode(lists.get(i), head);
        }
        head = new EquipmentNode(splittedHead, head);
        return head;

    }
    private class ArmorSearchWorkerProgressImpl implements ArmorSearchWorkerProgress {

        @Override
        public boolean shouldContinueSearching() {
            return armorSetsFound < uniqueSetSearchLimit && !shouldStop;
        }

        @Override
        public void onProgress(int workerId, UniquelyGeneratedArmorSet uniquelyGeneratedArmorSet) {
            //matchedSet.set(workerId, uniquelyGeneratedArmorSet);
            if (uniquelyGeneratedArmorSet != null) {
                ++armorSetsFound;
            } else {
                ++armorSetsTried;
            }

            if (onSearchResultProgress != null){
                //onSearchResultProgress.onProgress(uniquelyGeneratedArmorSet, armorSetsTried, maxArmorSetsToSearch);
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
