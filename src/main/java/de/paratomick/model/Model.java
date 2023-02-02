package de.paratomick.model;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.When;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class Model extends Task<Void> {

    StringProperty currentZone;
    StringProperty currentMap;

    ObservableMap<Integer, Unit> unitMap;
    ObservableMap<Integer, Ability> ability_info;
    ObservableList<Unit> allUnits;
    FilteredList<Unit> visibleUnits;
    SortedList<Unit> visibleSortedUnits;

    public DPS dps;

    private String path = "C:/Users/maxwi/Documents/Elder Scrolls Online/live/Logs/Encounter.log";
    private boolean running;
    private boolean inCombat;

    LongProperty lastTimecode;
    LongProperty estimatedTimcode;
    LongProperty startCombatTimecode;
    LongProperty lastCombatTimecode;
    LongProperty estimatedCombatTimecode;

    NumberBinding dpsMultiplier;

    LongProperty currentTime;
    LongProperty lastTime;
    LongProperty timeSinceLastUpdate;
    AnimationTimer animationTimer;

    final String[] relequenTrackNames = {
            "Havocrel Annihilator", "Fire Behemoth",
            "Flame-Herald Bahsei's Flesh Abomination", "Volatile Shell",
            "Target Iron Atronach, Trial", "Target Harrowing Reaper, Trial"
    };
    List<String> relequenTrackList;

    ObservableList<Unit> relequenList;

    public void init() {
        currentZone = new SimpleStringProperty("NONE");
        currentMap = new SimpleStringProperty("NONE");

        relequenTrackList = Arrays.asList(relequenTrackNames);
        relequenList = FXCollections.observableArrayList();

        unitMap = FXCollections.observableHashMap();
        ability_info = FXCollections.observableHashMap();
        allUnits = FXCollections.observableArrayList(unit -> new Observable[]{unit.inGroupProperty(), unit.maxHealthProperty()});
        visibleUnits = new FilteredList<>(allUnits);
        visibleSortedUnits = new SortedList<>(visibleUnits);

        Predicate<Unit> predicate_logger = Unit::isLogger;
        Predicate<Unit> predicate_boss = unit -> unit.maxHealthProperty().get() > 1000000
                && unit.type.getValue() == Unit.UnitType.MONSTER;//Unit::isBoss;
        Predicate<Unit> predicate_isInGroup = unit -> unit.inGroup.get();
        Predicate<Unit> predicate_tank = unit -> unit.maxHealthProperty().get() > 30000;
        visibleUnits.setPredicate((predicate_isInGroup.and(predicate_tank)));

        Comparator<Unit> comparator_groupID = (o1, o2) -> o2.characterName.get().compareTo(o1.characterName.get());
        visibleSortedUnits.setComparator(comparator_groupID);

        lastTime = new SimpleLongProperty();
        lastTimecode = new SimpleLongProperty();
        startCombatTimecode = new SimpleLongProperty();
        lastCombatTimecode = new SimpleLongProperty();
        estimatedCombatTimecode = new SimpleLongProperty();
        estimatedTimcode = new SimpleLongProperty();
        currentTime = new SimpleLongProperty();
        timeSinceLastUpdate = new SimpleLongProperty(0);
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                currentTime.set(System.nanoTime());
            }
        };
        timeSinceLastUpdate.bind(currentTime.subtract(lastTime));
        estimatedTimcode.bind(timeSinceLastUpdate.divide(1e6).add(lastTimecode));
        estimatedCombatTimecode.bind(lastCombatTimecode.subtract(startCombatTimecode));
        dpsMultiplier = new SimpleDoubleProperty(1000).divide(
                new When(estimatedCombatTimecode.isEqualTo(0))
                .then(1)
                .otherwise(estimatedCombatTimecode));

        animationTimer.start();

        dps = new DPS();

        running = true;
        inCombat = false;
    }

    public void setFilePath(String path) {
        this.path = path;
    }

    @Override
    public Void call() {
        File fileToWatch = new File(path);

        try (BufferedReader br = new BufferedReader(new FileReader(fileToWatch))) {

            //long finalStartTime[] = {0};

            Runnable action = () -> {
                boolean hasComputedSomething = false;
                try {
                    String line;
                    while ((line = br.readLine()) != null) {
                    //while (((System.nanoTime() - finalStartTime[0]) / 1e5 > lastTimecode.get()) && (line = br.readLine()) != null) {
                        compute(line);
                        hasComputedSomething = true;
                    }
                    if (hasComputedSomething) {
                        lastTime.set(System.nanoTime());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            Platform.runLater(() -> lastTime.set(System.nanoTime()));
            Thread.sleep(100);

            //finalStartTime[0] = System.nanoTime();

            while (running) {
                Platform.runLater(action);
                Thread.sleep(1);
            }
            Platform.runLater(() -> {
                animationTimer.stop();
                unitMap.clear();
                allUnits.clear();
                ability_info.clear();
                relequenList.clear();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void compute(String line) {
        String[] data = line.split(",");
        lastTimecode.set(Long.parseLong(data[0]));
        if(inCombat) {
            lastCombatTimecode.set(lastTimecode.get());
        }
        //System.out.println(line);
        switch (data[1]) {
            case "BEGIN_LOG", "END_LOG" -> {
                unitMap.clear();
                allUnits.clear();
                ability_info.clear();
                relequenList.clear();
            }
            case "BEGIN_COMBAT" -> {
                // 255473,BEGIN_COMBAT
                allUnits.forEach(Unit::resetStats);
                relequenList.clear();
                startCombatTimecode.set(lastTimecode.get());
                inCombat = true;
                dps.init();
            }
            case "END_COMBAT" -> {
                // 285106,END_COMBAT
                inCombat = false;
            }
            case "TRIAL_INIT" -> {
                // 4,TRIAL_INIT,15,F,F,0,0,F,0
            }
            case "BEGIN_TRIAL" -> {
                // 244643,BEGIN_TRIAL,15,1654106421687
            }
            case "END_TRIAL" -> {
                // 7383680,END_TRIAL,15,7139188,T,99760,0
            }
            case "ZONE_CHANGED" -> {
                // 0,1           ,2   ,3                          ,4
                // 4,ZONE_CHANGED,1265,"Shalidor's Shrouded Realm",NONE
                // 4,ZONE_CHANGED,1263,"Rockgrove",VETERAN
                if (data[3].equals("\"\"")) {
                    currentMap.set(data[3]);
                } else {
                    currentZone.set(data[3].split("\"")[1]);
                }
            }
            case "MAP_CHANGED" -> {
                // 0,1          ,2   ,3                          ,4
                // 4,MAP_CHANGED,1955,"Shalidor's Shrouded Realm","housing/shroudedrealmext_base"
                if (data[3].equals("\"\"")) {
                    currentMap.set(data[3]);
                } else {
                    currentMap.set(data[3].split("\"")[1]);
                }
            }
            case "UNIT_ADDED" -> {
                Unit unit = new Unit(line, data);
                if (unitMap.containsKey(unit.getId())) {
                    Unit entityToDelete = unitMap.get(Integer.parseInt(data[2]));
                    allUnits.remove(entityToDelete);
                }
                unitMap.put(unit.getId(), unit);
                allUnits.add(unit);
            }
            case "PLAYER_INFO" -> {
                // 7183746,PLAYER_INFO,1,
                //      [142210,142079,84672,55676,55386,45596,45573,45572,45565,45564,45562,45561,45559,45557,45549,45309,45200,45199,45198,45196,45195,45190,45188,45181,45176,45172,45165,40393,39248,33293,63601,107202,86673,13975,150054,147226,155149,58955,15594,47367,23316,24636,99787,45444,45430,45443,45446],
                //      [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
                //      [[HEAD,94740,T,16,ARMOR_INFUSED,LEGENDARY,266,STAMINA,T,16,LEGENDARY],[NECK,138147,T,16,JEWELRY_BLOODTHIRSTY,LEGENDARY,393,INCREASE_PHYSICAL_DAMAGE,T,16,LEGENDARY],[CHEST,170419,T,16,ARMOR_DIVINES,LEGENDARY,570,STAMINA,T,16,LEGENDARY],[SHOULDERS,138186,T,16,ARMOR_DIVINES,LEGENDARY,393,STAMINA,T,16,LEGENDARY],[MAIN_HAND,170412,T,16,WEAPON_PRECISE,LEGENDARY,570,POISONED_WEAPON,T,16,LEGENDARY],[OFF_HAND,170412,T,16,WEAPON_CHARGED,LEGENDARY,570,FIERY_WEAPON,T,16,LEGENDARY],[WAIST,170425,T,16,ARMOR_DIVINES,LEGENDARY,570,STAMINA,T,16,LEGENDARY],[LEGS,175524,T,16,ARMOR_DIVINES,LEGENDARY,594,STAMINA,T,16,LEGENDARY],[FEET,138162,T,16,ARMOR_DIVINES,LEGENDARY,393,STAMINA,T,16,LEGENDARY],[RING1,138146,T,16,JEWELRY_BLOODTHIRSTY,LEGENDARY,393,INCREASE_PHYSICAL_DAMAGE,T,16,LEGENDARY],[RING2,138146,T,16,JEWELRY_BLOODTHIRSTY,LEGENDARY,393,INCREASE_PHYSICAL_DAMAGE,T,16,LEGENDARY],[HAND,170421,T,16,ARMOR_DIVINES,LEGENDARY,570,STAMINA,T,16,LEGENDARY],[BACKUP_MAIN,166196,T,16,WEAPON_INFUSED,LEGENDARY,522,BERSERKER,T,16,LEGENDARY]],
                //      [77182,24328,46331,24165,77140,40161],[38788,23231,40382,77182,77140,23492]
            }
            case "UNIT_CHANGED" -> {
                try {
                    unitMap.get(Integer.parseInt(data[2])).unitChanged(data);
                } catch (NullPointerException ignored) {
                }
            }
            case "UNIT_REMOVED" -> {
                // 34034,UNIT_REMOVED,12
                Unit unit = unitMap.get(Integer.parseInt(data[2]));
                unitMap.remove(Integer.parseInt(data[2]));
                allUnits.remove(unit);
                relequenList.remove(unit);
            }
            case "ABILITY_INFO" -> {
                Ability ability = new Ability(data);
                ability_info.put(ability.id, ability);
                if (ability.effectType == Ability.EffectType.UNKNOWN) {
                    System.out.println(line);
                }
            }
            case "EFFECT_INFO" -> {
                Ability ability = ability_info.get(Integer.parseInt(data[2]));
                if (ability != null) {
                    ability.setEffect(data);
                    if (ability.varE4 == Ability.DamageType.UNKNOWN) {
                        System.out.println(line);
                    }
                }
            }
            case "EFFECT_CHANGED" -> {
                // 0    ,1             ,2     ,3,4      ,5     ,6        ,7                ,8  ,9  ,10 ,11 ,12    ,13    ,14    ,15    ,16    ,17         ,18         ,19         ,20     ,21      ,22,23    ,24    ,25
                // TIME ,EVENT_TYPE    ,      , ,       ,EFFECT,ENTITY   ,HEALTH           ,   ,   ,   ,   ,ABSORB,POS   ,POS   ,POS   ,ENTITY,HEALTH     ,MAGICKA    ,STAMINA    ,ULT    ,        ,  ,POS   ,POS   ,POS
                // 30188,EFFECT_CHANGED,GAINED,1,6139289,120017,268435464,21002944/21002944,0/0,0/0,0/0,0/0,0     ,0.5541,0.5928,4.7291,1     ,18960/18960,20624/20624,13520/13520,500/500,554/1000,0 ,0.5744,0.6020,1.1484
                try {
                    updateEntity(data[6], data[7], data[8], data[9], data[10], data[12]);
                    if (!data[16].equals("*")) {
                        updateEntity(data[16], data[17], data[18], data[19], data[20], data[22]);
                        if (getAbility(data[5]).isRelequen() && getUnit(data[6]).isLogger()) {
                            //System.out.printf("%s,%s,%s from %s to %s\n", data[1], data[2], data[3], getUnit(data[6]).getName(), getUnit(data[16]).getName());
                            //System.out.println(" - " + line);
                            var target = getUnit(data[16]);
                            if (target.isBoss() || relequenTrackList.contains(target.getName())) {
                                if (data[2].equals("FADED")) {
                                    target.setRelequen(0);
                                } else {
                                    target.setRelequen(Integer.parseInt(data[3]), Long.parseLong(data[0]));
                                    if (!relequenList.contains(target)) {
                                        relequenList.add(target);
                                    }
                                }
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ignore) {
                }
            }
            case "BEGIN_CAST" -> {
                // 0    ,1         ,2,3,4      ,5     ,6     ,7          ,8          ,9          ,10     ,11      ,12,13    ,14    ,15    ,16       ,17               ,18     ,19     ,20 ,21 ,22,23    ,24    ,25
                // TIME ,EVENT_TYPE, , ,       ,EFFECT,ENTITY,HEALTH     ,MAGICKA    ,STAMINA    ,ULT    ,        ,  ,POS   ,POS   ,POS   ,ENTITY   ,HEALTH           ,MAGICKA,STAMINA,ULT,   ,  ,POS   ,POS   ,POS
                // 30138,BEGIN_CAST,0,F,6140184,23604 ,1     ,18960/18960,20624/20624,13520/13520,500/500,554/1000,0 ,0.5758,0.6025,1.1484,268435464,21002944/21002944,0/0    ,0/0    ,0/0,0/0,0 ,0.5541,0.5928,4.7291
                try {
                    updateEntity(data[6], data[7], data[8], data[9], data[10], data[12]);
                    if (!data[16].equals("*")) {
                        updateEntity(data[16], data[17], data[18], data[19], data[20], data[22]);
                    }
                } catch (ArrayIndexOutOfBoundsException ignore) {
                }
            }
            case "END_CAST" -> {
                // 30138,END_CAST,COMPLETED,6140184,23604
            }
            case "COMBAT_EVENT" -> {
                // 0    ,1           ,2        ,3       ,4      ,5   ,6,7       ,8    ,9  ,10         ,11         ,12         ,13     ,14      ,15   ,16    ,17    ,18    ,19       ,20                ,21 ,22 ,23 ,24 ,25,26    ,27    ,28
                //  30190,COMBAT_EVENT,  DAMAGE,PHYSICAL,MAGICKA,1560,0, 6140184, 23604, 1,18960/18960,20624/20624,13520/13520,500/500,554/1000,    0,0.5744,0.6020,1.1484,268435464, 21001384/21002944,0/0,0/0,0/0,0/0, 0,0.5541,0.5928,4.7291
                // 594180,COMBAT_EVENT,DOT_TICK,PHYSICAL,MAGICKA,5453,0,28544076,107203,49,26625/26625,14273/14273, 5831/36097, 33/500,919/1000,48069,0.3876,0.4782,2.0738,     1784,97423931/139717184,0/0,0/0,0/0,0/0, 0,0.3774,0.4827,1.9512
                try {
                    doDamage(data[9], data[5], data[19]);
                    updateEntity(data[9], data[10], data[11], data[12], data[13], data[15]);
                    if (!data[19].equals("*")) {
                        updateEntity(data[19], data[20], data[21], data[22], data[23], data[25]);
                    }
                } catch (ArrayIndexOutOfBoundsException ignore) {
                }
            }
            case "HEALTH_REGEN" -> {
                // 0   ,1           ,2  ,3 ,4          ,5          6,          ,7      ,8     ,9,10    ,11    ,12
                // 3529,HEALTH_REGEN,692,46,41737/41737,18482/21735,20805/25322,210/500,0/1000,0,0.5651,0.5293,1.1119
                try {
                    updateEntity(data[3], data[4], data[5], data[6], data[7], data[9]);
                } catch (ArrayIndexOutOfBoundsException ignore) {
                }
            }
            default -> System.out.println(line);
        }
    }

    private void updateEntity(String id, String health, String stamina, String magicka, String ult, String absorb) {
        try {
            unitMap.get(Integer.parseInt(id)).readStats(health, stamina, magicka, ult, absorb);
        } catch (NullPointerException ignored) {
        }
    }

    private void doDamage(String id, String damage, String targetID) {
        try {
            boolean isBoss = false;
            if(!targetID.equals("*")) {
                Unit target = unitMap.get(Integer.parseInt(targetID));
                isBoss = target.isBoss();
            }
            Unit unit = unitMap.get(Integer.parseInt(id));
            unit.readDamage(damage, isBoss);
            dps.updateDamage(unit);
            if(unit.ownerID != 0) {
                Unit owner = unitMap.get(unit.ownerID);
                owner.readDamage(damage, isBoss);
                dps.updateDamage(owner);
            }
        } catch (NullPointerException ignored) {
        }
    }

    private Unit getUnit(String data) {
        return unitMap.get(Integer.parseInt(data));
    }

    private Ability getAbility(String data) {
        return ability_info.get(Integer.parseInt(data));
    }

    public StringProperty zoneProperty() {
        return currentZone;
    }

    public StringProperty mapProperty() {
        return currentMap;
    }

    public ObservableList<Unit> getVisibleUnits() {
        return visibleUnits;
    }

    public LongProperty timeSinceLastUpdateProperty() {
        return timeSinceLastUpdate;
    }

    public ObservableList<Unit> getVisibleSortedUnits() {
        return visibleSortedUnits;
    }

    public ObservableList<Unit> getRelequenUnits() {
        return relequenList;
    }

    public LongProperty lastTimeProperty() {
        return lastTime;
    }

    public LongProperty lastTimecodeProperty() {
        return lastTimecode;
    }

    public LongProperty currentTimeProperty() {
        return currentTime;
    }

    public LongProperty estimatedTimcodeProperty() {
        return estimatedTimcode;
    }

    public void stop() {
        running = false;
    }

    public String getFilePath() {
        return path;
    }

    public LongProperty estimatedCombatTimecodeProperty() {
        return estimatedCombatTimecode;
    }

    public NumberBinding dpsMultiplierProperty() {
        return dpsMultiplier;
    }
}
