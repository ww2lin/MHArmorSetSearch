package models.skillactivation;

import java.util.List;
import models.Decoration;

public class ActivatedSkillWithDecoration {
    final List<ActivatedSkill> activatedSkills;
    final List<Decoration> decorations;

    public ActivatedSkillWithDecoration(List<ActivatedSkill> activatedSkills, List<Decoration> decorations) {
        this.activatedSkills = activatedSkills;
        this.decorations = decorations;
    }

    public List<ActivatedSkill> getActivatedSkills() {
        return activatedSkills;
    }

    public List<Decoration> getDecorations() {
        return decorations;
    }

    @Override
    public String toString() {
        return "ActivatedSkillWithDecoration{" +
            "activatedSkills=" + activatedSkills +
            ", decorations=" + decorations +
            '}';
    }
}
