package tconstruct.modifiers.tools;

import java.util.Random;

import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import tconstruct.common.TRepo;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.ToolCore;
import tconstruct.util.config.PHConstruct;

public class TActiveOmniMod extends ActiveToolMod
{
    Random random = new Random();

    /* Updating */
    @Override
    public void updateTool (ToolCore tool, ItemStack stack, World world, Entity entity)
    {
        if (!world.isRemote && entity instanceof EntityLivingBase && !((EntityLivingBase) entity).isSwingInProgress && stack.getTagCompound() != null)
        {
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (tags.hasKey("Moss"))
            {
                int chance = tags.getInteger("Moss");
                int check = world.canBlockSeeTheSky((int) entity.posX, (int) entity.posY, (int) entity.posZ) ? 350 : 1150;
                if (random.nextInt(check) < chance)
                {
                    AbilityHelper.healTool(stack, 1, (EntityLivingBase) entity, true);
                }
            }
        }
    }

    /* Harvesting */
    @Override
    public boolean beforeBlockBreak (ToolCore tool, ItemStack stack, int x, int y, int z, EntityLivingBase entity)
    {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        baconator(tool, stack, entity, tags);

        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode)
            return false;
        TRepo.modLapis.midStreamModify(stack, tool);

        return false;
    }

    /* Attacking */

    @Override
    public int baseAttackDamage (int earlyModDamage, int damage, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity)
    {
        TRepo.modLapis.midStreamModify(stack, tool);
        baconator(tool, stack, player, tags);
        return 0;
    }

    private void baconator (ToolCore tool, ItemStack stack, EntityLivingBase entity, NBTTagCompound tags)
    {
        int bacon = 0;
        bacon += tags.getInteger("Head") == 18 ? 1 : 0;
        bacon += tags.getInteger("Handle") == 18 ? 1 : 0;
        bacon += tags.getInteger("Accessory") == 18 ? 1 : 0;
        bacon += tags.getInteger("Extra") == 18 ? 1 : 0;
        int chance = tool.getPartAmount() * 100;
        if (random.nextInt(chance) < bacon)
        {
            if (entity instanceof EntityPlayer)
                AbilityHelper.spawnItemAtPlayer((EntityPlayer) entity, new ItemStack(TRepo.strangeFood, 1, 2));
            else
                AbilityHelper.spawnItemAtEntity(entity, new ItemStack(TRepo.strangeFood, 1, 2), 0);
        }
    }

    @Override
    public int attackDamage (int modDamage, int currentDamage, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity)
    {
        int bonus = 0;
        if (entity instanceof EntityLivingBase)
        {
            EnumCreatureAttribute attribute = ((EntityLivingBase) entity).getCreatureAttribute();
            if (attribute == EnumCreatureAttribute.UNDEAD)
            {
                if (tool == TRepo.hammer)
                {
                    int level = 2;
                    bonus += random.nextInt(level * 2 + 1) + level * 2;
                }
                if (toolTags.hasKey("ModSmite"))
                {
                    int[] array = toolTags.getIntArray("ModSmite");
                    int base = array[0] / 18;
                    bonus += 1 + base + random.nextInt(base + 1);
                }
            }
            if (attribute == EnumCreatureAttribute.ARTHROPOD)
            {
                if (toolTags.hasKey("ModAntiSpider"))
                {
                    int[] array = toolTags.getIntArray("ModAntiSpider");
                    int base = array[0] / 2;
                    bonus += 1 + base + random.nextInt(base + 1);
                }
            }
        }
        return bonus;
    }

    @Override
    public float knockback (float modKnockback, float currentKnockback, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity)
    {
        float bonus = 0f;
        if (toolTags.hasKey("Knockback"))
        {
            float level = toolTags.getFloat("Knockback");
            bonus += level;
        }
        return bonus;
    }

    @Override
    public boolean doesCriticalHit (ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity)
    {
        if (tool == TRepo.cutlass && random.nextInt(10) == 0)
            return true;
        return false;
    }
}
