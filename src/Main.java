import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import models.ClassType;
import models.Equipment;
import models.Gender;
import models.GeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;
import models.skillactivation.SkillActivationRequirement;
import utils.CsvReader;

public class Main {

    private static final String FILE_PATH_HEAD_EQUIPMENT = "data/MHXX_EQUIP_HEAD.csv";
    private static final String FILE_PATH_BODY_EQUIPMENT = "data/MHXX_EQUIP_BODY.csv";
    private static final String FILE_PATH_ARM_EQUIPMENT = "data/MHXX_EQUIP_ARM.csv";
    private static final String FILE_PATH_WST_EQUIPMENT = "data/MHXX_EQUIP_WST.csv";
    private static final String FILE_PATH_LEG_EQUIPMENT = "data/MHXX_EQUIP_LEG.csv";
    private static final String FILE_PATH_SKILL_ACTIVATION = "data/MHXX_SKILL.csv";
    private static final String FILE_PATH_DECORATION = "data/MHXX_DECO.csv";

    public static void main(String args[]) throws IOException {
        List<Equipment> headEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_HEAD_EQUIPMENT);
        List<Equipment> bodyEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_BODY_EQUIPMENT);
        List<Equipment> armEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_ARM_EQUIPMENT);
        List<Equipment> wstEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_WST_EQUIPMENT);
        List<Equipment> legEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_LEG_EQUIPMENT);

        Map<String, List<SkillActivationRequirement>> skillActivationChartMap = CsvReader.getSkillActivationRequirementFromCsvFile(FILE_PATH_SKILL_ACTIVATION);
        SkillActivationChart skillActivationChart = new SkillActivationChart(skillActivationChartMap);

        //Map<String, List<Decoration>> decorationLookupTable = CsvReader.getDecorationFromCsvFile(FILE_PATH_DECORATION);

        //System.out.println(decorationLookupTable);

        ArmorSearch armorSearch = new ArmorSearch(headEquipments, bodyEquipments, armEquipments, wstEquipments, legEquipments, skillActivationChart, ClassType.BLADEMASTER, Gender.MALE);

        ActivatedSkill[] lookupSkills1 = {new ActivatedSkill("攻撃力UP【小】", "攻撃", 10)};
        ActivatedSkill[] lookupSkills2 = {new ActivatedSkill("攻撃力UP【小】", "攻撃", 10), new ActivatedSkill("ガード性能+1","ガード性能", 10)};
        List<GeneratedArmorSet> matchedSets = armorSearch.findArmorSetWith(Arrays.asList(lookupSkills2));


        // Testing purposes.
        System.out.println(matchedSets.size());
        Collections.reverseOrder(new GeneratedArmorSet.MostSkillComparator());
        //System.out.println(matchedSets.get(0));
        for (GeneratedArmorSet generatedArmorSet : matchedSets) {
            for (ActivatedSkill activatedSkill : generatedArmorSet.getActivatedSkills()){
                if (activatedSkill.getAccumulatedPoints() > 14) {
                    System.out.println(generatedArmorSet);
                }
            }
        }
    }


}
