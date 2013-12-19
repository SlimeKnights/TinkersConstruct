package tconstruct.items.blocks;

import java.util.List;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MultiBrickFancyItem extends MultiItemBlock
{
    static String blockTypes[] = { "obsidian", "sandstone", "netherrack", "stone.refined", "iron", "gold", "lapis", "diamond", "redstone", "bone", "slime", "blueslime", "endstone", "obsidian.ingot",
            "stone", "stone.road" };

    public MultiBrickFancyItem(int id)
    {
        super(id, "block.fancybrick", blockTypes);
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
            list.add(StatCollector.translateToLocal("fancybrick1.tooltip"));
            break;
        case 7:
            list.add(StatCollector.translateToLocal("fancybrick2.tooltip"));
            break;
        case 8:
            list.add(StatCollector.translateToLocal("fancybrick3.tooltip"));
            list.add(StatCollector.translateToLocal("fancybrick4.tooltip"));
            break;
        case 10:
        case 11:
            list.add(StatCollector.translateToLocal("fancybrick5.tooltip"));
            break;
        }
    }
}
