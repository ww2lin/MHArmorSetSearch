package interfaces;

import java.util.List;
import models.UniquelyGeneratedArmorSet;

public interface OnSearchResultProgress {
    void onProgress(UniquelyGeneratedArmorSet uniquelyGeneratedArmorSet, int current, int max);
    void onComplete(List<UniquelyGeneratedArmorSet> uniquelyGeneratedArmorSets);
}
