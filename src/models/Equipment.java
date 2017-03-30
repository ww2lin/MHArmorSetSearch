package models;

import java.util.Set;

/**
 * Created by AlexLin on 3/28/17.
 */
public class Equipment {
    private String name;
    private Gender gender;
    private ClassType classType;
    private int rarity;
    private int slots;
    // 99 means you cant get it...yet?
    private int onlineMonsterAvailableAtQuestLevel;
    private int villageMonsterAvailableAtQuestLevel;

    // Cant read japanese, guessing this means if you need to do both online/offline quest
    private boolean needBothOnlineAndOffLineQuest;

    private int baseDefense;
    private int maxDefense;

    private Set<Resistance> resistances;

    private Set<ArmorSkill> armorSkills;
    private Set<MonsterPart> monsterParts;

    private Equipment(){}

    public static Equipment Builder(){
        return new Equipment();
    }

    @Override public String toString() {
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
            ", monsterParts=" + monsterParts +
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

    public Set<Resistance> getResistance() {
        return resistances;
    }

    public Equipment setResistances(Set<Resistance> resistances) {
        this.resistances = resistances;
        return this;
    }

    public Equipment setArmorSkills(Set<ArmorSkill> armorSkills) {
        this.armorSkills = armorSkills;
        return this;
    }

    public Equipment setMonsterParts(Set<MonsterPart> monsterParts) {
        this.monsterParts = monsterParts;
        return this;
    }

    public Set<ArmorSkill> getArmorSkills() {
        return armorSkills;
    }

    public Set<MonsterPart> getMonsterParts() {
        return monsterParts;
    }
}
