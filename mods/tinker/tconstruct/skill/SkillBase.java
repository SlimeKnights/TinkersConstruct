package mods.tinker.tconstruct.skill;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/* Base skill
 * 
 */

public abstract class SkillBase
{
	public abstract String getTextureFile();
	public abstract String getSkillName();
	
	public int chargeTime() //Ticks
	{
		return 0;
	}
	
	public boolean canPlayerUseSkill()
	{
		return true;
	}
	
	public void activateSkill(EntityPlayer player, World world)
	{
		
	}
	
	public int getSkillCost()
	{
		return 0;
	}
}
