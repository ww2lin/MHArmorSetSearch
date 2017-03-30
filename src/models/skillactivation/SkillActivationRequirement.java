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
}