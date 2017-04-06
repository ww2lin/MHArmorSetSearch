package armorsearch.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import models.EquipmentType;
import models.GeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillUtil;

public class ArmorSearchWorkerThread extends Thread {

    private int id;
    private EquipmentType equipmentType;
    private EquipmentList previousEquipmentList;
    private EquipmentList currentEquipmentList;
    private final EquipmentList updatedEquipmentSkillList;

    private List<ActivatedSkill> desiredSkills;
    private final List<GeneratedArmorSet> generatedArmorSets;
    private boolean stop = false;
    private AtomicInteger setsFound;
    private final int uniqueSetSearchLimit;

    public ArmorSearchWorkerThread(int id,
                                   AtomicInteger setsFound,
                                   int uniqueSetSearchLimit,
                                   EquipmentType equipmentType,
                                   EquipmentList previousEquipmentList,
                                   EquipmentList currentEquipmentList,
                                   List<ActivatedSkill> desiredSkills,
                                   EquipmentList updatedEquipmentSkillList,
                                   List<GeneratedArmorSet> generatedArmorSets) {
        this.id = id;
        this.setsFound = setsFound;
        this.uniqueSetSearchLimit = uniqueSetSearchLimit;
        this.equipmentType = equipmentType;
        this.previousEquipmentList = previousEquipmentList;
        this.currentEquipmentList = currentEquipmentList;
        this.desiredSkills = desiredSkills;
        this.updatedEquipmentSkillList = updatedEquipmentSkillList;
        this.generatedArmorSets = generatedArmorSets;
    }

    @Override
    public void run() {
        EquipmentList equipmentList = new EquipmentList();
        List<GeneratedArmorSet> armorsFound = new ArrayList<>();
        // TODO Use multiple threads here to divide up the work.
        for (EquipmentNode curEquipmentNode : currentEquipmentList.getEquipmentNodes()) {
            for (EquipmentNode preEquipmentNode : previousEquipmentList.getEquipmentNodes()) {
                if (stop) {
                    return;
                }

                EquipmentNode sumNode = EquipmentNode.add(preEquipmentNode, curEquipmentNode, equipmentType);
                equipmentList.add(sumNode);

                // Check if this table satisfy the desire skills.
                List<ActivatedSkill> activatedSkills = sumNode.getActivatedSkills();
                if (SkillUtil.containsDesiredSkills(desiredSkills, activatedSkills)) {
                    GeneratedArmorSet generatedArmorSet = new GeneratedArmorSet(sumNode);
                    armorsFound.add(generatedArmorSet);
                }

                if (stop) {
                    synchronized (generatedArmorSets) {
                        generatedArmorSets.addAll(armorsFound);
                    }
                    return;
                }

                if (setsFound.incrementAndGet() > uniqueSetSearchLimit) {
                    returnData(equipmentList, armorsFound);
                    return;
                }
            }
        }
        returnData(equipmentList, armorsFound);
    }

    private void returnData(EquipmentList equipmentList, List<GeneratedArmorSet> armorsFound){
        synchronized (updatedEquipmentSkillList) {
            updatedEquipmentSkillList.add(equipmentList);
        }

        synchronized (generatedArmorSets) {
            generatedArmorSets.addAll(armorsFound);
        }
    }

    public void exit(){
        this.stop = true;
    }
}
