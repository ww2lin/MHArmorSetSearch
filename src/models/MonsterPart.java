package models;

public class MonsterPart {
    String name;
    int amount;

    private MonsterPart(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public static MonsterPart createMonsterPart(String name, int amount) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return new MonsterPart(name, amount);
    }

    @Override
    public String toString() {
        return "MonsterPart{" +
            "kind='" + name + '\'' +
            ", amount=" + amount +
            '}';
    }
}
