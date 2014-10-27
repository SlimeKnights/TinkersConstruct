package tconstruct.tools.itemblocks;

import cpw.mods.fml.relauncher.*;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class MultiBrickItem extends MultiItemBlock
{
    static String blockTypes[] = { "obsidian", "sandstone", "netherrack", "stone.refined", "iron", "gold", "lapis", "diamond", "redstone", "bone", "slime", "blueslime", "endstone", "obsidian.ingot", "stone.road", "stone.refined.road" };
    public static final int IRON = ArrayUtils.indexOf(blockTypes, "iron");
    public static final int GOLD = ArrayUtils.indexOf(blockTypes, "gold");

    public MultiBrickItem(Block b)
    {
        super(b, "block.brick", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
        case 5:
        case 6:
            list.add(StatCollector.translateToLocal("brick1.tooltip"));
            break;
        case 7:
            list.add(StatCollector.translateToLocal("brick2.tooltip"));
            break;
        case 8:
            list.add(StatCollector.translateToLocal("brick3.tooltip"));
            list.add(StatCollector.translateToLocal("brick4.tooltip"));
            break;
        case 10:
        case 11:
            list.add(StatCollector.translateToLocal("brick5.tooltip"));
            break;
        }
    }
}
