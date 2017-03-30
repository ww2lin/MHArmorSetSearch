package models;

public class ArmorSkill {
    // Note this is NOT the name of the skill, rather its the 'kind' of the skill
    // E.g its NOT AuS, AuM, or negate stun,
    // it is Attack, Poison, Stun, Hearing
    public String kind;
    public int points;

    private ArmorSkill(String kind, int points) {
        this.kind = kind;
        this.points = points;
    }

    public static ArmorSkill createArmorSkill(String kind, int points) {
        if (kind == null || kind.isEmpty()) {
            return null;
        }
        return new ArmorSkill(kind.trim(), points);
    }

    @Override
    public String toString() {
        return "ArmorSkill{" +
            "kind='" + kind + '\'' +
            ", points=" + points +
            '}';
    }

    public boolean isKind(String kind){
        return this.kind.equalsIgnoreCase(kind);
    }
}
