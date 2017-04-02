package utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import models.ArmorSkill;
import models.ClassType;
import models.Decoration;
import models.Equipment;
import models.EquipmentType;
import models.Gender;
import models.ItemPart;
import models.Resistance;
import models.ResistanceType;
import models.skillactivation.SkillActivationRequirement;

class CsvToModel {

    /**
     * TODO: extract special armors that will be placed in all cache.
     * Such as torso up armors, or 3 slots gears.
     * @param row
     * @return
     */
    public static Equipment csvEquipmentRowToModel(String[] row, EquipmentType equipmentType) {
        String name = row[0];
        Gender gender = Gender.values()[tryParseInt(row[1])];
        ClassType classType = ClassType.values()[tryParseInt(row[2])];
        int rarity = tryParseInt(row[3]);
        int slots = tryParseInt(row[4]);
        int onlineQuestLevelRequirement = tryParseInt(row[5]);
        int villageQuestLevelRequirement = tryParseInt(row[6]);
        boolean needBothOnlineAndOffLineQuest = tryParseInt(row[7]) == 1;
        int baseDefense = tryParseInt(row[8]);
        int maxDefense = tryParseInt(row[9]);

        List<Resistance> resistances = new LinkedList<>();
        resistances.add(new Resistance(ResistanceType.FIRE, tryParseInt(row[10])));
        resistances.add(new Resistance(ResistanceType.WATER, tryParseInt(row[11])));
        resistances.add(new Resistance(ResistanceType.THUNDER, tryParseInt(row[12])));
        resistances.add(new Resistance(ResistanceType.ICE, tryParseInt(row[13])));
        resistances.add(new Resistance(ResistanceType.DRAGON, tryParseInt(row[14])));

        Set<ArmorSkill> armorSkills = new HashSet<>();
        boolean isTorsoUp = false;
        armorSkills.add(ArmorSkill.createArmorSkill(row[15], tryParseInt(row[16])));
        armorSkills.add(ArmorSkill.createArmorSkill(row[17], tryParseInt(row[18])));
        armorSkills.add(ArmorSkill.createArmorSkill(row[19], tryParseInt(row[20])));
        armorSkills.add(ArmorSkill.createArmorSkill(row[21], tryParseInt(row[22])));
        armorSkills.add(ArmorSkill.createArmorSkill(row[23], tryParseInt(row[24])));
        armorSkills = removeEmptyArmorValues(armorSkills);

        for (ArmorSkill armorSkill : armorSkills) {
            if (armorSkill.isTorsoUp()) {
                isTorsoUp = true;
                break;
            }
        }

        Set<ItemPart> itemParts = new HashSet<>();
        itemParts.add(ItemPart.createMonsterPart(row[25], tryParseInt(row[26])));
        itemParts.add(ItemPart.createMonsterPart(row[27], tryParseInt(row[28])));
        itemParts.add(ItemPart.createMonsterPart(row[29], tryParseInt(row[30])));
        itemParts.add(ItemPart.createMonsterPart(row[31], tryParseInt(row[32])));
        itemParts = new HashSet<>(removeEmptyItemValues(itemParts));

        return Equipment.Builder()
            .setName(name)
            .setGender(gender)
            .setClassType(classType)
            .setRarity(rarity)
            .setSlots(slots)
            .setOnlineMonsterAvailableAtQuestLevel(onlineQuestLevelRequirement)
            .setVillageMonsterAvailableAtQuestLevel(villageQuestLevelRequirement)
            .setNeedBothOnlineAndOffLineQuest(needBothOnlineAndOffLineQuest)
            .setBaseDefense(baseDefense)
            .setMaxDefense(maxDefense)
            .setResistances(resistances)
            .setArmorSkills(armorSkills)
            .setItemParts(itemParts)
            .setTorsoUp(isTorsoUp)
            .setEquipmentType(equipmentType);
    }

    public static SkillActivationRequirement csvSkillActivationRequirementRowToModel(String[] row) {
        String name = row[0];
        String kind = row[1];
        Integer pointsToActivate = tryParseInt(row[2]);
        ClassType classType = ClassType.values()[tryParseInt(row[3])];
        return SkillActivationRequirement.Builder()
            .setName(name)
            .setKind(kind)
            .setPointsNeededToActivate(pointsToActivate)
            .setClassType(classType)
            .setIsNegativeSkill(pointsToActivate <= 0);
    }

    public static Decoration csvDecorationRowToModel(String[] row) {
        String name = row[0];
        int rarity = tryParseInt(row[1]);
        int slotsNeeded = tryParseInt(row[2]);
        int onlineQuestLevelRequirement = tryParseInt(row[3]);
        int villageQuestLevelRequirement = tryParseInt(row[4]);
        boolean needBothOnlineAndOffLineQuest = tryParseInt(row[5]) == 1;

        Set<ArmorSkill> armorSkills = new HashSet<>();
        armorSkills.add(ArmorSkill.createArmorSkill(row[6], tryParseInt(row[7])));
        armorSkills.add(ArmorSkill.createArmorSkill(row[8], tryParseInt(row[9])));
        armorSkills = removeEmptyArmorValues(armorSkills);

        List<List<ItemPart>> itemParts = new LinkedList<>();
        List<ItemPart> itemParts1 = new LinkedList<>();
        List<ItemPart> itemParts2 = new LinkedList<>();

        itemParts1.add(ItemPart.createMonsterPart(row[10], tryParseInt(row[11])));
        itemParts1.add(ItemPart.createMonsterPart(row[12], tryParseInt(row[13])));
        itemParts1.add(ItemPart.createMonsterPart(row[14], tryParseInt(row[15])));
        itemParts1.add(ItemPart.createMonsterPart(row[16], tryParseInt(row[17])));
        itemParts1 = removeEmptyItemValues(itemParts1);

        itemParts2.add(ItemPart.createMonsterPart(row[18], tryParseInt(row[19])));
        itemParts2.add(ItemPart.createMonsterPart(row[20], tryParseInt(row[21])));
        itemParts2.add(ItemPart.createMonsterPart(row[22], tryParseInt(row[23])));
        itemParts2.add(ItemPart.createMonsterPart(row[24], tryParseInt(row[25])));
        itemParts2 = removeEmptyItemValues(itemParts2);

        if (!itemParts1.isEmpty()){
            itemParts.add(itemParts1);
        }

        if (!itemParts2.isEmpty()){
            itemParts.add(itemParts2);
        }

        return Decoration.Builder()
            .setName(name)
            .setRarity(rarity)
            .setSlotsNeeded(slotsNeeded)
            .setOnlineMonsterAvailableAtQuestLevel(onlineQuestLevelRequirement)
            .setVillageMonsterAvailableAtQuestLevel(villageQuestLevelRequirement)
            .setNeedBothOnlineAndOffLineQuest(needBothOnlineAndOffLineQuest)
            .setArmorSkills(armorSkills)
            .setItemParts(itemParts);

    }

    private static int tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException nfe) {
            // Log exception.
            return 0;
        }
    }


    private static Set<ArmorSkill> removeEmptyArmorValues(Collection<ArmorSkill> collection) {
        return collection.stream().filter(armorSkill -> armorSkill != null && !armorSkill.isNull()).collect(Collectors.toSet());
    }

    private static List<ItemPart> removeEmptyItemValues(Collection<ItemPart> collection) {
        return collection.stream().filter(itemPart -> itemPart != null && !itemPart.isNull()).collect(Collectors.toList());
    }
}
