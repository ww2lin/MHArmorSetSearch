package armorsearch.filter;

import java.util.List;
import models.Equipment;

/**
 * Filter for individual pieces.
 */
public interface ArmorFilter {
     List<Equipment> filter(List<Equipment> equipmentList);
}
