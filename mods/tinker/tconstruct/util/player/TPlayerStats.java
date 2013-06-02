package mods.tinker.tconstruct.util.player;

import java.lang.ref.WeakReference;
import java.util.List;

import mods.tinker.tconstruct.skill.Skill;
import net.minecraft.entity.player.EntityPlayer;

public class TPlayerStats
{
	public WeakReference<EntityPlayer> player;
	public int level;
	public int levelHealth;
	public int bonusHealth;
	public int hunger;
	public boolean beginnerManual;
	public boolean materialManual;
	public boolean smelteryManual;
	public ArmorExtended armor;
	public List<Skill> skillList;
}
