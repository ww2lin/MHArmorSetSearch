package models.skillactivation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import models.ArmorSkill;
import models.ClassType;
import models.Decoration;
import models.Equipment;

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
     */
    public List<ActivatedSkill> getActivatedSkill(List<Equipment> equipmentList){
        Map<String, Integer> currentEquipmentSkillChart = new HashMap<>();
        List<ActivatedSkill> activatedSkills = new LinkedList<>();

        // loop over the equipment set, head, armor, body, leg, etc...
        for (Equipment equipment : equipmentList){
            // loop over the skills for a given equipment
            for (ArmorSkill armorSkill : equipment.getArmorSkills()){
                // accumulate the skill point by skill kind
                Integer sum = currentEquipmentSkillChart.get(armorSkill.kind);
                if (sum == null){
                    // if the current skill kind don't exist, assign it to 0
                    sum = 0;
                }

                sum += armorSkill.points;
                currentEquipmentSkillChart.put(armorSkill.kind, sum);
            }

            // loop over the decorations
            for (Map.Entry<Decoration, Integer> decorationSet: equipment.getDecorations().entrySet()) {
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
                    currentEquipmentSkillChart.put(armorSkill.kind, sum);
                }
            }
        }

        // check to see which skill is activated.
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
        // Deep copy the arraylist for decorations
        return activatedSkills;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }
}
