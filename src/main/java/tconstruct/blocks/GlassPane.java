package tconstruct.blocks;

import tconstruct.library.TConstructRegistry;
import net.minecraft.block.material.Material;

public class GlassPane extends PaneBase
{
    static String blockTextures[] = { "glass_clear", "soulglass", "soulglass_clear" };

    public GlassPane()
    {
        super(Material.field_151592_s, "glass/", blockTextures);
        this.func_149711_c(0.3F);
        this.setStepSound(soundGlassFootstep);
        this.setUnlocalizedName("tconstruct.glasspane");
        this.setCreativeTab(TConstructRegistry.blockTab);
    }
}
