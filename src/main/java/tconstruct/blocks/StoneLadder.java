package tconstruct.blocks;

import net.minecraft.block.BlockLadder;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;

public class StoneLadder extends BlockLadder
{

    // Override ladder material
    public final Material blockMaterial = Material.rock;

    private Icon icon;

    // Use the normally protected constructor
    public StoneLadder(int id)
    {
        super(id);
        this.setUnlocalizedName("decoration.stoneladder");
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public void registerIcons (IconRegister register)
    {
        icon = register.registerIcon("tinker:ladder_stone");
    }

    @Override
    public Icon getIcon (int meta, int side)
    {
        return icon;
    }

}
