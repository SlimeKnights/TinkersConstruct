package tconstruct.blocks;

import tconstruct.library.TConstructRegistry;
import net.minecraft.block.material.Material;

public class GlassPane extends PaneBase
{
    static String blockTextures[] = { "glass_clear", "soulglass", "soulglass_clear" };

    public GlassPane()
    {
        super(Material.glass, "glass/", blockTextures);
        this.setHardness(0.3F);
        this.stepSound = soundTypeGlass;
        this.setBlockName("tconstruct.glasspane");
        this.setCreativeTab(TConstructRegistry.blockTab);
    }
}
