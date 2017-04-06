package models;

import armorsetsearch.thread.EquipmentNode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import armorsetsearch.skillactivation.ActivatedSkill;


// contains a list of equipment sets, with decorations
public class GeneratedArmorSet {
    List<ActivatedSkill> activatedSkills;
    List<Resistance> totalResistance;
    List<Equipment> equipments;

    int totalBaseDefense;
    int totalMaxDefense;

    public GeneratedArmorSet(List<ActivatedSkill> activatedSkills, List<Equipment> equipments) {
        this.activatedSkills = activatedSkills;
        this.equipments = equipments;
        totalResistance = calculateTotalResistance();

        totalBaseDefense = calculateTotalBaseDefense();
        totalMaxDefense = calculateTotalMaxDefense();
    }

    public GeneratedArmorSet(EquipmentNode equipmentNode) {
        this(equipmentNode.getActivatedSkills(), equipmentNode.getEquipments());
    }

    public static class MostSkillComparator implements Comparator<GeneratedArmorSet> {
        @Override
        public int compare(GeneratedArmorSet o1, GeneratedArmorSet o2) {
            // dont need to worry about overflow, as number of unique skills is way too small.
            return o1.activatedSkills.size() - o2.activatedSkills.size();
        }
    }

    @Override public String toString() {
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

    private List<Resistance> calculateTotalResistance(){
        Resistance fire = new Resistance(ResistanceType.FIRE, 0);
        Resistance water = new Resistance(ResistanceType.WATER, 0);
        Resistance thunder = new Resistance(ResistanceType.THUNDER, 0);
        Resistance ice = new Resistance(ResistanceType.ICE, 0);
        Resistance dragon = new Resistance(ResistanceType.DRAGON, 0);

        List<Resistance> resistances = new ArrayList<>();
        resistances.add(fire);
        resistances.add(water);
        resistances.add(thunder);
        resistances.add(ice);
        resistances.add(dragon);

        equipments.forEach(equipment -> {
            equipment.getResistances().forEach(resistance -> {
                switch (resistance.resistanceType){
                    case FIRE:
                        fire.add(resistance);
                        break;
                    case WATER:
                        water.add(resistance);
                        break;
                    case THUNDER:
                        thunder.add(resistance);
                        break;
                    case ICE:
                        ice.add(resistance);
                        break;
                    case DRAGON:
                        dragon.add(resistance);
                        break;
                    default:
                        break;
                }
            });
        });
        return resistances;
    }

    private int calculateTotalBaseDefense(){
        return equipments.stream().mapToInt(Equipment::getBaseDefense).sum();
    }

    private int calculateTotalMaxDefense(){
        return equipments.stream().mapToInt(Equipment::getMaxDefense).sum();
    }

    public List<Resistance> getTotalResistance(){
        return totalResistance;
    }

    public int getTotalBaseDefense() {
        return totalBaseDefense;
    }

    public int getTotalMaxDefense() {
        return totalMaxDefense;
    }
}
