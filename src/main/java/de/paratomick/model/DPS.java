package de.paratomick.model;

import javafx.beans.binding.When;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;

import java.util.HashMap;

public class DPS {
    //HashMap<Unit, Long> damageDone;
    public LongProperty maximumDamageDone;
    public DoubleProperty maxDmgMult;

    public DPS() {
        //damageDone = new HashMap<>();
        maximumDamageDone = new SimpleLongProperty(1);
        maxDmgMult = new SimpleDoubleProperty(1);
        maxDmgMult.bind(
                new When(maximumDamageDone.isEqualTo(0)).then(0).otherwise(new SimpleDoubleProperty(1).divide(maximumDamageDone))
        );
        init();
    }

    void init() {
        //damageDone.clear();
        maximumDamageDone.set(1);
    }

    void updateDamage(Unit unit) {
        if(unit.damageDone.get() > maximumDamageDone.get()) {
            maximumDamageDone.set(unit.damageDone.get());
        }
    }
}
