package mods.tinker.tconstruct.library;

import mods.tinker.tconstruct.library.tools.ToolCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ActiveToolMod
{
	/* Updating */
	public void updateTool (ToolCore tool, ItemStack stack, World world, Entity entity)
	{
		
	}
	
	/* Harvesting */
    public boolean beforeBlockBreak (ToolCore tool, ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
    	return false;
    }
    
    public boolean afterBlockBreak()
    {
    	return false;
    }
    
    /* Attacking */
    
    public int baseAttackDamage(int earlyModDamage, int damage, ToolCore tool, ItemStack stack, EntityPlayer player, Entity entity)
    {
    	return 0;
    }
    
    //Calculated after sprinting and enchant bonuses
    public float knockback(float modKnockback, float currentKnockback, ToolCore tool, ItemStack stack, EntityPlayer player, Entity entity)
    {
    	return 0f;
    }
    
    public int attackDamage(int modDamage, int currentDamage, ToolCore tool, ItemStack stack, EntityPlayer player, Entity entity)
    {
    	return 0;
    }
    
    public void lateAttackEntity()
    {
    	
    }
    
    /* Damage tool */
    public boolean damageTool(ItemStack stack, int damage, EntityLiving entity)
    {
    	return false;
    }
}
