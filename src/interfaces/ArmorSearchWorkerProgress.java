package interfaces;

import java.util.List;
import models.UniquelyGeneratedArmorSet;

public interface ArmorSearchWorkerProgress {
    boolean shouldContinueSearching();
    void onProgress(int workerId, UniquelyGeneratedArmorSet uniquelyGeneratedArmorSet);
    void onCompleted(int workerId, List<UniquelyGeneratedArmorSet> uniquelyGeneratedArmorSets);

}
