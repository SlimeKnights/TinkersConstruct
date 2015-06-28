package tconstruct.tools.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import tconstruct.blocks.TConstructBlock;

public class MultiBrickMetal extends TConstructBlock {
    static String blockTextures[] = { "brick_alumite", "brick_ardite", "brick_cobalt", "brick_manyullyn", "fancybrick_alumite", "fancybrick_ardite", "fancybrick_cobalt", "fancybrick_manyullyn"};

    public MultiBrickMetal()
    {
        super(Material.iron, 10f, blockTextures);
        this.setStepSound(Block.soundTypeMetal);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:bricks/" + textureNames[i]);
        }
    }
}
