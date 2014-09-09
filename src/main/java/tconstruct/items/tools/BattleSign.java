package tconstruct.items.tools;

import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.world.World;
import tconstruct.library.tools.Weapon;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.logic.EquipLogic;

public class BattleSign extends Weapon
{
    public BattleSign()
    {
        super(1);
        this.setUnlocalizedName("InfiTool.Battlesign");
    }

    /*
     * public ItemStack onItemRightClick(ItemStack stack, World world,
     * EntityPlayer player) { if (!player.isSneaking())
     * player.setItemInUse(stack, this.getMaxItemUseDuration(stack)); return
     * stack; }
     */

    @Override
    public String getToolName ()
    {
        return "Battlesign";
    }

    /*
     * public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer player,
     * World par3World, int par4, int par5, int par6, int par7, float par8,
     * float par9, float par10) { if (par7 == 0 || !player.isSneaking()) {
     * return false; } else if (!par3World.getBlockMaterial(par4, par5,
     * par6).isSolid()) { return false; } else { if (par7 == 1) { ++par5; }
     * 
     * if (par7 == 2) { --par6; }
     * 
     * if (par7 == 3) { ++par6; }
     * 
     * if (par7 == 4) { --par4; }
     * 
     * if (par7 == 5) { ++par4; }
     * 
     * if (!player.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)) {
     * return false; } else if (!Block.signPost.canPlaceBlockAt(par3World, par4,
     * par5, par6)) { return false; } else { if (par7 == 1) { int var11 =
     * MathHelper.floor_double((double)((player.rotationYaw + 180.0F) * 16.0F /
     * 360.0F) + 0.5D) & 15; par3World.setBlock(par4, par5, par6,
     * Block.signPost.blockID, var11); } else { par3World.setBlock(par4, par5,
     * par6, Block.signWall.blockID, par7); }
     * 
     * //--par1ItemStack.stackSize; TileEntitySign var12 =
     * (TileEntitySign)par3World.getBlockTileEntity(par4, par5, par6);
     * 
     * if (var12 != null) { player.displayGUIEditSign(var12); }
     * 
     * return true; } } }
     */

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.signHead;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return null;
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
        brokenPartStrings.put(index, location[1]);
        handleStrings.put(index, location[2]);
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_battlesign_head";
        case 1:
            return "_battlesign_head_broken";
        case 2:
            return "_battlesign_handle";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_battlesign_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "battlesign";
    }

    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        if (side == 0 || !player.isSneaking())
        {
            return false;
        }
        else if (!world.getBlock(x, y, z).getMaterial().isSolid())
        {
            return false;
        }
        else
        {
            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }

            if (!player.canPlayerEdit(x, y, z, side, stack))
            {
                return false;
            }
            else if (!TinkerTools.battlesignBlock.canPlaceBlockAt(world, x, y, z))
            {
                return false;
            }
            else
            {
                world.setBlock(x, y, z, TinkerTools.battlesignBlock, 0, 3);
                TinkerTools.battlesignBlock.onBlockPlacedBy(world, x, y, z, player, stack);

                EquipLogic logic = (EquipLogic) world.getTileEntity(x, y, z);
                logic.setEquipmentItem(stack);
                --stack.stackSize;

                return true;
            }
        }
    }
}
