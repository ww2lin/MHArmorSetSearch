package models.skillactivation;

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

    public ActivatedSkill(SkillActivationRequirement skillActivationRequirement) {
        this.name = skillActivationRequirement.getName();
        this.kind = skillActivationRequirement.getKind();
        this.pointsNeededToActivate = skillActivationRequirement.getPointsNeededToActivate();
        this.accumulatedPoints =  0;
    }

    public ActivatedSkill(String name, String kind, int pointsNeededToActivate) {
        this.name = name;
        this.kind = kind;
        this.pointsNeededToActivate = pointsNeededToActivate;
    }

    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActivatedSkill)) {
            return false;
        }

        ActivatedSkill that = (ActivatedSkill) o;
        return kind.equals(that.kind);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + kind.hashCode();
        return result;
    }
}
