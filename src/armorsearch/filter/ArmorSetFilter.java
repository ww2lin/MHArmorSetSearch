package armorsearch.filter;

import java.util.List;
import models.GeneratedArmorSet;

/**
 * filter for a generated armor set
 */
public interface ArmorSetFilter {
    List<GeneratedArmorSet> filterArmorSet(List<GeneratedArmorSet> equipmentList);
}
