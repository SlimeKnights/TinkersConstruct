package tconstruct.items.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class SlimeLeavesItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "blue" };

    public SlimeLeavesItemBlock(int id)
    {
        super(id, "block.slime.leaves", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

}
