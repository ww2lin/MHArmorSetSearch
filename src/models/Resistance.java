package models;

public class Resistance {
    ResistanceType resistanceType;
    int value;

    public Resistance(ResistanceType resistanceType, int value) {
        this.resistanceType = resistanceType;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Resistance{" +
            "resistanceType=" + resistanceType +
            ", value=" + value +
            '}';
    }
}
