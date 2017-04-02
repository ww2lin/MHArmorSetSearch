package models;

import armorsearch.EquipmentSlots;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Equipment {
    private static final int NOT_AVAILABLE = 99;

    private String name;
    private Gender gender;
    private ClassType classType;
    private int rarity;
    private int slots;
    // 99 means you cant get it.
    private int onlineMonsterAvailableAtQuestLevel;
    private int villageMonsterAvailableAtQuestLevel;

    // Cant read japanese, guessing this means if you need to do both online/offline quest
    private boolean needBothOnlineAndOffLineQuest;

    private int baseDefense;
    private int maxDefense;

    private List<Resistance> resistances;

    private Set<ArmorSkill> armorSkills;
    private Set<ItemPart> itemParts;

    // State variable, not from the CSV
    private int slotsUsed;
    // Maps: Decoration -> frequency/Count of this jewel
    private Map<Decoration, Integer> decorations = new HashMap<>();
    private boolean isTorsoUp;
    private EquipmentType equipmentType;
    private boolean canBeSubstitutedForAnyOtherEquipment;

    private Equipment(){}

    public Equipment(Equipment other, EquipmentSlots decorations) {
        this.name = other.name;
        this.gender = other.gender;
        this.classType = other.classType;
        this.rarity = other.rarity;
        this.slots = other.slots;
        this.onlineMonsterAvailableAtQuestLevel = other.onlineMonsterAvailableAtQuestLevel;
        this.villageMonsterAvailableAtQuestLevel = other.villageMonsterAvailableAtQuestLevel;
        this.needBothOnlineAndOffLineQuest = other.needBothOnlineAndOffLineQuest;
        this.baseDefense = other.baseDefense;
        this.maxDefense = other.maxDefense;
        this.resistances = other.resistances;
        this.armorSkills = other.armorSkills;
        this.itemParts = other.itemParts;
        this.slotsUsed = decorations.getSlotsUsed();

        for (Map.Entry<Decoration, Integer> decorationSet : decorations.getDecorations().entrySet()) {
            getDecorations().put(decorationSet.getKey(), decorationSet.getValue());
        }

        this.isTorsoUp = other.isTorsoUp;
        this.equipmentType = other.equipmentType;
        this.canBeSubstitutedForAnyOtherEquipment = other.canBeSubstitutedForAnyOtherEquipment;
    }

    public static Equipment Builder(){
        return new Equipment();
    }

    @Override
    public String toString() {
        return "Equipment{" +
            "name='" + name + '\'' +
            ", gender=" + gender +
            ", classType=" + classType +
            ", rarity=" + rarity +
            ", slots=" + slots +
            ", onlineMonsterAvailableAtQuestLevel=" + onlineMonsterAvailableAtQuestLevel +
            ", villageMonsterAvailableAtQuestLevel=" + villageMonsterAvailableAtQuestLevel +
            ", needBothOnlineAndOffLineQuest=" + needBothOnlineAndOffLineQuest +
            ", baseDefense=" + baseDefense +
            ", maxDefense=" + maxDefense +
            ", resistances=" + resistances +
            ", armorSkills=" + armorSkills +
            ", itemParts=" + itemParts +
            ", slotsUsed=" + slotsUsed +
            ", decorations=" + decorations +
            ", isTorsoUp=" + isTorsoUp +
            ", equipmentType=" + equipmentType +
            ", canBeSubstitutedForAnyOtherEquipment=" + canBeSubstitutedForAnyOtherEquipment +
            '}';
    }

    public String getName() {
        return name;
    }

    public Equipment setName(String name) {
        this.name = name;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public Equipment setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public ClassType getClassType() {
        return classType;
    }

    public Equipment setClassType(ClassType classType) {
        this.classType = classType;
        return this;
    }

    public int getRarity() {
        return rarity;
    }

    public Equipment setRarity(int rarity) {
        this.rarity = rarity;
        return this;
    }

    public int getSlots() {
        return slots;
    }

    public Equipment setSlots(int slots) {
        this.slots = slots;
        return this;
    }

    public int getOnlineMonsterAvailableAtQuestLevel() {
        return onlineMonsterAvailableAtQuestLevel;
    }

    public Equipment setOnlineMonsterAvailableAtQuestLevel(int onlineMonsterAvailableAtQuestLevel) {
        this.onlineMonsterAvailableAtQuestLevel = onlineMonsterAvailableAtQuestLevel;
        return this;
    }

    public int getVillageMonsterAvailableAtQuestLevel() {
        return villageMonsterAvailableAtQuestLevel;
    }

    public Equipment setVillageMonsterAvailableAtQuestLevel(int villageMonsterAvailableAtQuestLevel) {
        this.villageMonsterAvailableAtQuestLevel = villageMonsterAvailableAtQuestLevel;
        return this;
    }

    public boolean isNeedBothOnlineAndOffLineQuest() {
        return needBothOnlineAndOffLineQuest;
    }

    public Equipment setNeedBothOnlineAndOffLineQuest(boolean needBothOnlineAndOffLineQuest) {
        this.needBothOnlineAndOffLineQuest = needBothOnlineAndOffLineQuest;
        return this;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public Equipment setBaseDefense(int baseDefense) {
        this.baseDefense = baseDefense;
        return this;
    }

    public int getMaxDefense() {
        return maxDefense;
    }

    public Equipment setMaxDefense(int maxDefense) {
        this.maxDefense = maxDefense;
        return this;
    }

    public List<Resistance> getResistances() {
        return resistances;
    }

    public Equipment setResistances(List<Resistance> resistances) {
        this.resistances = resistances;
        return this;
    }

    public Equipment setArmorSkills(Set<ArmorSkill> armorSkills) {
        this.armorSkills = armorSkills;
        return this;
    }

    public Equipment setItemParts(Set<ItemPart> itemParts) {
        this.itemParts = itemParts;
        return this;
    }

    public Equipment setTorsoUp(boolean torsoUp) {
        isTorsoUp = torsoUp;
        return this;
    }

    public Set<ArmorSkill> getArmorSkills() {
        return armorSkills;
    }

    public Set<ItemPart> getItemParts() {
        return itemParts;
    }

    public boolean isAvailable(){
        return onlineMonsterAvailableAtQuestLevel != NOT_AVAILABLE || villageMonsterAvailableAtQuestLevel != NOT_AVAILABLE;
    }

    public Map<Decoration, Integer> getDecorations() {
        return decorations;
    }

    public boolean isTorsoUp() {
        return isTorsoUp;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public Equipment setEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
        return this;
    }

    public boolean isCanBeSubstitutedForAnyOtherEquipment() {
        return canBeSubstitutedForAnyOtherEquipment;
    }

    public void setCanBeSubstitutedForAnyOtherEquipment(boolean canBeSubstitutedForAnyOtherEquipment) {
        this.canBeSubstitutedForAnyOtherEquipment = canBeSubstitutedForAnyOtherEquipment;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Equipment)) {
            return false;
        }

        Equipment equipment = (Equipment) o;

        if (rarity != equipment.rarity) {
            return false;
        }
        if (slots != equipment.slots) {
            return false;
        }
        if (onlineMonsterAvailableAtQuestLevel != equipment.onlineMonsterAvailableAtQuestLevel) {
            return false;
        }
        if (villageMonsterAvailableAtQuestLevel != equipment.villageMonsterAvailableAtQuestLevel) {
            return false;
        }
        if (needBothOnlineAndOffLineQuest != equipment.needBothOnlineAndOffLineQuest) {
            return false;
        }
        if (baseDefense != equipment.baseDefense) {
            return false;
        }
        if (maxDefense != equipment.maxDefense) {
            return false;
        }
        if (!name.equals(equipment.name)) {
            return false;
        }
        if (gender != equipment.gender) {
            return false;
        }
        if (classType != equipment.classType) {
            return false;
        }
        if (!resistances.equals(equipment.resistances)) {
            return false;
        }
        if (!armorSkills.equals(equipment.armorSkills)) {
            return false;
        }
        return itemParts.equals(equipment.itemParts);
    }

    @Override public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + gender.hashCode();
        result = 31 * result + classType.hashCode();
        result = 31 * result + rarity;
        result = 31 * result + slots;
        result = 31 * result + onlineMonsterAvailableAtQuestLevel;
        result = 31 * result + villageMonsterAvailableAtQuestLevel;
        result = 31 * result + (needBothOnlineAndOffLineQuest ? 1 : 0);
        result = 31 * result + baseDefense;
        result = 31 * result + maxDefense;
        result = 31 * result + resistances.hashCode();
        result = 31 * result + armorSkills.hashCode();
        result = 31 * result + itemParts.hashCode();
        return result;
    }

}
