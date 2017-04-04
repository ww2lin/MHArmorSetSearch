package armorsearch.thread;

import armorsearch.DecorationSearch;
import armorsearch.filter.ArmorSetFilter;
import interfaces.ArmorSearchWorkerProgress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Decoration;
import models.Equipment;
import models.EquipmentType;
import models.GeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;
import models.skillactivation.SkillUtil;

public class ArmorSearchWorkerThread extends Thread {

    private int id;
    private Map<EquipmentType, List<Equipment>> equipmentsToSearch;
    private ArmorSearchWorkerProgress armorSearchWorkerProgress;
    private List<ArmorSetFilter> armorSetFilters;

    private DecorationSearch decorationSearch;
    private List<ActivatedSkill> desiredSkills;

    public ArmorSearchWorkerThread(int id,
                                   Map<EquipmentType, List<Equipment>> equipmentsToSearch,
                                   ArmorSearchWorkerProgress armorSearchWorkerProgress,
                                   List<ArmorSetFilter> armorSetFilters,
                                   DecorationSearch decorationSearch,
                                   List<ActivatedSkill> desiredSkills) {
        this.id = id;
        this.equipmentsToSearch = equipmentsToSearch;
        this.armorSearchWorkerProgress = armorSearchWorkerProgress;
        this.armorSetFilters = armorSetFilters;
        this.decorationSearch = decorationSearch;
        this.desiredSkills = desiredSkills;
    }

    @Override
    public void run() {
        List<GeneratedArmorSet> results = searchArmor();
        if (armorSearchWorkerProgress != null){
            armorSearchWorkerProgress.onCompleted(id, results);
        }
    }

    /**
     * DP implementation of finding if a possible armor set exists.
     * @return
     */
    private List<GeneratedArmorSet> searchArmor() {

        List<GeneratedArmorSet> results = new ArrayList<>();
        int setsTried = 0;
        int torsoUpCount = 0;
        // Do the body last since we need to know the previous skill point need to adjust for torso ups.
        EquipmentType[] equipmentTypes = {EquipmentType.HEAD, EquipmentType.ARM, EquipmentType.WST, EquipmentType.LEG, EquipmentType.BODY};
        int size = equipmentTypes.length;
        EquipmentList[] table = new EquipmentList[size];

        // Base case.
        EquipmentType currentType = equipmentTypes[0];
        List<Equipment> equipments = equipmentsToSearch.get(currentType);
        for (Equipment equipment : equipments) {
            List<EquipmentNode> equipmentNodes = decorationSearch.findArmorWithDecorationRecursively(equipment);
            table[0] = new EquipmentList(equipmentNodes);

            // We did the earlier internal filtering, so there will only be one
            // Torso up armor piece per EquipmentType
            if (equipment.isTorsoUp()) {
                ++torsoUpCount;
            }
        }

        // iterative case
        for (int i = 1; i < size; ++i){
            currentType = equipmentTypes[i];
            equipments = equipmentsToSearch.get(currentType);

            EquipmentList currentEquipmentList = new EquipmentList();
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
            EquipmentList sumEquipmentList = new EquipmentList();

            for (EquipmentNode preEquipmentNode : previousEquipmentList.getEquipmentNodes()) {
                for (EquipmentNode curEquipmentNode : currentEquipmentList.getEquipmentNodes()) {
                    EquipmentNode sumNode = EquipmentNode.add(preEquipmentNode, curEquipmentNode);
                    sumEquipmentList.add(sumNode);

                    if (!armorSearchWorkerProgress.shouldContinueSearching()) {
                        return results;
                    }

                    // Check if this table satisfy the desire skills.
                    List<ActivatedSkill> activatedSkills = sumNode.getActivatedSkills();
                    if (SkillUtil.containsDesiredSkills(desiredSkills, activatedSkills)) {
                        GeneratedArmorSet generatedArmorSet = new GeneratedArmorSet(sumNode);
                        results.add(generatedArmorSet);

                        if (armorSearchWorkerProgress != null){
                            armorSearchWorkerProgress.onProgress(id, generatedArmorSet, setsTried);
                        }
                    }
                    if (armorSearchWorkerProgress != null){
                        armorSearchWorkerProgress.onProgress(id, null, setsTried);
                    }
                    ++setsTried;
                }
            }

            // place the sumNode back in i-th index
            table[i] = sumEquipmentList;
            System.out.println(i+"  "+table[i].size());

        }

        return results;
    }

    private void updateProgress(GeneratedArmorSet generatedArmorSet, int current) {
        if (armorSearchWorkerProgress != null){
            armorSearchWorkerProgress.onProgress(id, generatedArmorSet, current);
        }
    }
}
