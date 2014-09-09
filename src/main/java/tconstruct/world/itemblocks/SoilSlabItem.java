package tconstruct.world.itemblocks;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class SoilSlabItem extends MultiItemBlock
{
    public static final String blockTypes[] = { "grass", "dirt", "mycelium", "slime", "grout", "blueslime", "graveyardsoil", "consecratedsoil" };

    public SoilSlabItem(Block b)
    {
        super(b, "block.soil.slab", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
        case 3:
            list.add(StatCollector.translateToLocal("craftedsoil.slab1.tooltip"));
            break;
        case 4:
            list.add(StatCollector.translateToLocal("craftedsoil.slab2.tooltip"));
            break;
        }
    }
}
