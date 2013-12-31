package tconstruct.items.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class SmelteryItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "Controller", "Drain", "Brick", "Furnace", "Stone", "Cobblestone", "Paver", "Brick.Cracked", "Road", "Brick.Fancy", "Brick.Square" };

    public SmelteryItemBlock(Block b)
    {
        super(b, "Smeltery", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }



    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            list.add(StatCollector.translateToLocal("smeltery.controller.tooltip"));
            break;
        case 1:
            list.add(StatCollector.translateToLocal("smeltery.drain.tooltip1"));
            list.add(StatCollector.translateToLocal("smeltery.drain.tooltip2"));
            break;
        case 3:
            list.add(StatCollector.translateToLocal("smeltery.furnace.tooltip"));
            break;
        default:
            list.add(StatCollector.translateToLocal("smeltery.brick.tooltip1"));
            list.add(StatCollector.translateToLocal("smeltery.brick.tooltip2"));
            break;
        }
    }
}
