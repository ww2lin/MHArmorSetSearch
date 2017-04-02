package armorsearch.filter;

import java.util.List;
import models.Equipment;

/**
 * filter for a generated armor set
 */
public interface ArmorSetFilter {
    boolean isArmorValid(List<Equipment> currentSet);
}
