package utils;

import java.util.HashSet;
import java.util.Set;
import models.ArmorSkill;
import models.ClassType;
import models.Equipment;
import models.Gender;
import models.MonsterPart;
import models.Resistance;
import models.ResistanceType;
import models.skillactivation.SkillActivationRequirement;

class CsvToModel {

    public static Equipment csvEquipmentRowToModel(String[] row) {
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

        Set<Resistance> resistances = new HashSet<>();
        resistances.add(new Resistance(ResistanceType.FIRE, tryParseInt(row[10])));
        resistances.add(new Resistance(ResistanceType.WATER, tryParseInt(row[11])));
        resistances.add(new Resistance(ResistanceType.THUNDER, tryParseInt(row[12])));
        resistances.add(new Resistance(ResistanceType.ICE, tryParseInt(row[13])));
        resistances.add(new Resistance(ResistanceType.DRAGON, tryParseInt(row[14])));
        removeEmptyValues(resistances);

        // TODO filter out empty values
        Set<ArmorSkill> armorSkills = new HashSet<>();
        armorSkills.add(ArmorSkill.createArmorSkill(row[15], tryParseInt(row[16])));
        armorSkills.add(ArmorSkill.createArmorSkill(row[17], tryParseInt(row[18])));
        armorSkills.add(ArmorSkill.createArmorSkill(row[19], tryParseInt(row[20])));
        armorSkills.add(ArmorSkill.createArmorSkill(row[21], tryParseInt(row[22])));
        armorSkills.add(ArmorSkill.createArmorSkill(row[23], tryParseInt(row[24])));
        removeEmptyValues(armorSkills);

        Set<MonsterPart> monsterParts = new HashSet<>();
        monsterParts.add(MonsterPart.createMonsterPart(row[25], tryParseInt(row[26])));
        monsterParts.add(MonsterPart.createMonsterPart(row[27], tryParseInt(row[28])));
        monsterParts.add(MonsterPart.createMonsterPart(row[29], tryParseInt(row[30])));
        monsterParts.add(MonsterPart.createMonsterPart(row[31], tryParseInt(row[32])));
        removeEmptyValues(monsterParts);


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
            .setMonsterParts(monsterParts);
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

    private static int tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException nfe) {
            // Log exception.
            return 0;
        }
    }

    private static <T> Set<T> removeEmptyValues(Set<T> set) {
        set.remove("");
        set.remove(null);
        return set;
    }
}
