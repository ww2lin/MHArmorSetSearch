package armorsearch;

import armorsearch.filter.ArmorFilter;
import armorsearch.filter.ArmorSetFilter;
import interfaces.OnSearchResultProgress;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.ClassType;
import models.Decoration;
import models.Equipment;
import models.EquipmentType;
import models.Gender;
import models.UniquelyGeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;
import models.skillactivation.SkillActivationRequirement;
import utils.CsvReader;

public class ArmorSearchWrapper {

    private static final String FILE_PATH_HEAD_EQUIPMENT = "data/MH_EQUIP_HEAD.csv";
    private static final String FILE_PATH_BODY_EQUIPMENT = "data/MH_EQUIP_BODY.csv";
    private static final String FILE_PATH_ARM_EQUIPMENT = "data/MH_EQUIP_ARM.csv";
    private static final String FILE_PATH_WST_EQUIPMENT = "data/MH_EQUIP_WST.csv";
    private static final String FILE_PATH_LEG_EQUIPMENT = "data/MH_EQUIP_LEG.csv";
    private static final String FILE_PATH_SKILL_ACTIVATION = "data/MH_SKILL.csv";
    private static final String FILE_PATH_DECORATION = "data/MH_DECO.csv";

    private AllEquipments allEquipments;
    private Map<String, List<SkillActivationRequirement>> skillActivationChartMap;
    private Map<String, List<Decoration>> decorationLookupTable;
    private SkillActivationChart skillActivationChart;
    private ArmorSkillCacheTable armorSkillCacheTable;

    private List<SkillActivationRequirement> skillList;

    private Gender gender;
    private ClassType classType;
    private List<ArmorFilter> armorFilters;

    public ArmorSearchWrapper(ClassType classType, Gender gender, List<ArmorFilter> armorFilters) throws IOException {
        // Parse CSV
        List<Equipment> headEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_HEAD_EQUIPMENT, EquipmentType.HEAD);
        List<Equipment> bodyEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_BODY_EQUIPMENT, EquipmentType.BODY);
        List<Equipment> armEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_ARM_EQUIPMENT, EquipmentType.ARM);
        List<Equipment> wstEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_WST_EQUIPMENT, EquipmentType.WST);
        List<Equipment> legEquipments = CsvReader.getEquipmentFromCsvFile(FILE_PATH_LEG_EQUIPMENT, EquipmentType.LEG);

        allEquipments = new AllEquipments(headEquipments, bodyEquipments, armEquipments, wstEquipments, legEquipments);

        skillActivationChartMap = CsvReader.getSkillActivationRequirementFromCsvFile(FILE_PATH_SKILL_ACTIVATION);
        decorationLookupTable = CsvReader.getDecorationFromCsvFile(FILE_PATH_DECORATION);

        this.gender = gender;
        this.classType = classType;
        this.armorFilters = armorFilters;

        refreshSkillList();
    }

    public void refreshSkillList() {
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

    public List<SkillActivationRequirement> getPositiveSkillList(){
        return skillList.stream().filter(sar -> sar.getPointsNeededToActivate() > 0).collect(Collectors.toList());
    }

    public List<UniquelyGeneratedArmorSet> search(List<ArmorSetFilter> armorSetFilters, List<SkillActivationRequirement> desiredSkills, final int uniqueSetSearchLimit, final int decorationSearchLimit, OnSearchResultProgress onSearchResultProgress){
        ArmorSearch armorSearch  = new ArmorSearch(armorSkillCacheTable,
                                                   decorationLookupTable,
                                                   armorSetFilters,
                                                   uniqueSetSearchLimit,
                                                   decorationSearchLimit,
                                                   skillActivationChart,
                                                   onSearchResultProgress);
        List<ActivatedSkill> activatedSkills = new ArrayList<>(desiredSkills.size());
        desiredSkills.forEach(skillActivationRequirement -> {
            activatedSkills.add(new ActivatedSkill(skillActivationRequirement));
        });
        return armorSearch.findArmorSetWith(activatedSkills);
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public void setArmorFilters(List<ArmorFilter> armorFilters) {
        this.armorFilters = armorFilters;
    }
}
