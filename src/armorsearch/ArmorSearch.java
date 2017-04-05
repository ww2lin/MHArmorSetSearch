package armorsearch;

import armorsearch.filter.ArmorSetFilter;
import armorsearch.thread.ArmorSearchWorkerThread;
import armorsearch.thread.EquipmentList;
import armorsearch.thread.EquipmentNode;
import interfaces.ArmorSearchWorkerProgress;
import interfaces.OnSearchResultProgress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Equipment;
import models.EquipmentType;
import models.GeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillUtil;

class ArmorSearch {

    private static final int THREAD_COUNT = 1;// Runtime.getRuntime().availableProcessors() / 2;

    private ArmorSkillCacheTable armorSkillCacheTable;
    private List<ArmorSetFilter> armorSetFilters;
    private final int uniqueSetSearchLimit;
    private OnSearchResultProgress onSearchResultProgress;
    private DecorationSearch decorationSearch;

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

        return searchArmor(desiredSkills, equipments);
    }

    /**
     * DP implementation of finding if a possible armor set exists.
     * @return
     */
    private List<GeneratedArmorSet> searchArmor(List<ActivatedSkill> desiredSkills, Map<EquipmentType, List<Equipment>> equipmentsToSearch) {

        List<GeneratedArmorSet> results = new ArrayList<>();
        // Do the body last since we need to know the previous skill point need to adjust for torso ups.
        EquipmentType[] equipmentTypes = {EquipmentType.HEAD, EquipmentType.ARM, EquipmentType.WST, EquipmentType.LEG, EquipmentType.BODY};


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

        // iterative case
        for (int i = 1; i < size; ++i){
            currentType = equipmentTypes[i];
            equipments = equipmentsToSearch.get(currentType);

            currentEquipmentList = new EquipmentList();
            // construct all the table for the i element first.
            for (Equipment equipment : equipments) {
                // sets with it that contains the jewel.
                EquipmentNode equipmentNodes = decorationSearch.findArmorWithDecoration(equipment);
                currentEquipmentList.add(equipmentNodes);
            }

            // update the all the values for the current i from i-1
            // add it to sumEquipmentList - this is to avoid value getting updated after one iteration
            EquipmentList previousEquipmentList = table[i-1];
            EquipmentList sumEquipmentList = new EquipmentList();

            // TODO Use multiple threads here to divide up the work.
            for (EquipmentNode preEquipmentNode : previousEquipmentList.getEquipmentNodes()) {
                for (EquipmentNode curEquipmentNode : currentEquipmentList.getEquipmentNodes()) {
                    if (shouldStop) {
                        return results;
                    }

                    EquipmentNode sumNode = EquipmentNode.add(preEquipmentNode, curEquipmentNode, equipmentTypes[i]);
                    sumEquipmentList.add(sumNode);

                    // Check if this table satisfy the desire skills.
                    List<ActivatedSkill> activatedSkills = sumNode.getActivatedSkills();
                    if (SkillUtil.containsDesiredSkills(desiredSkills, activatedSkills)) {
                        GeneratedArmorSet generatedArmorSet = new GeneratedArmorSet(sumNode);
                        results.add(generatedArmorSet);

                    }
                }
            }

            // place the sumNode back in i-th index
            table[i] = sumEquipmentList;
            System.out.println(i+"  "+table[i].size());

        }

        return results;
    }

    public void stop() {
        this.shouldStop = true;
    }
}
