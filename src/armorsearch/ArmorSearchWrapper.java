package armorsearch;

import armorsearch.filter.ArmorFilter;
import armorsearch.model.AllEquipments;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.ClassType;
import models.Decoration;
import models.Equipment;
import models.Gender;
import models.UniquelyGeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;
import models.skillactivation.SkillActivationRequirement;
import utils.CsvReader;

public class ArmorSearchWrapper {

    private static final String FILE_PATH_HEAD_EQUIPMENT = "data/MHXX_EQUIP_HEAD.csv";
    private static final String FILE_PATH_BODY_EQUIPMENT = "data/MHXX_EQUIP_BODY.csv";
    private static final String FILE_PATH_ARM_EQUIPMENT = "data/MHXX_EQUIP_ARM.csv";
    private static final String FILE_PATH_WST_EQUIPMENT = "data/MHXX_EQUIP_WST.csv";
    private static final String FILE_PATH_LEG_EQUIPMENT = "data/MHXX_EQUIP_LEG.csv";
    private static final String FILE_PATH_SKILL_ACTIVATION = "data/MHXX_SKILL.csv";
    private static final String FILE_PATH_DECORATION = "data/MHXX_DECO.csv";

    private AllEquipments allEquipments;
    private Map<String, List<SkillActivationRequirement>> skillActivationChartMap;
    private Map<String, List<Decoration>> decorationLookupTable;
    private SkillActivationChart skillActivationChart;
    private ArmorSkillCacheTable armorSkillCacheTable;

    private List<SkillActivationRequirement> skillList;



    public ArmorSearchWrapper(ClassType classType, Gender gender, List<ArmorFilter> armorFilters) throws IOException {
        // Parse CSV
        List<Equipment> headEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_HEAD_EQUIPMENT);
        List<Equipment> bodyEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_BODY_EQUIPMENT);
        List<Equipment> armEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_ARM_EQUIPMENT);
        List<Equipment> wstEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_WST_EQUIPMENT);
        List<Equipment> legEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_LEG_EQUIPMENT);

        allEquipments = new AllEquipments(headEquipments, bodyEquipments, armEquipments, wstEquipments, legEquipments);

        skillActivationChartMap = CsvReader.getSkillActivationRequirementFromCsvFile(FILE_PATH_SKILL_ACTIVATION);
        decorationLookupTable = CsvReader.getDecorationFromCsvFile(FILE_PATH_DECORATION);

        initWithClassAndGender(classType, gender, armorFilters);
    }

    public void initWithClassAndGender(ClassType classType, Gender gender, List<ArmorFilter> armorFilters) {
        skillActivationChart = new SkillActivationChart(skillActivationChartMap, classType);

        armorSkillCacheTable = new ArmorSkillCacheTable(skillActivationChart, allEquipments, armorFilters, classType, gender);

        // Skill list to display to the user
        skillList = new ArrayList<>();
        skillActivationChartMap.values().forEach(skillActivationRequirements -> {
            skillList.addAll(skillActivationRequirements);
        });
    }

    public List<SkillActivationRequirement> getSkillList(){
        return skillList;
    }

    public List<UniquelyGeneratedArmorSet> search(final int uniqueSetSearchLimit, final int decorationSearchLimit){
        // individual filters.

        ActivatedSkill[] lookupSkills1 = {new ActivatedSkill("攻撃力UP【小】", "攻撃", 10)};
        ActivatedSkill[] lookupSkills2 = {new ActivatedSkill("攻撃力UP【小】", "攻撃", 10), new ActivatedSkill("ガード性能+1","ガード性能", 10)};
        List<ActivatedSkill> desiredSkills = new ArrayList<>();

        desiredSkills.addAll(Arrays.asList(lookupSkills2));

        List<UniquelyGeneratedArmorSet> matchedSets = new ArmorSearch().findArmorSetWith(decorationLookupTable, uniqueSetSearchLimit, decorationSearchLimit, skillActivationChart, desiredSkills, armorSkillCacheTable);

        return matchedSets;
    }


}
