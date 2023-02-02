package de.paratomick.model;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.math.BigInteger;

public class Unit {

    public enum UnitType {
        PLAYER, MONSTER, OBJECT, NO_TYPE;

        public static UnitType getTypeFromString(String type) {
            return switch (type) {
                case "PLAYER" -> UnitType.PLAYER;
                case "MONSTER" -> UnitType.MONSTER;
                case "OBJECT" -> UnitType.OBJECT;
                default -> UnitType.NO_TYPE;
            };
        }
    }

    public enum AlignmentType {
        PLAYER_ALLY, NPC_ALLY, HOSTILE, NO_ALIGNMENT;

        public static AlignmentType getAlignmentFromString(String type) {
            return switch (type) {
                case "PLAYER_ALLY" -> AlignmentType.PLAYER_ALLY;
                case "NPC_ALLY" -> AlignmentType.NPC_ALLY;
                case "HOSTILE" -> AlignmentType.HOSTILE;
                default -> AlignmentType.NO_ALIGNMENT;
            };
        }
    }

    public enum CharacterClass {
        CLASSLESS, DRAGONKNITE, SORCERER, NIGHTBLADE, WARDEN, NECROMANCER, TEMPLAR;

        public static CharacterClass getClassFromString(String type) {
            return switch (type) {
                case "DRAGONKNITE" -> CharacterClass.DRAGONKNITE;
                case "SORCERER" -> CharacterClass.SORCERER;
                case "NIGHTBLADE" -> CharacterClass.NIGHTBLADE;
                case "WARDEN" -> CharacterClass.WARDEN;
                case "NECROMANCER" -> CharacterClass.NECROMANCER;
                case "TEMPLAR" -> CharacterClass.TEMPLAR;
                default -> CharacterClass.CLASSLESS;
            };
        }
    }

    long creationTime;
    int id;
    Property<UnitType> type;
    BooleanProperty isLogger;
    int idInGroup;
    String monsterTypeID;
    BooleanProperty isBoss;
    CharacterClass character_class;
    int race;
    StringProperty characterName;
    StringProperty accountName;
    BigInteger characterID;
    int level;
    int cp;
    int ownerID;
    AlignmentType alignment;
    BooleanProperty inGroup;

    LongProperty health, maxHealth, magicka, maxMagicka, stamina, maxStamina, ult, absorb;
    double posX, posY, posZ;
    int rendersSinceLastUpdate = 0;
    IntegerProperty relequenStacks;
    LongProperty relequenTimecode;

    LongProperty damageDone;
    LongProperty damageDoneBoss;

    public Unit(String line, String[] data) {
        // 0,1         ,2  ,3      ,4,5 ,6     ,7,8,9,10                  ,11               ,12                  ,13,14  ,15 ,16         ,17
        // 4,UNIT_ADDED,  1, PLAYER,T, 1,     0,F,5,1, "Na'Haru Charmiuth",    "@Paratomick", 3895382426828647236,50,1809,  0,PLAYER_ALLY,T
        // 4,UNIT_ADDED, 67, PLAYER,F, 2,     0,F,6,9,           "Auamkin",        "@Shaesn",15102217330525964041,50,1814,  0,PLAYER_ALLY,T
        // 4,UNIT_ADDED, 59, PLAYER,F, 3,     0,F,1,7,     "Bayaz Ancrath","@TaktischeTaube", 8775280970965884875,50,1838,  0,PLAYER_ALLY,T
        // 4,UNIT_ADDED, 73, PLAYER,F, 4,     0,F,1,5,      "Gr'olg Thaox", "@darklight4242",17269376320991016591,50,1279,  0,PLAYER_ALLY,T
        // 4,UNIT_ADDED, 66, PLAYER,F, 5,     0,F,6,7,    "Kelet Baphomet", "@COLOGNE_D3VIL", 3048656060128328184,50,1760,  0,PLAYER_ALLY,T
        // 4,UNIT_ADDED, 68, PLAYER,F, 6,     0,F,1,5,            "Learyt",      "@RmvSound", 2392034613387800246,50, 994,  0,PLAYER_ALLY,T
        // 4,UNIT_ADDED, 63, PLAYER,F, 7,     0,F,3,1,         "Leon Skye",   "@RedHuskyVII",10626831936058289849,50,1800,  0,PLAYER_ALLY,T
        // 4,UNIT_ADDED, 64, PLAYER,F, 8,     0,F,5,1,       "Meera Smeik",   "@iceteaqueen", 7304700668503133780,50,1793,  0,PLAYER_ALLY,T
        // 4,UNIT_ADDED, 60, PLAYER,F, 9,     0,F,2,7,          "Red Kili",     "@maschinle",17273124038875344851,50,1176,  0,PLAYER_ALLY,T
        // 4,UNIT_ADDED, 65, PLAYER,F,10,     0,F,3,8,  "Tayeril Finerion",      "@Wernaert",16659712227656176531,50,1623,  0,PLAYER_ALLY,T
        // 4,UNIT_ADDED, 75, PLAYER,F,11,     0,F,3,9,       "Yako Akasha",    "@YakoAkasha", 3898685377618025624,50,1712,  0,PLAYER_ALLY,T
        // 4,UNIT_ADDED, 74, PLAYER,F,12,     0,F,4,6,  "Zariel Salyranda",    "@Knödelchen",17472536772159625527,50,1039,  0,PLAYER_ALLY,T

        // 0,1         ,2  ,3      ,4,5 ,6     ,7,8,9,10                  ,11               ,12                  ,13,14  ,15 ,16         ,17
        // 4,UNIT_ADDED,461,MONSTER,F, 0,103718,F,0,0,      "Death Hopper",               "",                   0,50, 160,325,    HOSTILE,F
        // 4,UNIT_ADDED, 49,MONSTER,F, 0, 33150,F,0,0,"Twilight Tormentor",               "",                   0,50, 160, 45,   NPC_ALLY,F
        // 4,UNIT_ADDED, 49,MONSTER,F, 0,103586,F,0,0,"Silver Rose Archer",               "",                   0,50, 160,  0,    HOSTILE,F

        creationTime = Long.parseLong(data[0]);
        id = Integer.parseInt(data[2]);                                 // ID
        type = new SimpleObjectProperty<>(UnitType.getTypeFromString(data[3]));                     // PLAYER
        isLogger = new SimpleBooleanProperty(data[4].equals("T"));                                 // T/F // Is logger?
        idInGroup = Integer.parseInt(data[5]);                          // Group ID
        monsterTypeID = data[6];                                        // Monster Type ID?
        isBoss = new SimpleBooleanProperty(data[7].equals("T"));                                   // T/F // Is Boss?
        character_class = CharacterClass.values()[Integer.parseInt(data[8])];   // Class
        race = Integer.parseInt(data[9]);                               // "Playerstuff"
        characterName = new SimpleStringProperty(
                data[10].equals("\"\"") ? ("Anno" + id) : data[10].split("\"")[1]); // Name
        int offset = 0;
        if(!data[11].startsWith("\"")) {
            characterName.set(characterName.get() + data[11].split("\"")[0]);
            offset++;
        }
        accountName = new SimpleStringProperty(
                data[11 + offset].startsWith("\"@") ? data[11 + offset].split("\"")[1] : (type.getValue() == UnitType.PLAYER ? "@Anno" : "")); // Accountname
        characterID = new BigInteger(data[12 + offset]);                         // "Playerstuff"
        level = Integer.parseInt(data[13 + offset]);                             // Level
        cp = Integer.parseInt(data[14 + offset]);                                // CP
        ownerID = Integer.parseInt(data[15 + offset]);                           // Owner
        alignment = AlignmentType.getAlignmentFromString(data[16 + offset]);     // PLAYER_ALLY
        inGroup = new SimpleBooleanProperty(data[17 + offset].equals("T"));                                               // T/F

        health = new SimpleLongProperty(0);
        maxHealth = new SimpleLongProperty(0);
        magicka = new SimpleLongProperty(0);
        maxMagicka = new SimpleLongProperty(0);
        stamina = new SimpleLongProperty(0);
        maxStamina = new SimpleLongProperty(0);
        ult = new SimpleLongProperty(0);
        absorb = new SimpleLongProperty(0);

        relequenStacks = new SimpleIntegerProperty();
        relequenTimecode = new SimpleLongProperty();

        damageDone = new SimpleLongProperty();
        damageDoneBoss = new SimpleLongProperty();
    }

    public void resetStats() {
        relequenStacks.set(0);
        relequenTimecode.set(0);

        damageDone.set(0);
        damageDoneBoss.set(0);
    }

    // 96814,UNIT_ADDED  ,461,MONSTER,F,0,103718,F,0,0,"Death Hopper","",0,50,160,325,HOSTILE,F
    // 97402,UNIT_CHANGED,461,                     0,0,"Death Hopper","",0,50,160,0  ,HOSTILE,F

    //      4,UNIT_ADDED  ,49,PLAYER,F,4,0,F,1,7,"Edana die Feurige","@Wonder2323",14767704093390382522,50,1689,0,PLAYER_ALLY,T
    // 496816,UNIT_CHANGED,49,               1,7,"Edana die Feurige","@Wonder2323",14767704093390382522,50,1690,0,PLAYER_ALLY,T
    // 611700,UNIT_ADDED,49,MONSTER,F,0,33150,F,0,0,"Twilight Tormentor","",0,50,160,45,NPC_ALLY,F
    // 1219426,UNIT_ADDED,49,MONSTER,F,0,103586,F,0,0,"Silver Rose Archer","",0,50,160,0,HOSTILE,F
    public void unitChanged(String[] data) {
        // 0,    ,1           ,2, ,3,4,5                    ,6            ,7                   ,8 ,9   ,10,11         ,12
        // 236053,UNIT_CHANGED,783,0,0,   "Ihudir Duplicate",           "",                   0,50, 160,0 ,HOSTILE    ,F
        //  97402,UNIT_CHANGED,461,0,0,       "Death Hopper",           "",                   0,50, 160,0 ,HOSTILE    ,F
        // 496816,UNIT_CHANGED, 49,1,7,  "Edana die Feurige","@Wonder2323",14767704093390382522,50,1690,0 ,PLAYER_ALLY,T
        // 610396,UNIT_CHANGED,  1,2,1,"Meanellor Charmiuth","@Paratomick", 7742259435989133244,50,1894,0 ,PLAYER_ALLY,T

        character_class = CharacterClass.getClassFromString(data[3]);   // 2
        race = Integer.parseInt(data[4]);                               // 1
        characterName.set(data[5].equals("\"\"") ? "Anno" : data[5].split("\"")[1]);  // "Meanellor Charmiuth"
        int offset = 0;
        if(!data[6].startsWith("\"")) {
            characterName.set(characterName.get() + "," + data[6].split("\"")[0]);
            offset++;
        }
        accountName = new SimpleStringProperty(
                data[6 + offset].startsWith("\"@") ? data[6 + offset].split("\"")[1] : (type.getValue() == UnitType.PLAYER ? "@Anno" : ""));
        characterID = new BigInteger(data[7 + offset]);                          // 7742259435989133244
        level = Integer.parseInt(data[8 + offset]);                              // 50
        cp = Integer.parseInt(data[9 + offset]);                                 // 1894
        ownerID = Integer.parseInt(data[10 + offset]);                           // 0
        alignment = AlignmentType.getAlignmentFromString(data[11 + offset]);     // HOSTILE / PLAYER_ALLY
        inGroup.set(data[12 + offset].equals("T"));                              // F / T
    }

    public void readStats(String health, String magicka, String stamina, String ult, String absorb) {
        readHealth(health);
        readMagicka(magicka);
        readStamina(stamina);
        readUlt(ult);
        readAbsorb(absorb);
        rendersSinceLastUpdate = 0;
    }

    public void readStats(String health, String magicka, String stamina, String ult, String absorb, String posX, String posY, String posZ) {
        readStats(health, magicka, stamina, ult, absorb);
        // Pos Stuff goes here
    }

    public void readHealth(String data) {
        String[] stat = data.split("/");
        health.set(Long.parseLong(stat[0]));
        maxHealth.set(Long.parseLong(stat[1]));
    }

    public void readMagicka(String data) {
        String[] stat = data.split("/");
        magicka.set(Long.parseLong(stat[0]));
        maxMagicka.set(Long.parseLong(stat[1]));
    }

    public void readStamina(String data) {
        String[] stat = data.split("/");
        stamina.set(Long.parseLong(stat[0]));
        maxStamina.set(Long.parseLong(stat[1]));
    }

    public void readUlt(String data) {
        ult.set(Long.parseLong(data.split("/")[0]));
    }

    public void readAbsorb(String data) {
        absorb.set(Long.parseLong(data));
    }

    public void readDamage(String data, boolean isBoss) {
        damageDone.set(damageDone.get() + Long.parseLong(data));
        if(isBoss) {
            damageDoneBoss.set(damageDoneBoss.get() + Long.parseLong(data));
        }
    }

    public BooleanBinding isPlayer() {
        return Bindings.equal(UnitType.PLAYER, (ObservableObjectValue<?>) type);
    }

    public boolean isLogger() {
        return isLogger.get();
    }

    public boolean isBoss() {
        return isBoss.get();
    }

    public long getCreationTime() {
        return creationTime;
    }

    public int getId() {
        return id;
    }

    public int getIdInGroup() {
        return idInGroup;
    }

    public UnitType getType() {
        return type.getValue();
    }

    public String getCharacterName() {
        return characterName.get();
    }

    public String getAccountName() {
        return accountName.get();
    }

    public double getHealth() {
        return health.get();
    }

    public double getMaxHealth() {
        return maxHealth.get();
    }

    public double getMagicka() {
        return magicka.get();
    }

    public double getMaxMagicka() {
        return maxMagicka.get();
    }

    public double getStamina() {
        return stamina.get();
    }

    public double getMaxStamina() {
        return maxStamina.get();
    }

    public double getUlt() {
        return ult.get();
    }

    public long getAbsorb() {
        return absorb.get();
    }

    public StringProperty characterNameProperty() {
        return characterName;
    }

    public StringProperty accountNameProperty() {
        return accountName;
    }

    public LongProperty healthProperty() {
        return health;
    }

    public LongProperty maxHealthProperty() {
        return maxHealth;
    }

    public LongProperty magickaProperty() {
        return magicka;
    }

    public LongProperty maxMagickaProperty() {
        return maxMagicka;
    }

    public LongProperty staminaProperty() {
        return stamina;
    }

    public LongProperty maxStaminaProperty() {
        return maxStamina;
    }

    public LongProperty ultProperty() {
        return ult;
    }

    public LongProperty absorbProperty() {
        return absorb;
    }

    public BooleanProperty inGroupProperty() {
        return inGroup;
    }

    public int rendersSinceLastUpdate() {
        return rendersSinceLastUpdate;
    }

    public void incRenders() {
        rendersSinceLastUpdate++;
    }

    public String getName() {
        return isPlayer().get() ? accountName.getValue() : characterName.get();
    }

    public void setRelequen(int parseInt, long l) {
        //System.out.println("SetRelequen: " + parseInt + ", " + l);
        relequenStacks.set(parseInt);
        relequenTimecode.set(l);
    }

    public void setRelequen(int parseInt) {
        //System.out.println("SetRelequen: " + parseInt);
        relequenStacks.set(parseInt);
    }

    public String toString() {
        return "[" + id + ", " + characterName.get() + ", " + accountName.get() + "," + isPlayer() + "," + inGroup.get() + "]";
    }

    public IntegerProperty relequenProperty() {
        return relequenStacks;
    }

    public LongProperty relequenTimecodeProperty() {
        return relequenTimecode;
    }

    public LongProperty damageDoneProperty() {
        return damageDone;
    }

    public LongProperty damageDoneBossProperty() {
        return damageDoneBoss;
    }
}
