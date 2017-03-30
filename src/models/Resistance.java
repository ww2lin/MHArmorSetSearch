package models;

/**
 * Created by AlexLin on 3/28/17.
 */
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
