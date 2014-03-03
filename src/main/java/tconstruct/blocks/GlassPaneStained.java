package tconstruct.blocks;

import tconstruct.library.TConstructRegistry;
import net.minecraft.block.material.Material;

public class GlassPaneStained extends PaneBase
{
    static String blockTextures[] = { "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black" };

    public GlassPaneStained(int id)
    {
        super(id, Material.glass, "glass/", assembleBlockTextures());
        this.setHardness(0.3F);
        this.setStepSound(soundGlassFootstep);
        this.setUnlocalizedName("tconstruct.glasspanestained");
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
