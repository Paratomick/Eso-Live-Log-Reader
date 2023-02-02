package de.paratomick.model;

public class Ability {

    public enum EffectType {
        BUFF, DEBUFF, UNKNOWN
    }

    public enum DamageType {
        NONE, MAGIC, BLEED, SNARE, ROOT, STUN, ENVIRONMENT, POISON, DISEASE, UNKNOWN
    }

    int id;

    // Ability Info
    //String name;
    //String icon;
    boolean var5;
    boolean var6;

    // Effect Info
    EffectType effectType;
    DamageType varE4;
    boolean varE5;
    int varE6 = -1;

    boolean isRelequen;

    public Ability(String[] data) {
        // 0    ,1           ,2      ,3                       ,4                                                      ,5,6
        // 159582,ABILITY_INFO, 58955,   "Death Achieve Check",                "/esoui/art/icons/ability_mage_065.dds",F,F
        // 255475,ABILITY_INFO, 40493,         "Shooting Star",         "/esoui/art/icons/ability_mageguild_005_a.dds",F,F
        //  58395,ABILITY_INFO, 76617,          "Minor Slayer",       "/esoui/art/icons/ability_buff_minor_slayer.dds",F,T
        //  58395, EFFECT_INFO, 76617,BUFF,NONE,F
        //  58395,ABILITY_INFO,147226,          "Minor Slayer",                "/esoui/art/icons/ability_mage_065.dds",F,T
        //  58395, EFFECT_INFO,147226,BUFF,NONE,T
        // 263350,ABILITY_INFO, 76916,      "Puncturing Sweep","/esoui/art/icons/ability_templar_reckless_attacks.dds",T,F
        //  30138,ABILITY_INFO, 23604,"Light Attack (Unarmed)",         "/esoui/art/icons/death_recap_melee_basic.dds",F,T
        //  58395,ABILITY_INFO,107202,      "Arms of Relequen",                "/esoui/art/icons/ability_mage_065.dds",T,T
        //  58395, EFFECT_INFO,107202,BUFF,NONE,T
        // 474946,ABILITY_INFO,107203,      "Arms of Relequen","/esoui/art/icons/ability_warden_003.dds",F,F
        // 474946, EFFECT_INFO,107203,DEBUFF,NONE,F
        // Posisoned (T,F), Barbed Trap (T,F)
        id = Integer.parseInt(data[2]);    // 23604
        //name = data[3];                    // "Light Attack (Unarmed)"
        //icon = data[4];                    // "/esoui/art/icons/death_recap_melee_basic.dds"
        var5 = data[5].equals("T");        // F
        var6 = data[6].equals("T");        // T

        isRelequen = false;
    }

    public void setEffect(String[] data) {
        // 0    ,1          ,2     ,3   ,4   ,5, 6
        // 30188,EFFECT_INFO,120017,BUFF,NONE,T
        // 5620391,EFFECT_INFO,21487,DEBUFF,MAGIC,T
        // 4895543,EFFECT_INFO,56984,DEBUFF,BLEED,F
        // 4896594,EFFECT_INFO,56983,DEBUFF,BLEED,T
        // 1106273,EFFECT_INFO,42040,BUFF,NONE,T,63507
        id = Integer.parseInt(data[2]);                // 120017
        effectType = getEffectTypeFromString(data[3]); // BUFF
        varE4 = getTypeFromString(data[4]);            // NONE
        varE5 = data[5].equals("T");                   // T

        isRelequen = data[3].contains("Arms of Relequen") && effectType == EffectType.DEBUFF;
    }

    public EffectType getEffectTypeFromString(String type) {
        return switch (type) {
            case "BUFF" -> EffectType.BUFF;
            case "DEBUFF" -> EffectType.DEBUFF;
            default -> EffectType.UNKNOWN;
        };
    }

    public DamageType getTypeFromString(String type) {
        return switch (type) {
            case "NONE" -> DamageType.NONE;
            case "MAGIC" -> DamageType.MAGIC;
            case "BLEED" -> DamageType.BLEED;
            case "SNARE" -> DamageType.SNARE;
            case "ROOT" -> DamageType.ROOT;
            case "STUN" -> DamageType.STUN;
            case "ENVIRONMENT" -> DamageType.ENVIRONMENT;
            case "POISON" -> DamageType.POISON;
            case "DISEASE" -> DamageType.DISEASE;
            default -> DamageType.UNKNOWN;
        };
    }

    public boolean isRelequen() {
        return isRelequen;
    }

}
