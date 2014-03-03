package tconstruct.items.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import tconstruct.common.TContent;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.Weapon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Scythe extends Weapon
{
    public Scythe(int itemID)
    {
        super(itemID, 4);
        this.setUnlocalizedName("InfiTool.Scythe");
    }

    /*@Override
    protected String getHarvestType()
    {
    	return "sword";
    }*/

    @Override
    protected Material[] getEffectiveMaterials ()
    {
        return materials;
    }

    static Material[] materials = new Material[] { Material.web, Material.cactus, Material.pumpkin, Material.plants, Material.vine, Material.leaves };

    @Override
    public Item getHeadItem ()
    {
        return TContent.scytheBlade;
    }

    @Override
    public Item getHandleItem ()
    {
        return TContent.toughRod;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TContent.toughBinding;
    }

    @Override
    public Item getExtraItem ()
    {
        return TContent.toughRod;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 10;
    }

    @Override
    public int getPartAmount ()
    {
        return 4;
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_scythe_head";
        case 1:
            return "_scythe_head_broken";
        case 2:
            return "_scythe_handle";
        case 3:
            return "_scythe_binding";
        case 4:
            return "_scythe_accessory";
        default:
            return "";
        }
    }

    public float getDurabilityModifier ()
    {
        return 3.0f;
    }

    @Override
    public float getRepairCost ()
    {
        return 4.0f;
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_scythe_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "scythe";
    }

    @Override
    public int durabilityTypeAccessory ()
    {
        return 1;
    }

    @Override
    public int durabilityTypeExtra ()
    {
        return 1;
    }

    @Override
    public float getDamageModifier ()
    {
        return 0.75f;
    }

    @Override
    public String[] toolCategories ()
    {
        return new String[] { "weapon", "melee", "harvest" };
    }

    /* Scythe Specific */

    @Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        if (!stack.hasTagCompound())
            return false;

        World world = player.worldObj;
        final int blockID = world.getBlockId(x, y, z);
        final int meta = world.getBlockMetadata(x, y, z);
        if (!stack.hasTagCompound())
            return false;
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        boolean butter = EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack) > 0;
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            for (int yPos = y - 1; yPos <= y + 1; yPos++)
            {
                for (int zPos = z - 1; zPos <= z + 1; zPos++)
                {
                    if (!(tags.getBoolean("Broken")))
                    {
                        boolean cancelHarvest = false;
                        for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
                        {
                            if (mod.beforeBlockBreak(this, stack, xPos, yPos, zPos, player))
                                cancelHarvest = true;
                        }

                        if (!cancelHarvest)
                        {
                            int localblockID = world.getBlockId(xPos, yPos, zPos);
                            Block localBlock = Block.blocksList[localblockID];
                            int localMeta = world.getBlockMetadata(xPos, yPos, zPos);
                            float localHardness = localBlock == null ? Float.MAX_VALUE : localBlock.getBlockHardness(world, xPos, yPos, zPos);
                            if (localBlock != null)// && (block.blockMaterial == Material.leaves || block.isLeaves(world, xPos, yPos, zPos)))
                            {
                                for (int iter = 0; iter < materials.length; iter++)
                                {
                                    if (materials[iter] == localBlock.blockMaterial)
                                    {
                                        if (!player.capabilities.isCreativeMode)
                                        {
                                            if (butter && localBlock instanceof IShearable && ((IShearable) localBlock).isShearable(stack, player.worldObj, x, y, z))
                                            {
                                                ArrayList<ItemStack> drops = ((IShearable) localBlock).onSheared(stack, player.worldObj, x, y, z,
                                                        EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));
                                                Random rand = new Random();

                                                if (!world.isRemote)
                                                    for (ItemStack dropStack : drops)
                                                    {
                                                        float f = 0.7F;
                                                        double d = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                                                        double d1 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                                                        double d2 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                                                        EntityItem entityitem = new EntityItem(player.worldObj, (double) xPos + d, (double) yPos + d1, (double) zPos + d2, dropStack);
                                                        entityitem.delayBeforeCanPickup = 10;
                                                        player.worldObj.spawnEntityInWorld(entityitem);
                                                    }

                                                if (localHardness > 0f)
                                                    onBlockDestroyed(stack, world, localblockID, xPos, yPos, zPos, player);
                                                player.addStat(StatList.mineBlockStatArray[localblockID], 1);
                                                world.setBlockToAir(xPos, yPos, zPos);
                                            }
                                            else
                                            {
                                                if (localBlock.removeBlockByPlayer(world, player, xPos, yPos, zPos))
                                                {
                                                    localBlock.onBlockDestroyedByPlayer(world, xPos, yPos, zPos, localMeta);
                                                }
                                                localBlock.harvestBlock(world, player, xPos, yPos, zPos, localMeta);
                                                localBlock.onBlockHarvested(world, xPos, yPos, zPos, localMeta, player);
                                                if (localHardness > 0f)
                                                    onBlockDestroyed(stack, world, localblockID, xPos, yPos, zPos, player);
                                            }
                                        }
                                        else
                                        {
                                            world.setBlockToAir(xPos, yPos, zPos);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!world.isRemote)
            world.playAuxSFX(2001, x, y, z, blockID + (meta << 12));
        return super.onBlockStartBreak(stack, x, y, z, player);
    }

    public boolean onLeftClickEntity (ItemStack stack, EntityPlayer player, Entity entity)
    {
        AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(entity.posX, entity.posY, entity.posZ, entity.posX + 1.0D, entity.posY + 1.0D, entity.posZ + 1.0D).expand(1.0D, 1.0D, 1.0D);
        List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, box);
        for (Object o : list)
        {
            AbilityHelper.onLeftClickEntity(stack, player, (Entity) o, this);
        }
        return true;
    }

    //1.6.4 start
    @Override
    public boolean allowOffhand (ItemStack mainhand, ItemStack offhand)
    {
        return false;
    }

    @Override
    public boolean isOffhandHandDual (ItemStack off)
    {
        return false;
    }

    @Override
    public boolean sheatheOnBack (ItemStack item)
    {
        return true;
    }

    //1.6.2 end

    //1.6.2 start
    @Override
    public boolean willAllowOffhandWeapon ()
    {
        return false;
    }

    @Override
    public boolean willAllowShield ()
    {
        return false;
    }

    @Override
    public boolean isOffhandHandDualWeapon ()
    {
        return false;
    }

    @Override
    public boolean sheatheOnBack ()
    {
        return true;
    }
    //1.6.2 end
}
