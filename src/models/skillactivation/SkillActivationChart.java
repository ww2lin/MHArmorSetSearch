package models.skillactivation;

import armorsearch.EquipmentSlots;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import models.ArmorSkill;
import models.ClassType;
import models.Decoration;
import models.Equipment;
import models.EquipmentType;

public class SkillActivationChart {

    private ClassType classType;
    private Map<String, List<SkillActivationRequirement>> skillActivationLookupTable;

    /**
     * Mapping from the skill kind -> actual skill
     * e.g Attack -> Attack Up Small, Attack Up Mid, Attack Up Large.
     */
    public SkillActivationChart(Map<String, List<SkillActivationRequirement>> skillChart, ClassType classType) {
        skillActivationLookupTable = skillChart;
        this.classType = classType;
    }

    public Set<String> getSkillKind(){
        return skillActivationLookupTable.keySet();
    }

    public ActivatedSkill getMaxedActivatedSkill(String kind) {
        List<SkillActivationRequirement> skillActivationRequirements = skillActivationLookupTable.get(kind);

        // There has to be a skill with the type kind, or we will never obtain kind from the CSV sheets.
        SkillActivationRequirement maxSkillActivationRequirement = skillActivationRequirements.get(0);
        for (SkillActivationRequirement skillActivationRequirement : skillActivationRequirements) {
            if (skillActivationRequirement.getPointsNeededToActivate() > maxSkillActivationRequirement.getPointsNeededToActivate()){
                maxSkillActivationRequirement = skillActivationRequirement;
            }
        }
        return new ActivatedSkill(maxSkillActivationRequirement, 0);
    }

    /**
     * Given a list of equipments and a @{models.ClassType}, return what skills has been activated.
     * This can return negative skill.
     * @param equipmentList the equipment set to check
     * @param decorationsForCurrentSet since the program is run in multiple threads,
     * editing the decorations in the equipment list will result in a crash
     * due to race condition, so instead a fresh copy of it, is made and passed in.
     * @return
     */
    public List<ActivatedSkill> getActivatedSkill(Map<EquipmentType, Equipment> equipmentSetTable, Map<EquipmentType, EquipmentSlots> decorationsForCurrentSet){
        Map<String, Integer> currentEquipmentSkillChart = new HashMap<>();
        int torsoUpCount = 0;

        // loop over once and find the number of torsoUp armors
        for (Equipment equipment : equipmentSetTable.values()){
            if (equipment.isTorsoUp()) {
                ++torsoUpCount;
            }
        }
        int skillMultiplier = torsoUpCount + 1;
        // Calculate the skill point for decoration
        for (Map.Entry<EquipmentType, EquipmentSlots> decorationSet : decorationsForCurrentSet.entrySet()){
            if (decorationSet.getKey() == EquipmentType.BODY) {
                updateSkillChartByDecoration(currentEquipmentSkillChart, decorationSet.getValue().getDecorations(), skillMultiplier);
            } else {
                updateSkillChartByDecoration(currentEquipmentSkillChart, decorationSet.getValue().getDecorations(), 1);
            }
        }


        for (Map.Entry<EquipmentType, Equipment> equipmentSet : equipmentSetTable.entrySet()) {
            if (equipmentSet.getKey() == EquipmentType.BODY) {
                updateSkillChartByArmorSkill(currentEquipmentSkillChart, equipmentSet.getValue().getArmorSkills(), skillMultiplier);
            } else {
                updateSkillChartByArmorSkill(currentEquipmentSkillChart, equipmentSet.getValue().getArmorSkills(), 1);
            }
        }
        return getActivatedSkills(currentEquipmentSkillChart);
    }

    public void updateSkillChartByArmorSkill(Map<String, Integer> currentEquipmentSkillChart, Set<ArmorSkill> armorSkills, int skillMuliplier){
        for (ArmorSkill armorSkill : armorSkills){
            // accumulate the skill point by skill kind
            Integer sum = currentEquipmentSkillChart.get(armorSkill.kind);
            if (sum == null){
                // if the current skill kind don't exist, assign it to 0
                sum = 0;
            }

            sum += armorSkill.points;
            sum *= skillMuliplier;
            currentEquipmentSkillChart.put(armorSkill.kind, sum);
        }
    }

    public void updateSkillChartByDecoration(Map<String, Integer> currentEquipmentSkillChart, Map<Decoration, Integer> decorations, int skillMuliplier){
        // loop over the decorations
        for (Map.Entry<Decoration, Integer> decorationSet: decorations.entrySet()) {
            Decoration decoration = decorationSet.getKey();
            Integer frequencyCount = decorationSet.getValue();

            for (ArmorSkill armorSkill : decoration.getArmorSkills()){
                Integer sum = currentEquipmentSkillChart.get(armorSkill.kind);
                if (sum == null){
                    // if the current skill kind don't exist, assign it to 0
                    sum = 0;
                }

                // Times the armor skill by the number of the same jewels
                sum += (armorSkill.points * frequencyCount);
                sum *= skillMuliplier;
                currentEquipmentSkillChart.put(armorSkill.kind, sum);
            }
        }
    }

    /**
     * check to see which skill is activated.
     * @param currentEquipmentSkillChart
     * @return
     */
    public List<ActivatedSkill> getActivatedSkills(Map<String, Integer> currentEquipmentSkillChart){
        List<ActivatedSkill> activatedSkills = new LinkedList<>();
        for (Map.Entry<String, Integer> skill : currentEquipmentSkillChart.entrySet()) {
            String kind = skill.getKey();
            Integer skillPoints = skill.getValue();

            List<SkillActivationRequirement> skillActivationRequirements = skillActivationLookupTable.get(kind);

            SkillActivationRequirement maxSkillActivation = null;
            // Find the biggest armor skill the current skill point can activate.
            // E.g 20 points in Attack -> will only return 'Attack Up Large'
            for (SkillActivationRequirement skillActivationRequirement : skillActivationRequirements){
                // TODO fix it for negative skill?
                boolean isNegativeSkill = skillActivationRequirement.isNegativeSkill();
                boolean hasEnoughSkillPoints = skillPoints >= skillActivationRequirement.getPointsNeededToActivate();
                boolean usableClass = skillActivationRequirement.getClassType() == ClassType.ALL || skillActivationRequirement.getClassType() == classType;

                if (!isNegativeSkill && hasEnoughSkillPoints && usableClass) {
                    maxSkillActivation = skillActivationRequirement;
                }
            }

            if (maxSkillActivation != null){
                // found an activated skill.
                activatedSkills.add(new ActivatedSkill(maxSkillActivation, skillPoints));
            }
        }
        return activatedSkills;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }
}
