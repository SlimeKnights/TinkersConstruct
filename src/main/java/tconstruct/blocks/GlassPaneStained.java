package tconstruct.blocks;

import tconstruct.library.TConstructRegistry;
import net.minecraft.block.material.Material;

public class GlassPaneStained extends PaneBase
{
    static String blockTextures[] = { "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black" };

    public GlassPaneStained()
    {
        super(Material.field_151592_s, "glass/", assembleBlockTextures());
        //TODO setHardness
        this.func_149711_c(0.3F);
        this.field_149762_H = field_149778_k;
        this.func_149663_c("tconstruct.glasspanestained");
        this.func_149647_a(TConstructRegistry.blockTab);
    }

    private static String[] assembleBlockTextures ()
    {
        String[] textures = new String[blockTextures.length];
        for (int i = 0; i < blockTextures.length; i++)
        {
            textures[i] = "stainedglass_" + blockTextures[i];
        }
        return textures;
    }

    @Override
    public int getRenderBlockPass ()
    {
        return 1;
    }

    @Override
    public int func_149692_a (int par1)
    {
        return par1;
    }
}
