package armorsearch.thread;

import armorsearch.filter.ArmorSetFilter;
import interfaces.ArmorSearchWorkerProgress;
import java.util.List;

public class ArmorSearchWorkerThread extends Thread {

    private int id;
    private ArmorSearchWorkerProgress armorSearchWorkerProgress;
    private List<ArmorSetFilter> armorSetFilters;

    public ArmorSearchWorkerThread(int id,
                                   ArmorSearchWorkerProgress armorSearchWorkerProgress,
                                   List<ArmorSetFilter> armorSetFilters) {
        this.id = id;
        this.armorSearchWorkerProgress = armorSearchWorkerProgress;
        this.armorSetFilters = armorSetFilters;
    }

    @Override
    public void run() {
    }



}
