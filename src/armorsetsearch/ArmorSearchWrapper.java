package armorsetsearch;

import armorsetsearch.armorsearch.ArmorSearch;
import armorsetsearch.armorsearch.thread.EquipmentList;
import armorsetsearch.charmsearch.CharmSearch;
import armorsetsearch.decorationsearch.DecorationSearch;
import armorsetsearch.filter.ArmorFilter;
import armorsetsearch.filter.ArmorSetFilter;
import armorsetsearch.skillactivation.SkillUtil;
import interfaces.OnSearchResultProgress;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.CharmData;
import models.ClassType;
import models.Decoration;
import models.Equipment;
import models.EquipmentType;
import models.Gender;
import models.GeneratedArmorSet;
import armorsetsearch.skillactivation.ActivatedSkill;
import armorsetsearch.skillactivation.SkillActivationChart;
import armorsetsearch.skillactivation.SkillActivationRequirement;
import utils.CsvReader;
import utils.StopWatch;

public class ArmorSearchWrapper {

    //TODO extract these into an external file, such that data can be configure without a rebuild.
    private static final String FILE_PATH_HEAD_EQUIPMENT = "data/MH_EQUIP_HEAD.csv";
    private static final String FILE_PATH_BODY_EQUIPMENT = "data/MH_EQUIP_BODY.csv";
    private static final String FILE_PATH_ARM_EQUIPMENT = "data/MH_EQUIP_ARM.csv";
    private static final String FILE_PATH_WST_EQUIPMENT = "data/MH_EQUIP_WST.csv";
    private static final String FILE_PATH_LEG_EQUIPMENT = "data/MH_EQUIP_LEG.csv";
    private static final String FILE_PATH_SKILL_ACTIVATION = "data/MH_SKILL_TRANS.csv";
    private static final String FILE_PATH_DECORATION = "data/MH_DECO.csv";
    private static final String FILE_PATH_CHARM = "data/MH_CHARM_TABLE.csv";

    private AllEquipments allEquipments;
    private Map<String, List<SkillActivationRequirement>> skillActivationChartMap;
    private Map<String, List<Decoration>> decorationLookupTable;
    private Map<String, List<CharmData>> charmLookupTable;
    private SkillActivationChart skillActivationChart;
    private ArmorSkillCacheTable armorSkillCacheTable;

    private List<SkillActivationRequirement> skillList;

    private Gender gender;
    private ClassType classType;
    private List<ArmorFilter> armorFilters;
    private ArmorSearch armorSearch;
    private CharmSearch charmSearch;
    private DecorationSearch decorationSearch;

    private int weapSlot = 0;

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
        charmLookupTable = CsvReader.getCharmFromCsvFile(FILE_PATH_CHARM);
        this.gender = gender;
        this.classType = classType;
        this.armorFilters = armorFilters;

        refreshSkillList();
    }

    public void refreshSkillList() {
        skillActivationChart = new SkillActivationChart(skillActivationChartMap, classType);

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

    public List<GeneratedArmorSet> search(List<ArmorSetFilter> armorSetFilters,
                                          List<SkillActivationRequirement> desiredSkills,
                                          final int uniqueSetSearchLimit,
                                          final int decorationSearchLimit,
                                          OnSearchResultProgress onSearchResultProgress) {
        if (!SkillUtil.shouldDoSearch(desiredSkills)) {
            return Collections.emptyList();
        }

        List<ActivatedSkill> activatedSkills = new ArrayList<>(desiredSkills.size());

        desiredSkills.forEach(skillActivationRequirement -> {
            activatedSkills.add(new ActivatedSkill(skillActivationRequirement));
        });


        float progressChunk = 100 / 3;
        float progress = 0;
        List<GeneratedArmorSet> generatedArmorSets = new ArrayList<>();
        StopWatch stopWatch = new StopWatch();

        System.out.println("Filtering Equipments");
        armorSkillCacheTable = new ArmorSkillCacheTable(activatedSkills, skillActivationChart, allEquipments, armorFilters, classType, gender);
        stopWatch.printMsgAndResetTime("Finished filtering");

        System.out.println("Building Decoration data");
        decorationSearch = new DecorationSearch(progress, progressChunk, uniqueSetSearchLimit, onSearchResultProgress, activatedSkills, decorationLookupTable);
        stopWatch.printMsgAndResetTime("Finished decoration setup");

        progress+=progressChunk;

        System.out.println("Building charm data");
        charmSearch = new CharmSearch(generatedArmorSets, progress, progressChunk, uniqueSetSearchLimit, onSearchResultProgress, charmLookupTable, decorationSearch);
        stopWatch.printMsgAndResetTime("Finished charm setup");

        progress+=progressChunk;

        System.out.println("Building equipment data");
        armorSearch = new ArmorSearch(generatedArmorSets, progress, progressChunk, weapSlot, armorSkillCacheTable, uniqueSetSearchLimit, onSearchResultProgress);
        stopWatch.printMsgAndResetTime("Finished armor setup");

        System.out.println("Starting Armor Search.");
        EquipmentList equipmentList = armorSearch.findArmorSetWith(activatedSkills);
        stopWatch.printMsgAndResetTime("Finished Armor Search");



        System.out.println("Starting Decoration Search.");
        equipmentList = decorationSearch.buildEquipmentWithDecorationSkillTable(equipmentList, activatedSkills);
        stopWatch.printMsgAndResetTime("Finished Decoration Search");

        System.out.println("Starting Charm Search.");
        charmSearch.findAValidCharmWithArmorSkill(equipmentList, activatedSkills, 50);
        stopWatch.printMsgAndResetTime("Finished Charm Search");

        return generatedArmorSets;
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

    public void setWeapSlot(int weapSlot) {
        this.weapSlot = weapSlot;
    }

    public void stopSearching(){

       if (armorSearch != null){
           armorSearch.stop();
       }

       if (decorationSearch != null) {
           decorationSearch.stop();
       }

       if (charmSearch != null) {
           charmSearch.stop();
       }
    }
}
