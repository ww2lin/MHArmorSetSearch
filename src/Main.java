import armorsearch.ArmorSearch;
import armorsearch.armorcache.ArmorSkillCacheTable;
import armorsearch.filter.ArmorFilter;
import armorsearch.model.AllEquipments;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import models.ClassType;
import models.Decoration;
import models.Equipment;
import models.Gender;
import models.GeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.ActivatedSkillWithDecoration;
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
        // Parse CSV
        List<Equipment> headEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_HEAD_EQUIPMENT);
        List<Equipment> bodyEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_BODY_EQUIPMENT);
        List<Equipment> armEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_ARM_EQUIPMENT);
        List<Equipment> wstEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_WST_EQUIPMENT);
        List<Equipment> legEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_LEG_EQUIPMENT);
        Map<String, List<SkillActivationRequirement>> skillActivationChartMap = CsvReader.getSkillActivationRequirementFromCsvFile(FILE_PATH_SKILL_ACTIVATION);
        Map<String, List<Decoration>> decorationLookupTable = CsvReader.getDecorationFromCsvFile(FILE_PATH_DECORATION);

        // init objects
        final int searchLimit = 200;
        Gender gender = Gender.MALE;
        ClassType classType = ClassType.BLADEMASTER;

        SkillActivationChart skillActivationChart = new SkillActivationChart(skillActivationChartMap, classType);
        AllEquipments allEquipments = new AllEquipments(headEquipments, bodyEquipments, armEquipments, wstEquipments, legEquipments);

        // individual filters.
        List<ArmorFilter> armorFilters = new LinkedList<>();
        ArmorSkillCacheTable armorSkillCacheTable = new ArmorSkillCacheTable(skillActivationChart, allEquipments, armorFilters, classType, gender);

        ActivatedSkill[] lookupSkills1 = {new ActivatedSkill("攻撃力UP【小】", "攻撃", 10)};
        ActivatedSkill[] lookupSkills2 = {new ActivatedSkill("攻撃力UP【小】", "攻撃", 10), new ActivatedSkill("ガード性能+1","ガード性能", 10)};
        List<ActivatedSkill> desiredSkills = new ArrayList<>();

        desiredSkills.addAll(Arrays.asList(lookupSkills2));

        List<GeneratedArmorSet> matchedSets = new ArmorSearch().findArmorSetWith(decorationLookupTable, searchLimit, skillActivationChart, desiredSkills, armorSkillCacheTable);

        // Testing purposes.
        //System.out.println(matchedSets.size());
        Collections.reverseOrder(new GeneratedArmorSet.MostSkillComparator());
        for (GeneratedArmorSet generatedArmorSet : matchedSets) {
            for (ActivatedSkillWithDecoration activatedSkillWithDecoration : generatedArmorSet.getActivatedSkills()){
                for (ActivatedSkill activatedSkill : activatedSkillWithDecoration.getActivatedSkills()) {
                    if (activatedSkill.getAccumulatedPoints() > 18) {
                        System.out.println(generatedArmorSet);
                    }
                }
            }
        }
    }


}
