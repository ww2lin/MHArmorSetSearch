package models;

/**
 * Created by AlexLin on 3/28/17.
 */
public class ArmorSkill {
    String name;
    int points;

    public ArmorSkill(String name, int points) {
        this.name = name;
        this.points = points;
    }

    @Override
    public String toString() {
        return "ArmorSkill{" +
            "name='" + name + '\'' +
            ", points=" + points +
            '}';
    }
}
