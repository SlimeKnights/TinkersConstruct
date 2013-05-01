package mods.tinker.tconstruct.items.tools;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.crafting.Detailing.DetailInput;
import mods.tinker.tconstruct.library.tools.AbilityHelper;
import mods.tinker.tconstruct.library.tools.ToolCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Chisel extends ToolCore
{
    public Chisel(int id)
    {
        super(id, 0);
        this.setUnlocalizedName("InfiTool.Chisel");
        this.setContainerItem(this);
    }

    @Override
    public ItemStack getContainerItemStack (ItemStack itemStack)
    {
        return itemStack;
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid (ItemStack par1ItemStack)
    {
        return false;
    }

    boolean performDetailing (World world, int x, int y, int z, int blockID, int blockMeta)
    {
        boolean detailed = false;
        return detailed;
    }

    @Override
    public ItemStack onItemRightClick (ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        if (entityplayer.capabilities.isCreativeMode)
        {
            onEaten(itemstack, world, entityplayer);
        }
        else
        {
            entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
        }
        return itemstack;
    }

    @Override
    public ItemStack onEaten (ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        if (!world.isRemote)
        {
            MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityplayer, true);
            if (movingobjectposition == null)
            {
                return itemstack;
            }
            if (movingobjectposition.typeOfHit == EnumMovingObjectType.TILE)
            {
                int x = movingobjectposition.blockX;
                int y = movingobjectposition.blockY;
                int z = movingobjectposition.blockZ;
                int blockID = world.getBlockId(x, y, z);
                int meta = world.getBlockMetadata(x, y, z);

                DetailInput details = TConstruct.chiselDetailing.getDetailing(blockID, meta);
                if (details != null && details.outputID < 4096)
                {
                    world.setBlock(x, y, z, details.outputID, details.outputMeta, 3);
                    if (!(entityplayer.capabilities.isCreativeMode))
                        AbilityHelper.damageTool(itemstack, 1, entityplayer, false);
                    world.playAuxSFX(2001, x, y, z, blockID + (meta << 12));
                    entityplayer.swingItem();
                }
            }
        }

        return itemstack;
    }

    @Override
    public int getMaxItemUseDuration (ItemStack itemstack)
    {
        return 15;
    }

    @Override
    public EnumAction getItemUseAction (ItemStack itemstack)
    {
        return EnumAction.eat;
    }

    @Override
    public int getHeadType ()
    {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 8;
    }

    @Override
    public int getPartAmount ()
    {
        return 2;
    }

    @Override
    public void registerPartPaths (int index, String[] location)
    {
        headStrings.put(index, location[0]);
        brokenHeadStrings.put(index, location[1]);
        handleStrings.put(index, location[2]);
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_chisel_head";
        case 1:
            return "_chisel_head_broken";
        case 2:
            return "_chisel_handle";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_chisel_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "chisel";
    }

    @Override
    protected Item getHeadItem ()
    {
        return TContent.chiselHead;
    }

    @Override
    protected Item getAccessoryItem ()
    {
        return null;
    }

    @Override
    public String[] toolCategories ()
    {
        return new String[] { "utility" };
    }

}
