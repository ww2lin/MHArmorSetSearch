package models;

import java.util.Comparator;
import java.util.List;
import models.skillactivation.ActivatedSkill;

public class GeneratedArmorSet {
    List<ActivatedSkill> activatedSkills;
    List<Equipment> equipments;

    public GeneratedArmorSet(List<ActivatedSkill> activatedSkills, List<Equipment> equipments) {
        this.activatedSkills = activatedSkills;
        this.equipments = equipments;
    }

    public static class MostSkillComparator implements Comparator<GeneratedArmorSet> {
        @Override
        public int compare(GeneratedArmorSet o1, GeneratedArmorSet o2) {
            // dont need to worry about overflow, as number of unique skills is way too small.
            return o1.activatedSkills.size() - o2.activatedSkills.size();
        }
    }

    @Override
    public String toString() {
        return "GeneratedArmorSet{" +
            "activatedSkills=" + activatedSkills +
            ", equipments=" + equipments +
            '}';
    }

    public List<ActivatedSkill> getActivatedSkills() {
        return activatedSkills;
    }

    public List<Equipment> getEquipments() {
        return equipments;
    }
}
