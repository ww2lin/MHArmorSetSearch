import com.sun.tools.javac.jvm.Gen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import models.ArmorSkill;
import models.ClassType;
import models.Equipment;
import models.Gender;
import models.GeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationChart;
import models.skillactivation.SkillUtil;

public class ArmorSearch {

    private List<Equipment> headEquipments;
    private List<Equipment> bodyEquipments;
    private List<Equipment> armEquipments;
    private List<Equipment> wstEquipments;
    private List<Equipment> legEquipments;
    private SkillActivationChart skillActivationChart;

    // Build a table from kind -> All equipment has that kind of skill
    // TODO this logic can be moved into the CsvReader when generating the list of equipments
    private Map<String, List<Equipment>> headEquipmentCache = new HashMap<>();
    private Map<String, List<Equipment>> bodyEquipmentCache = new HashMap<>();
    private Map<String, List<Equipment>> armEquipmentCache = new HashMap<>();
    private Map<String, List<Equipment>> wstEquipmentCache = new HashMap<>();
    private Map<String, List<Equipment>> legEquipmentCache = new HashMap<>();

    private ClassType classType;
    private Gender gender;

    public ArmorSearch(List<Equipment> headEquipments, List<Equipment> bodyEquipments, List<Equipment> armEquipments, List<Equipment> wstEquipments, List<Equipment> legEquipments, SkillActivationChart skillActivationChart, ClassType classType, Gender gender) {
        this.headEquipments = headEquipments;
        this.bodyEquipments = bodyEquipments;
        this.armEquipments = armEquipments;
        this.wstEquipments = wstEquipments;
        this.legEquipments = legEquipments;
        this.skillActivationChart = skillActivationChart;

        this.classType = classType;
        this.gender = gender;

        buildLookupTable();
    }

    /**
     * Build a look up table by skill, for faster lookup time. E.g
     * headEquipmentCache: skillKind -> All head armor that has this skill.
     * This construction should be moved into the csv while generating the List of equipments
     */
    private void buildLookupTable() {
        Set<String> skillKinds = skillActivationChart.getSkillKind();
        for (String skillkind : skillKinds) {
            updateCacheBySkillKind(headEquipments, headEquipmentCache, skillkind);
            updateCacheBySkillKind(bodyEquipments, bodyEquipmentCache, skillkind);
            updateCacheBySkillKind(armEquipments, armEquipmentCache, skillkind);
            updateCacheBySkillKind(wstEquipments, wstEquipmentCache, skillkind);
            updateCacheBySkillKind(legEquipments, legEquipmentCache, skillkind);
        }
    }

    private void updateCacheBySkillKind(final List<Equipment> equipmentData, Map<String, List<Equipment>> currentCache, String skillKind){
        List<Equipment> equipmentsByKind = currentCache.get(skillKind);
        if (equipmentsByKind == null){
            equipmentsByKind = new LinkedList<>();
        }
        List<Equipment> filterBySkillKind = getEquipmentBySkillKind(equipmentData, skillKind);
        equipmentsByKind.addAll(filterBySkillKind);

        currentCache.put(skillKind, equipmentsByKind);
    }

    /**
     * @param equipments a list of equipment, head, body...
     * @param skillkind which skill we are trying to search for
     * @return all the equipment that matches the skill kind
     */
    private List<Equipment> getEquipmentBySkillKind(List<Equipment> equipments, String skillkind) {
        return equipments.stream().filter(
            (equipment) -> {
                boolean isArmorAvailable = equipment.isArmorAvailable();
                boolean validGender = (equipment.getGender() == Gender.BOTH || equipment.getGender() == gender);
                boolean validClassType = (equipment.getClassType() == ClassType.ALL || equipment.getClassType() == classType);
                if (validGender && validClassType && isArmorAvailable) {
                    for (ArmorSkill armorSkill : equipment.getArmorSkills()) {
                        //TODO remove negative check?
                        if (armorSkill.isKind(skillkind) && armorSkill.points > 0) {
                            return true;
                        }
                    }
                }
                return false;
            }).collect(Collectors.toList());
    }

    /**
     * run a dfs search for the skill search
     * @param desiredSkills that the user wants to generate
     * @return list of equipment that matches what the user wants
     */
    public List<GeneratedArmorSet> findArmorSetWith(List<ActivatedSkill> desiredSkills) {
        List<Equipment> headWithDesiredArmorSkill = new LinkedList<>();
        List<Equipment> bodyWithDesiredArmorSkill = new LinkedList<>();
        List<Equipment> armWithDesiredArmorSkill = new LinkedList<>();
        List<Equipment> wstWithDesiredArmorSkill = new LinkedList<>();
        List<Equipment> legWithDesiredArmorSkill = new LinkedList<>();

        // pull out all the armor pieces that contains any of skillkind defined by the user
        for (ActivatedSkill activatedSkill : desiredSkills) {
            addToListIfSkillExistInCache(headWithDesiredArmorSkill, headEquipmentCache, activatedSkill);
            addToListIfSkillExistInCache(bodyWithDesiredArmorSkill, bodyEquipmentCache, activatedSkill);
            addToListIfSkillExistInCache(armWithDesiredArmorSkill, armEquipmentCache, activatedSkill);
            addToListIfSkillExistInCache(wstWithDesiredArmorSkill, wstEquipmentCache, activatedSkill);
            addToListIfSkillExistInCache(legWithDesiredArmorSkill, legEquipmentCache, activatedSkill);
        }

        // construct the node structure to use for dfs search
        Node head = new Node(headWithDesiredArmorSkill, null);
        Node body = new Node(bodyWithDesiredArmorSkill, head);
        Node arm = new Node(armWithDesiredArmorSkill, body);
        Node wst = new Node(wstWithDesiredArmorSkill, arm);
        Node leg = new Node(legWithDesiredArmorSkill, wst);

        List<GeneratedArmorSet> matchedSets = new ArrayList<>();
        findArmorRecursively(new LinkedList<>(), leg, matchedSets, classType, desiredSkills);


        return matchedSets;
    }

    private void findArmorRecursively(List<Equipment> currentSet, Node node, List<GeneratedArmorSet> matchedSet, ClassType classType, List<ActivatedSkill> desiredSkills) {
        if (node != null) {
            List<Equipment> equipments = node.armorWithDesiredSkills;
            for (Equipment equipment : equipments) {
                currentSet.add(equipment);
                findArmorRecursively(currentSet, node.next, matchedSet, classType, desiredSkills);

                // back tracking.
                currentSet.remove(equipment);
            }

        } else {
            // we found a potential a full set...

            // check if this set contains the skill desired.
            List<ActivatedSkill> activatedSkills = skillActivationChart.getActiavtedSkill(currentSet, classType);

            if (SkillUtil.containsDesiredSkills(desiredSkills, activatedSkills)) {
                // create a new array reference, so that when back tracking the sets is not modified
                matchedSet.add(new GeneratedArmorSet(activatedSkills, new ArrayList<>(currentSet)));
            }
        }
    }

    private void addToListIfSkillExistInCache(List<Equipment> lst, Map<String, List<Equipment>> cache, ActivatedSkill activatedSkill) {
        List<Equipment> equipments = cache.get(activatedSkill.getKind());
        if (equipments != null && !equipments.isEmpty()) {
            lst.addAll(equipments);
        }
    }

    private class Node {
        List<Equipment> armorWithDesiredSkills;
        Node next;

        public Node(List<Equipment> armorWithDesiredSkills, Node next) {
            this.armorWithDesiredSkills = armorWithDesiredSkills;
            this.next = next;
        }
    }
}
