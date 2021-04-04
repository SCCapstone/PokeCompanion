package com.example.template;

public class Stats {
    int hp, atk, def, satk, sdef, spd;

    public Stats() {
    }

    public Stats(int atk, int def, int hp, int satk, int sdef, int spd) {
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.satk = satk;
        this.sdef = sdef;
        this.spd = spd;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public int getSatk() {
        return satk;
    }

    public void setSatk(int satk) {
        this.satk = satk;
    }

    public int getSdef() {
        return sdef;
    }

    public void setSdef(int sdef) {
        this.sdef = sdef;
    }

    public int getSpd() {
        return spd;
    }

    public void setSpd(int spd) {
        this.spd = spd;
    }

    public String toString() {
        return "HP: " + this.hp + "\tSPD: " + this.spd + "\tATK: " + this.atk + "\tDEF: " + this.def +
                "\tSATK: " + this.satk + "\tSDEF: " + this.sdef;
    }
}
