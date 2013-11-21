package tconstruct.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBaseRailLogic;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class WoodRail extends BlockRailBase
{
    @SideOnly(Side.CLIENT)
    private Icon theIcon;

    public WoodRail(int par1)
    {
        super(par1, false);
    }

    @SideOnly(Side.CLIENT)
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon (int par1, int par2)
    {
        return par2 >= 6 ? this.theIcon : this.blockIcon;
    }

    @SideOnly(Side.CLIENT)
    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons (IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("tinker:woodrail");
        this.theIcon = par1IconRegister.registerIcon("tinker:woodrail_turn");
    }

    protected void func_94358_a (World par1World, int par2, int par3, int par4, int par5, int par6, int par7)
    {
        if (par7 > 0 && Block.blocksList[par7].canProvidePower() && (new BlockBaseRailLogic(this, par1World, par2, par3, par4)).getNumberOfAdjacentTracks() == 3)
        {
            this.refreshTrackShape(par1World, par2, par3, par4, false);
        }
    }

    @Override
    public float getRailMaxSpeed (World world, EntityMinecart cart, int y, int x, int z)
    {
        return 0.3f;
    }

}
