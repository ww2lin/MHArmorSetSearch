package armorsetsearch.skillactivation;

import java.util.List;

public class SkillUtil {

    /** check if the current armor set is a super set of the desired set
     * @param desiredSkills the skill that the user wants
     * @param activatedSkills current generated armor set
     * @return true if current generated armor set matches or has more of the desired skills
     */
    public static boolean containsDesiredSkills(List<ActivatedSkill> desiredSkills, List<ActivatedSkill> activatedSkills) {
        if (activatedSkills.size() < desiredSkills.size()) {
            return false;
        } else {
            // the skill is a match if
            // 1. the skill kind matches
            // 2. skill point is equal or greater to the desired skill
            for (ActivatedSkill desiredSkill : desiredSkills) {
                boolean hasSkill = false;
                for (ActivatedSkill activatedSkill : activatedSkills) {
                    boolean hasEnoughSkillPoints = activatedSkill.getAccumulatedPoints() >= desiredSkill.getPointsNeededToActivate();
                    if (hasEnoughSkillPoints && desiredSkill.kind.equalsIgnoreCase(activatedSkill.kind)) {
                        hasSkill = true;
                    }
                }
                if (!hasSkill) {
                    // dont have one of the desired skills, exit.
                    return false;
                }
            }
            return true;
        }
    }

}
