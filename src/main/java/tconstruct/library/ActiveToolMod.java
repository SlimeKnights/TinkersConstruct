package tconstruct.library;

import net.minecraft.entity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.library.tools.ToolCore;

public class ActiveToolMod
{
    /* Updating */
    public void updateTool (ToolCore tool, ItemStack stack, World world, Entity entity)
    {

    }

    /* Harvesting */
    public boolean beforeBlockBreak (ToolCore tool, ItemStack stack, int x, int y, int z, EntityLivingBase entity)
    {
        return false;
    }

    public boolean afterBlockBreak () // Unfinished, not called
    {
        return false;
    }

    /* Attacking */

    public int baseAttackDamage (int earlyModDamage, int damage, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity)
    {
        return 0;
    }

    // Calculated after sprinting and enchant bonuses
    public float knockback (float modKnockback, float currentKnockback, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity)
    {
        return 0f;
    }

    public int attackDamage (int modDamage, int currentDamage, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity)
    {
        return 0;
    }

    public void lateAttackEntity () // Unfinished, not called
    {

    }

    /* Damage tool */
    public boolean damageTool (ItemStack stack, int damage, EntityLivingBase entity)
    {
        return false;
    }

    public boolean doesCriticalHit (ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity)
    {
        return false;
    }
}
