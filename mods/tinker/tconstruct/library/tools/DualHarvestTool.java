package mods.tinker.tconstruct.library.tools;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

/* Base class for harvest tools with each head having a different purpose */

public abstract class DualHarvestTool extends HarvestTool
{
    public DualHarvestTool(int itemID, int baseDamage)
    {
        super(itemID, baseDamage);
    }

    @Override
    public int getHeadType ()
    {
        return 3;
    }

    @Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        World world = player.worldObj;
        int bID = player.worldObj.getBlockId(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        Block block = Block.blocksList[bID];
        if (block == null || bID < 1)
            return false;
        int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
        int shlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getSecondHarvestType());

        if (hlvl <= tags.getInteger("HarvestLevel") && shlvl <= tags.getInteger("HarvestLevel2"))
        {
            if (tags.getBoolean("Lava") && block.quantityDropped(meta, 0, random) != 0)
            {
                ItemStack smeltStack = new ItemStack(block.idDropped(block.blockID, random, 0), 1, block.damageDropped(meta));
                if (smeltStack.itemID < 0 || smeltStack.itemID >= 32000 || smeltStack.getItem() == null)
                    return false;
                ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(smeltStack);
                if (result != null)
                {
                    world.setBlockToAir(x, y, z);
                    if (!player.capabilities.isCreativeMode)
                        onBlockDestroyed(stack, world, bID, x, y, z, player);
                    if (!world.isRemote)
                    {
                        ItemStack spawnme = result.copy();
                        if (!(result.getItem() instanceof ItemBlock))
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

                    }
                    for (int i = 0; i < 6; i++)
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
        else
        {
            if (!player.capabilities.isCreativeMode)
                onBlockDestroyed(stack, world, bID, x, y, z, player);
            world.setBlockToAir(x, y, z);
            if (!world.isRemote)
                world.playAuxSFX(2001, x, y, z, bID + (meta << 12));
            return true;
        }
    }

    @Override
    public float getStrVsBlock (ItemStack stack, Block block, int meta)
    {

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getBoolean("Broken"))
            return 0.1f;

        Material[] materials = getEffectiveMaterials();
        for (int i = 0; i < materials.length; i++)
        {
            if (materials[i] == block.blockMaterial)
            {
                float speed = tags.getInteger("MiningSpeed");
                speed /= 100f;
                int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
                int durability = tags.getInteger("Damage");

                float shoddy = tags.getFloat("Shoddy");
                speed += shoddy * durability / 100f;

                if (hlvl <= tags.getInteger("HarvestLevel"))
                    return speed;
                return 0.1f;
            }
        }
        materials = getEffectiveSecondaryMaterials();
        for (int i = 0; i < materials.length; i++)
        {
            if (materials[i] == block.blockMaterial)
            {
                float speed = tags.getInteger("MiningSpeed2");
                speed /= 100f;
                int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
                int durability = tags.getInteger("Damage");

                float shoddy = tags.getFloat("Shoddy");
                speed += shoddy * durability / 100f;

                if (hlvl <= tags.getInteger("HarvestLevel2"))
                    return speed;
                return 0.1f;
            }
        }
        return super.getStrVsBlock(stack, block, meta);
    }

    public boolean canHarvestBlock (Block block)
    {
        if (block.blockMaterial.isToolNotRequired())
        {
            return true;
        }
        for (Material m : getEffectiveMaterials())
        {
            if (m == block.blockMaterial)
                return true;
        }
        for (Material m : getEffectiveSecondaryMaterials())
        {
            if (m == block.blockMaterial)
                return true;
        }
        return false;
    }

    @Override
    public String[] toolCategories ()
    {
        return new String[] { "harvest", "dualharvest" };
    }

    protected abstract Material[] getEffectiveSecondaryMaterials ();

    protected abstract String getSecondHarvestType ();
}
