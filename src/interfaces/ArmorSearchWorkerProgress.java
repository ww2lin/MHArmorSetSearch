package interfaces;

import java.util.List;
import models.GeneratedArmorSet;

public interface ArmorSearchWorkerProgress {
    boolean shouldContinueSearching();
    void onProgress(int workerId, GeneratedArmorSet generatedArmorSet, int armorSetsTried);
    void onCompleted(int workerId, List<GeneratedArmorSet> generatedArmorSets);

}
