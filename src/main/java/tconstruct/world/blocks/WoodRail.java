package tconstruct.world.blocks;

import cpw.mods.fml.relauncher.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class WoodRail extends BlockRailBase
{
    @SideOnly(Side.CLIENT)
    private IIcon theIcon;

    public WoodRail()
    {
        super(false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public IIcon getIcon (int par1, int par2)
    {
        return par2 >= 6 ? this.theIcon : this.blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerBlockIcons (IIconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("tinker:woodrail");
        this.theIcon = par1IconRegister.registerIcon("tinker:woodrail_turn");
    }

    protected void func_94358_a (World par1World, int par2, int par3, int par4, int par5, int par6, Block par7)
    {
        if (par7 != Blocks.air && par7.canProvidePower() && (new Rail(par1World, par2, par3, par4)).func_150650_a() == 3)
        {
            this.func_150052_a(par1World, par2, par3, par4, false);
        }
    }

    @Override
    public float getRailMaxSpeed (World world, EntityMinecart cart, int y, int x, int z)
    {
        return 0.3f;
    }

}
