package models;

import java.util.List;
import java.util.Set;

public class Decoration {
    String name;
    int rarity;
    int slotsNeeded;

    // 99 means you cant get it...yet?
    private int onlineMonsterAvailableAtQuestLevel;
    private int villageMonsterAvailableAtQuestLevel;

    private boolean needBothOnlineAndOffLineQuest;

    Set<ArmorSkill> armorSkills;

    List<List<ItemPart>> itemParts;


    private Decoration(){}

    public static Decoration Builder(){
        return new Decoration();
    }

    public String getName() {
        return name;
    }

    public Decoration setName(String name) {
        this.name = name;
        return this;
    }

    public int getRarity() {
        return rarity;
    }

    public Decoration setRarity(int rarity) {
        this.rarity = rarity;
        return this;
    }

    public int getSlotsNeeded() {
        return slotsNeeded;
    }

    public Decoration setSlotsNeeded(int slotsNeeded) {
        this.slotsNeeded = slotsNeeded;
        return this;
    }

    public int getOnlineMonsterAvailableAtQuestLevel() {
        return onlineMonsterAvailableAtQuestLevel;
    }

    public Decoration setOnlineMonsterAvailableAtQuestLevel(int onlineMonsterAvailableAtQuestLevel) {
        this.onlineMonsterAvailableAtQuestLevel = onlineMonsterAvailableAtQuestLevel;
        return this;
    }

    public int getVillageMonsterAvailableAtQuestLevel() {
        return villageMonsterAvailableAtQuestLevel;
    }

    public Decoration setVillageMonsterAvailableAtQuestLevel(int villageMonsterAvailableAtQuestLevel) {
        this.villageMonsterAvailableAtQuestLevel = villageMonsterAvailableAtQuestLevel;
        return this;
    }

    public boolean isNeedBothOnlineAndOffLineQuest() {
        return needBothOnlineAndOffLineQuest;
    }

    public Decoration setNeedBothOnlineAndOffLineQuest(boolean needBothOnlineAndOffLineQuest) {
        this.needBothOnlineAndOffLineQuest = needBothOnlineAndOffLineQuest;
        return this;
    }

    public Set<ArmorSkill> getArmorSkills() {
        return armorSkills;
    }

    public Decoration setArmorSkills(Set<ArmorSkill> armorSkills) {
        this.armorSkills = armorSkills;
        return this;
    }

    public List<List<ItemPart>> getItemParts() {
        return itemParts;
    }

    public Decoration setItemParts(List<List<ItemPart>> itemParts) {
        this.itemParts = itemParts;
        return this;
    }

    @Override public String toString() {
        return "Decoration{" +
            "name='" + name + '\'' +
            ", rarity=" + rarity +
            ", slotsNeeded=" + slotsNeeded +
            ", onlineMonsterAvailableAtQuestLevel=" + onlineMonsterAvailableAtQuestLevel +
            ", villageMonsterAvailableAtQuestLevel=" + villageMonsterAvailableAtQuestLevel +
            ", needBothOnlineAndOffLineQuest=" + needBothOnlineAndOffLineQuest +
            ", armorSkills=" + armorSkills +
            ", itemParts=" + itemParts +
            '}';
    }
}
