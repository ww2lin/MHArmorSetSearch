package utils;

import armorsearch.ArmorSearchWrapper;
import armorsearch.filter.ArmorSetFilter;
import interfaces.OnSearchResultProgress;
import java.util.List;
import models.UniquelyGeneratedArmorSet;
import models.skillactivation.ActivatedSkill;
import models.skillactivation.SkillActivationRequirement;

/**
 * Do the search in a thread, to avoid blocking the UI thread
 */
public class WorkerThread extends Thread {
    private OnSearchResultProgress onSearchResultProgress;
    private ArmorSearchWrapper armorSearchWrapper;
    private List<SkillActivationRequirement> desiredSkills;
    private final int uniqueSetSearchLimit;
    private final int decorationSearchLimit;
    private List<ArmorSetFilter> armorSetFilters;

    public WorkerThread(OnSearchResultProgress onSearchResultProgress, ArmorSearchWrapper armorSearchWrapper, List<SkillActivationRequirement> desiredSkills, int uniqueSetSearchLimit, int decorationSearchLimit, List<ArmorSetFilter> armorSetFilters) {
        this.onSearchResultProgress = onSearchResultProgress;
        this.armorSearchWrapper = armorSearchWrapper;
        this.desiredSkills = desiredSkills;
        this.uniqueSetSearchLimit = uniqueSetSearchLimit;
        this.decorationSearchLimit = decorationSearchLimit;
        this.armorSetFilters = armorSetFilters;
    }

    @Override
    public void run() {
        List<UniquelyGeneratedArmorSet> uniquelyGeneratedArmorSets = armorSearchWrapper.search(armorSetFilters, desiredSkills, uniqueSetSearchLimit, decorationSearchLimit, onSearchResultProgress);
        onSearchResultProgress.onComplete(uniquelyGeneratedArmorSets);

    }
}
