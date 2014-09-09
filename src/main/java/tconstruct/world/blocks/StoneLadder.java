package tconstruct.world.blocks;

import cpw.mods.fml.relauncher.*;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

public class StoneLadder extends BlockLadder
{

    // Override ladder material
    public final Material blockMaterial = Material.rock;

    private IIcon icon;

    // Use the normally protected constructor
    public StoneLadder()
    {
        super();
        this.setBlockName("decoration.stoneladder");
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public void registerBlockIcons (IIconRegister register)
    {
        icon = register.registerIcon("tinker:ladder_stone");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int meta, int side)
    {
        return icon;
    }

}
