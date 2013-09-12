package mods.battlegear2.api;


import mods.battlegear2.heraldry.HeraldyPattern;

public interface IHeraldyArmour extends IHeraldyItem{
	
	public String getBaseArmourPath(int armourSlot);

    public String getPatternArmourPath(HeraldyPattern pattern, int armourSlot);
}
