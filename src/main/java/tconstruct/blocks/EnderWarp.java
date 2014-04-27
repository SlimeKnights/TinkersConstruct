package tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import tconstruct.blocks.logic.EnderWarpLogic;
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnderWarp extends BlockContainer
{
    public EnderWarp(int id)
    {
        super(id, Material.anvil);
        this.setCreativeTab(TConstructRegistry.blockTab);
        setHardness(2.0f);
        setStepSound(soundMetalFootstep);
    }

    @Override
    public TileEntity createNewTileEntity (World world)
    {
        return new EnderWarpLogic();
    }

    public void onNeighborBlockChange (World world, int x, int y, int z, int neighborBlockID)
    {
        EnderWarpLogic logic = (EnderWarpLogic) world.getBlockTileEntity(x, y, z);
        logic.recalculateFrequency();

        boolean flag = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z);
        if (flag)
            logic.teleport();
    }

    @SideOnly(Side.CLIENT)
    Icon topIcon;

    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("tinker:compressed_ender");
        this.topIcon = par1IconRegister.registerIcon("tinker:compressed_ender_top");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int meta)
    {
        if (side == 0 || side == 1)
            return topIcon;
        return this.blockIcon;
    }
}
