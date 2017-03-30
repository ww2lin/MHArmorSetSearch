package models.skillactivation;

/**
 * Created by AlexLin on 3/29/17.
 */
public class ActivatedSkill {
    String name;
    String kind;

    private int pointsNeededToActivate;
    private int accumulatedPoints;

    public ActivatedSkill(SkillActivationRequirement skillActivationRequirement, int accumulatedPoints) {
        this.name = skillActivationRequirement.getName();
        this.kind = skillActivationRequirement.getKind();
        this.pointsNeededToActivate = skillActivationRequirement.getPointsNeededToActivate();
        this.accumulatedPoints = accumulatedPoints;
    }

    public ActivatedSkill(String name, String kind, int pointsNeededToActivate) {
        this.name = name;
        this.kind = kind;
        this.pointsNeededToActivate = pointsNeededToActivate;
    }

    public String getKind() {
        return kind;
    }

    public int getPointsNeededToActivate() {
        return pointsNeededToActivate;
    }

    public int getAccumulatedPoints() {
        return accumulatedPoints;
    }

    @Override public String toString() {
        return "ActivatedSkill{" +
            "name='" + name + '\'' +
            ", kind='" + kind + '\'' +
            ", pointsNeededToActivate=" + pointsNeededToActivate +
            ", accumulatedPoints=" + accumulatedPoints +
            '}';
    }
}
