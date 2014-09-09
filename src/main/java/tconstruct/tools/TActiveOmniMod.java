package tconstruct.tools;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.enchantment.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.tools.*;
import tconstruct.util.config.PHConstruct;
import tconstruct.world.TinkerWorld;

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
        TinkerTools.modLapis.midStreamModify(stack, tool);
        if (autoSmelt(tool, tags, stack, x, y, z, entity))
            return true;

        return false;
    }

    private boolean autoSmelt (ToolCore tool, NBTTagCompound tags, ItemStack stack, int x, int y, int z, EntityLivingBase entity)
    {
        World world = entity.worldObj;
        Block block = world.getBlock(x, y, z);
        if (block == null)
            return false;

        int meta = world.getBlockMetadata(x, y, z);
        meta = block.damageDropped(meta);

        if (tags.getBoolean("Lava") && block.quantityDropped(meta, 0, random) != 0)
        {
            int amount = block.quantityDropped(random);
            Item item = block.getItemDropped(meta, random, EnchantmentHelper.getFortuneModifier(entity));

            // apparently some things that don't drop blocks (like glass panes without silktouch) return null.
            if (item == null)
                return false;

            ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(new ItemStack(item, amount, meta));
            if (result != null)
            {
                world.setBlockToAir(x, y, z);
                if (entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode)
                    tool.onBlockDestroyed(stack, world, block, x, y, z, entity);
                if (!world.isRemote)
                {
                    ItemStack spawnme = new ItemStack(result.getItem(), amount * result.stackSize, result.getItemDamage());
                    if (result.hasTagCompound())
                        spawnme.setTagCompound(result.getTagCompound());
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
                    world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));

                    int i = spawnme.stackSize;
                    float f = FurnaceRecipes.smelting().func_151398_b(spawnme);
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
    public int baseAttackDamage (int earlyModDamage, int damage, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity)
    {
        TinkerTools.modLapis.midStreamModify(stack, tool);
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
                AbilityHelper.spawnItemAtPlayer((EntityPlayer) entity, new ItemStack(TinkerWorld.strangeFood, 1, 2));
            else
                AbilityHelper.spawnItemAtEntity(entity, new ItemStack(TinkerWorld.strangeFood, 1, 2), 0);
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
                if (tool == TinkerTools.hammer)
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
        if (tool == TinkerTools.cutlass && random.nextInt(10) == 0)
            return true;
        return false;
    }
}
