package tconstruct.smeltery.blocks;

import net.minecraft.block.material.Material;
import tconstruct.library.TConstructRegistry;

public class GlassPaneStained extends PaneBase
{
    static String blockTextures[] = { "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black" };

    public GlassPaneStained()
    {
        super(Material.glass, "glass/", assembleBlockTextures());
        // TODO setHardness
        this.setHardness(0.3F);
        this.stepSound = soundTypeGlass;
        this.setBlockName("tconstruct.glasspanestained");
        this.setCreativeTab(TConstructRegistry.blockTab);
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
    public int damageDropped (int par1)
    {
        return par1;
    }
}
