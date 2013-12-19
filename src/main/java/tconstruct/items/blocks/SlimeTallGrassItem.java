package tconstruct.items.blocks;

import mantle.blocks.abstracts.MultiItemBlock;

import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import tconstruct.common.TRepo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlimeTallGrassItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "tallgrass", "tallgrass.fern" };

    public SlimeTallGrassItem(int id)
    {
        super(id,"block.slime", blockTypes );
        setMaxDamage(0);
        setHasSubtypes(true);
    }


    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamage (int meta)
    {
        int arr = MathHelper.clamp_int(meta, 0, this.blockType.length);
        return TRepo.slimeTallGrass.getIcon(0, arr);
    }

}
