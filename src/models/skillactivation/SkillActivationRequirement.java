package models.skillactivation;

import models.ClassType;

public class SkillActivationRequirement {
    private String name;
    private String kind;
    private int pointsNeededToActivate;
    private ClassType classType;
    private boolean isNegativeSkill;

    private SkillActivationRequirement(){}

    public static SkillActivationRequirement Builder() {
        return new SkillActivationRequirement();
    }

    public SkillActivationRequirement setName(String name) {
        this.name = name.trim();
        return this;
    }

    public SkillActivationRequirement setKind(String kind) {
        this.kind = kind;
        return this;
    }

    public SkillActivationRequirement setPointsNeededToActivate(int pointsNeededToActivate) {
        this.pointsNeededToActivate = pointsNeededToActivate;
        return this;
    }

    public SkillActivationRequirement setClassType(ClassType classType) {
        this.classType = classType;
        return this;
    }

    public SkillActivationRequirement setIsNegativeSkill(boolean isNegativeSkill) {
        this.isNegativeSkill = isNegativeSkill;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getKind() {
        return kind;
    }

    public int getPointsNeededToActivate() {
        return pointsNeededToActivate;
    }

    public ClassType getClassType() {
        return classType;
    }

    public boolean isNegativeSkill() {
        return isNegativeSkill;
    }

    @Override
    public String toString() {
        return "SkillActivationRequirement{" +
            "name='" + name + '\'' +
            ", kind='" + kind + '\'' +
            ", pointsNeededToActivate=" + pointsNeededToActivate +
            ", classType=" + classType +
            ", isNegativeSkill=" + isNegativeSkill +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SkillActivationRequirement)) {
            return false;
        }

        SkillActivationRequirement that = (SkillActivationRequirement) o;

        if (pointsNeededToActivate != that.pointsNeededToActivate) {
            return false;
        }
        if (isNegativeSkill != that.isNegativeSkill) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }
        if (!kind.equals(that.kind)) {
            return false;
        }
        return classType == that.classType;
    }

    @Override public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + kind.hashCode();
        result = 31 * result + pointsNeededToActivate;
        result = 31 * result + classType.hashCode();
        result = 31 * result + (isNegativeSkill ? 1 : 0);
        return result;
    }
}