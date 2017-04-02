package models;

import java.util.List;
import java.util.Set;

public class Decoration {
    private static final int NOT_AVAILABLE = 99;

    String name;
    int rarity;
    int slotsNeeded;

    // 99 means you cant get it
    private int onlineMonsterAvailableAtQuestLevel;
    private int villageMonsterAvailableAtQuestLevel;

    private boolean needBothOnlineAndOffLineQuest;

    Set<ArmorSkill> armorSkills;

    List<List<ItemPart>> itemParts;


    private Decoration(){}

    public boolean isAvailable(){
        return onlineMonsterAvailableAtQuestLevel != NOT_AVAILABLE || villageMonsterAvailableAtQuestLevel != NOT_AVAILABLE;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Decoration)) {
            return false;
        }

        Decoration that = (Decoration) o;

        if (rarity != that.rarity) {
            return false;
        }
        if (slotsNeeded != that.slotsNeeded) {
            return false;
        }
        if (onlineMonsterAvailableAtQuestLevel != that.onlineMonsterAvailableAtQuestLevel) {
            return false;
        }
        if (villageMonsterAvailableAtQuestLevel != that.villageMonsterAvailableAtQuestLevel) {
            return false;
        }
        if (needBothOnlineAndOffLineQuest != that.needBothOnlineAndOffLineQuest) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }
        if (!armorSkills.equals(that.armorSkills)) {
            return false;
        }
        return itemParts.equals(that.itemParts);
    }

    @Override public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + rarity;
        result = 31 * result + slotsNeeded;
        result = 31 * result + onlineMonsterAvailableAtQuestLevel;
        result = 31 * result + villageMonsterAvailableAtQuestLevel;
        result = 31 * result + (needBothOnlineAndOffLineQuest ? 1 : 0);
        result = 31 * result + armorSkills.hashCode();
        result = 31 * result + itemParts.hashCode();
        return result;
    }

    public boolean isPositive(String kind){
        for(ArmorSkill armorSkill : armorSkills) {
            if (armorSkill.isKind(kind) && armorSkill.points < 0) {
                return false;
            }
        }
        return true;
    }
}
