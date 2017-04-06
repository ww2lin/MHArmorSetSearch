package armorsetsearch.thread;

import constants.Constants;
import interfaces.OnSearchResultProgress;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.SwingUtilities;
import models.EquipmentType;
import models.GeneratedArmorSet;
import armorsetsearch.skillactivation.ActivatedSkill;
import armorsetsearch.skillactivation.SkillUtil;

public class ArmorSearchWorkerThread extends Thread {

    private int id;
    private int currentProgress;
    private int currentMaxPossibleSet;
    private EquipmentType equipmentType;
    private EquipmentList previousEquipmentList;
    private EquipmentList currentEquipmentList;
    private final EquipmentList updatedEquipmentSkillList;

    private List<ActivatedSkill> desiredSkills;
    private final List<GeneratedArmorSet> generatedArmorSets;
    private boolean stop = false;
    private AtomicInteger setsFound;
    private final int uniqueSetSearchLimit;
    private OnSearchResultProgress onSearchResultProgress;

    public ArmorSearchWorkerThread(int id,
                                   AtomicInteger setsFound,
                                   int currentProgress,
                                   int currentMaxPossibleSet,
                                   OnSearchResultProgress onSearchResultProgress,
                                   int uniqueSetSearchLimit,
                                   EquipmentType equipmentType,
                                   EquipmentList previousEquipmentList,
                                   EquipmentList currentEquipmentList,
                                   List<ActivatedSkill> desiredSkills,
                                   EquipmentList updatedEquipmentSkillList,
                                   List<GeneratedArmorSet> generatedArmorSets) {
        this.id = id;
        this.setsFound = setsFound;
        this.currentProgress = currentProgress;
        this.currentMaxPossibleSet = currentMaxPossibleSet;
        this.onSearchResultProgress = onSearchResultProgress;
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
                    updateUi(generatedArmorSet, setsFound.incrementAndGet());
                }

                if (stop) {
                    synchronized (generatedArmorSets) {
                        generatedArmorSets.addAll(armorsFound);
                    }
                    return;
                }
                updateUi(null, setsFound.incrementAndGet());
                //if (setsFound.incrementAndGet() > uniqueSetSearchLimit) {
                //    returnData(equipmentList, armorsFound);
                //    return;
                //}
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

    private void updateUi(GeneratedArmorSet generatedArmorSet, int progress) {
        //try {
        //    SwingUtilities.invokeAndWait(() -> {
                if (onSearchResultProgress != null) {
                    onSearchResultProgress.onProgress(generatedArmorSet, getProgressNumber(progress));
                }
            //});
        //} catch (Exception e) {
        //}
    }

    private int getProgressNumber(float i){
        return currentProgress + (int)(i/currentMaxPossibleSet);
    }

    public void exit(){
        this.stop = true;
    }
}
