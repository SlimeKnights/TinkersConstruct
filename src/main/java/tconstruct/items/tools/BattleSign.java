package tconstruct.items.tools;

import net.minecraft.item.Item;
import tconstruct.common.TRepo;
import tconstruct.library.tools.Weapon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BattleSign extends Weapon
{
    public BattleSign()
    {
        super(1);
        this.setUnlocalizedName("InfiTool.Battlesign");
    }

    /*public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
    	if (!player.isSneaking())
    		player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }*/

    public String getToolName ()
    {
        return "Battlesign";
    }

    /*public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer player, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (par7 == 0 || !player.isSneaking())
        {
            return false;
        }
        else if (!par3World.getBlockMaterial(par4, par5, par6).isSolid())
        {
            return false;
        }
        else
        {
            if (par7 == 1)
            {
                ++par5;
            }

            if (par7 == 2)
            {
                --par6;
            }

            if (par7 == 3)
            {
                ++par6;
            }

            if (par7 == 4)
            {
                --par4;
            }

            if (par7 == 5)
            {
                ++par4;
            }

            if (!player.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
            {
                return false;
            }
            else if (!Block.signPost.canPlaceBlockAt(par3World, par4, par5, par6))
            {
                return false;
            }
            else
            {
                if (par7 == 1)
                {
                    int var11 = MathHelper.floor_double((double)((player.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                    par3World.setBlock(par4, par5, par6, Block.signPost.blockID, var11);
                }
                else
                {
                    par3World.setBlock(par4, par5, par6, Block.signWall.blockID, par7);
                }

                //--par1ItemStack.stackSize;
                TileEntitySign var12 = (TileEntitySign)par3World.getBlockTileEntity(par4, par5, par6);

                if (var12 != null)
                {
                    player.displayGUIEditSign(var12);
                }

                return true;
            }
        }
    }*/

    @Override
    public Item getHeadItem ()
    {
        return TRepo.signHead;
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
}
