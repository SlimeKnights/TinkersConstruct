package tconstruct.smeltery.itemblocks;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import tconstruct.achievements.TAchievements;

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

    @Override
    public void onCreated (ItemStack item, World world, EntityPlayer player)
    {
        TAchievements.triggerAchievement(player, "tconstruct.smelteryMaker");
    }
}
