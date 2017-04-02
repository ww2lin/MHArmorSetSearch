package armorsearch;

import armorsearch.filter.ArmorSetFilter;
import interfaces.ArmorSearchWorkerProgress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Decoration;
import models.Equipment;
import models.GeneratedArmorSet;
import models.UniquelyGeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;

public class ArmorSearchWorkerThread extends Thread {

    private int id;
    private EquipmentNode equipmentNode;
    private ArmorSearchWorkerProgress armorSearchWorkerProgress;
    private Map<String, List<Decoration>> decorationLookupTable;
    private List<ArmorSetFilter> armorSetFilters;
    private final int decorationSearchLimit;
    private SkillActivationChart skillActivationChart;
    List<ActivatedSkill> desiredSkills;

    private int armorSetsTried = 0;
    private int maxArmorSets = 0;

    public ArmorSearchWorkerThread(int id, EquipmentNode equipmentNode, ArmorSearchWorkerProgress armorSearchWorkerProgress, Map<String, List<Decoration>> decorationLookupTable, List<ArmorSetFilter> armorSetFilters, int decorationSearchLimit, SkillActivationChart skillActivationChart, List<ActivatedSkill> desiredSkills) {
        this.id = id;
        this.equipmentNode = equipmentNode;
        this.armorSearchWorkerProgress = armorSearchWorkerProgress;
        this.decorationLookupTable = decorationLookupTable;
        this.armorSetFilters = armorSetFilters;
        this.decorationSearchLimit = decorationSearchLimit;
        this.skillActivationChart = skillActivationChart;
        this.desiredSkills = desiredSkills;

        maxArmorSets = equipmentNode.getTotalCombinations();
    }

    @Override
    public void run() {
        List<UniquelyGeneratedArmorSet> uniquelyGeneratedArmorSets = new ArrayList<>();
        findArmorRecursively(new ArrayList<>(),
                             equipmentNode,
                             uniquelyGeneratedArmorSets,
                             desiredSkills);
        if (armorSearchWorkerProgress != null){
            armorSearchWorkerProgress.onCompleted(id, uniquelyGeneratedArmorSets);
        }
    }

    private void findArmorRecursively(List<Equipment> currentSet,
                                      EquipmentNode equipmentNode,
                                      List<UniquelyGeneratedArmorSet> matchedSet,
                                      List<ActivatedSkill> desiredSkills) {
        // check to see if we hit the limit of armor search
        if (!armorSearchWorkerProgress.shouldContinueSearching()) {
            return;
        } else if (equipmentNode != null) {
            List<Equipment> equipments = equipmentNode.armorWithDesiredSkills;
            for (Equipment equipment : equipments) {
                currentSet.add(equipment);
                findArmorRecursively(currentSet,
                                     equipmentNode.next,
                                     matchedSet,
                                     desiredSkills);
                // back tracking.
                currentSet.remove(equipment);

                if (!armorSearchWorkerProgress.shouldContinueSearching()){
                    return;
                }
            }
        } else {

            if (armorSearchWorkerProgress != null) {
                ++armorSetsTried;
                System.out.println("id: "+id +"  tried: "+armorSetsTried+ " max:"+maxArmorSets);
                armorSearchWorkerProgress.onProgress(id, null);
            }

            // apply filters.
            for (ArmorSetFilter armorSetFilter : armorSetFilters) {
                if (!armorSetFilter.isArmorValid(currentSet)){
                    return;
                }
            }

            // we found a potential full set...
            List<GeneratedArmorSet> sameArmorDifferentDecoration = new ArrayList<>();
            List<EquipmentSlots> decorationsForCurrentSet = new ArrayList<>();
            for (int i = 0; i < currentSet.size(); ++i){
                decorationsForCurrentSet.add(new EquipmentSlots(currentSet.get(i).getSlots()));
            }
            DecorationSearch.findArmorWithJewelRecursively(decorationsForCurrentSet,
                                                           decorationSearchLimit,
                                                           decorationLookupTable,
                                                           skillActivationChart,
                                                           currentSet,
                                                           0,
                                                           sameArmorDifferentDecoration,
                                                           desiredSkills,
                                                           new ArrayList<>());

            if (!sameArmorDifferentDecoration.isEmpty()) {
                // create a new array reference for current set, so that when back tracking the list is not modified
                UniquelyGeneratedArmorSet uniquelyGeneratedArmorSet = new UniquelyGeneratedArmorSet(sameArmorDifferentDecoration);
                matchedSet.add(uniquelyGeneratedArmorSet);
                if (armorSearchWorkerProgress != null) {
                    armorSearchWorkerProgress.onProgress(id, uniquelyGeneratedArmorSet);
                }
            }
        }
    }
}
