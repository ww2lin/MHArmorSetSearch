package models;

/**
 * Created by AlexLin on 3/28/17.
 */
public class MonsterPart {
    String name;
    int amount;

    public MonsterPart(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "MonsterPart{" +
            "name='" + name + '\'' +
            ", amount=" + amount +
            '}';
    }
}
