package tinker.tconstruct.tools;

import tinker.tconstruct.AbilityHelper;
import tinker.tconstruct.TConstructContent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BattleSign extends Weapon
{
	public BattleSign(int itemID, String tex)
	{
		super(itemID, 1, tex);
		this.setItemName("InfiTool.Battlesign");
	}
	
	/*public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
		if (!player.isSneaking())
			player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }*/
	
	public String getToolName ()
	{
		return "Battle Sign";
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
                    par3World.setBlockAndMetadataWithNotify(par4, par5, par6, Block.signPost.blockID, var11);
                }
                else
                {
                    par3World.setBlockAndMetadataWithNotify(par4, par5, par6, Block.signWall.blockID, par7);
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
	protected Item getHeadItem ()
	{
		return  TConstructContent.signHead;
	}

	@Override
	protected Item getAccessoryItem ()
	{
		return null;
	}
}
