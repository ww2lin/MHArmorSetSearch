package models;

import java.util.Comparator;
import java.util.List;
import models.skillactivation.ActivatedSkill;

public class UniquelyGeneratedArmorSet {
    List<GeneratedArmorSet> generatedArmorSets;

    public UniquelyGeneratedArmorSet(List<GeneratedArmorSet> generatedArmorSets) {
        this.generatedArmorSets = generatedArmorSets;
    }

    public static class MostSkillComparator implements Comparator<UniquelyGeneratedArmorSet> {
        @Override
        public int compare(UniquelyGeneratedArmorSet o1, UniquelyGeneratedArmorSet o2) {
            // dont need to worry about overflow, as number of unique skills is way too small.
            return o1.generatedArmorSets.get(0).activatedSkills.size() - o2.generatedArmorSets.get(0).activatedSkills.size();
        }
    }

    public List<GeneratedArmorSet> getGeneratedArmorSets() {
        return generatedArmorSets;
    }
}
