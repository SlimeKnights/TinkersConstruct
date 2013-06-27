package mods.tinker.tconstruct.modifiers;

import java.util.Random;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.ActiveToolMod;
import mods.tinker.tconstruct.library.tools.AbilityHelper;
import mods.tinker.tconstruct.library.tools.HarvestTool;
import mods.tinker.tconstruct.library.tools.ToolCore;
import mods.tinker.tconstruct.library.tools.Weapon;
import mods.tinker.tconstruct.util.PHConstruct;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
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

public class TActiveOmniMod extends ActiveToolMod
{
    Random random = new Random();

    /* Updating */
    @Override
    public void updateTool (ToolCore tool, ItemStack stack, World world, Entity entity)
    {
        if (!world.isRemote && entity instanceof EntityLiving && !((EntityLiving) entity).isSwingInProgress && stack.getTagCompound() != null)
        {
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (tags.hasKey("Moss"))
            {
                int chance = tags.getInteger("Moss");
                int check = world.canBlockSeeTheSky((int) entity.posX, (int) entity.posY, (int) entity.posZ) ? 350 : 1150;
                if (random.nextInt(check) < chance)
                {
                    AbilityHelper.healTool(stack, 1, (EntityLiving) entity, true);
                }
            }
        }
    }

    /* Harvesting */
    @Override
    public boolean beforeBlockBreak (ToolCore tool, ItemStack stack, int x, int y, int z, EntityLiving entity)
    {
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode)
            return false;

        if (tool instanceof HarvestTool)
            TContent.modL.midStreamModify(stack);

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        World world = entity.worldObj;
        int bID = entity.worldObj.getBlockId(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        Block block = Block.blocksList[bID];
        if (block == null || bID < 1 || bID > 4095)
            return false;

        if (tags.getBoolean("Lava") && block.quantityDropped(meta, 0, random) != 0)
        {
            ItemStack smeltStack = new ItemStack(block.idDropped(meta, random, 0), block.quantityDropped(meta, 0, random), block.damageDropped(meta));
            if (smeltStack.itemID < 0 || smeltStack.itemID >= 32000 || smeltStack.getItem() == null)
                return false;
            ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(smeltStack);
            if (result != null)
            {
                world.setBlockToAir(x, y, z);
                if (entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode)
                    tool.onBlockDestroyed(stack, world, bID, x, y, z, entity);
                if (!world.isRemote)
                {
                    ItemStack spawnme = result.copy();
                    if (!(result.getItem() instanceof ItemBlock) && PHConstruct.lavaFortuneInteraction)
                    {
                        int loot = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);
                        if (loot > 0)
                        {
                            spawnme.stackSize *= (random.nextInt(loot + 1) + 1);
                        }
                    }
                    EntityItem entityitem = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, spawnme);

                    entityitem.delayBeforeCanPickup = 10;
                    world.spawnEntityInWorld(entityitem);
                    world.playAuxSFX(2001, x, y, z, bID + (meta << 12));

                    int i = spawnme.stackSize;
                    float f = FurnaceRecipes.smelting().getExperience(spawnme);
                    int j;

                    if (f == 0.0F)
                    {
                        i = 0;
                    }
                    else if (f < 1.0F)
                    {
                        j = MathHelper.floor_float((float) i * f);

                        if (j < MathHelper.ceiling_float_int((float) i * f) && (float) Math.random() < (float) i * f - (float) j)
                        {
                            ++j;
                        }

                        i = j;
                    }

                    while (i > 0)
                    {
                        j = EntityXPOrb.getXPSplit(i);
                        i -= j;
                        entity.worldObj.spawnEntityInWorld(new EntityXPOrb(world, x, y + 0.5, z, j));
                    }
                }
                for (int i = 0; i < 5; i++)
                {
                    float f = (float) x + random.nextFloat();
                    float f1 = (float) y + random.nextFloat();
                    float f2 = (float) z + random.nextFloat();
                    float f3 = 0.52F;
                    float f4 = random.nextFloat() * 0.6F - 0.3F;
                    world.spawnParticle("smoke", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);

                    world.spawnParticle("smoke", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);

                    world.spawnParticle("smoke", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);

                    world.spawnParticle("smoke", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
                }
                return true;
            }
        }

        return false;
    }

    /* Attacking */

    @Override
    public int baseAttackDamage (int earlyModDamage, int damage, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLiving player, Entity entity)
    {
        if (tool instanceof Weapon)
            TContent.modL.midStreamModify(stack);
        return 0;
    }

    @Override
    public int attackDamage (int modDamage, int currentDamage, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLiving player, Entity entity)
    {
        int bonus = 0;
        if (entity instanceof EntityLiving)
        {
            EnumCreatureAttribute attribute = ((EntityLiving) entity).getCreatureAttribute();
            if (attribute == EnumCreatureAttribute.UNDEAD)
            {
                if (tool == TContent.hammer)
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
    public float knockback (float modKnockback, float currentKnockback, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLiving player, Entity entity)
    {
        float bonus = 0f;
        if (toolTags.hasKey("Knockback"))
        {
            float level = toolTags.getFloat("Knockback");
            bonus += level;
        }
        return bonus;
    }
}
