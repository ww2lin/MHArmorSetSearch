package interfaces;

import java.util.List;
import models.GeneratedArmorSet;

public interface OnSearchResultProgress {
    void onStart(int max);
    void onProgress(GeneratedArmorSet generatedArmorSet, int current);
    void onComplete(List<GeneratedArmorSet> generatedArmorSets);
}
