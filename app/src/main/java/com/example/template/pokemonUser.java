package com.example.template;

public class pokemonUser {
    public String species, nickname, ability, nature;
    public Stats stats, baseStats, evs, ivs;

    public pokemonUser() {

    }

    public pokemonUser(String species, String nickname, String ability, String nature, int currHP,int currATK,int currDEF,int currSATK, int currSDEF, int currSPD,int baseHP,int baseATK,int baseDEF,int baseSATK, int baseSDEF, int baseSPD,int evHP,int evATK,int evDEF,int evSATK, int evSDEF, int evSPD,int ivHP,int ivATK,int ivDEF,int ivSATK, int ivSDEF, int ivSPD) {
        this.species = species;
        this.nickname = nickname;
        this.ability = ability;
        this.nature = nature;
        this.stats = new Stats(currHP,currATK,currDEF,currSATK,currSDEF,currSPD);
        this.baseStats = new Stats(baseHP,baseATK,baseDEF,baseSATK,baseSDEF,baseSPD);
        this.evs = new Stats(evHP,evATK,evDEF,evSATK,evSDEF,evSPD);
        this.ivs = new Stats(ivHP,ivATK,ivDEF,ivSATK,ivSDEF,ivSPD);
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public Stats getBaseStats() {
        return baseStats;
    }

    public void setBaseStats(Stats baseStats) {
        this.baseStats = baseStats;
    }

    public Stats getEvs() {
        return evs;
    }

    public void setEvs(Stats evs) {
        this.evs = evs;
    }

    public Stats getIvs() {
        return ivs;
    }

    public void setIvs(Stats ivs) {
        this.ivs = ivs;
    }
}
