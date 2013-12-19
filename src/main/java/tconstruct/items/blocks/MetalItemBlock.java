package tconstruct.items.blocks;

import java.util.List;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class MetalItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = { "Cobalt", "Ardite", "Manyullyn", "Copper", "Bronze", "Tin", "Aluminum", "AlBrass", "Alumite", "Steel", "Ender" };

    public MetalItemBlock(int id)
    {
        super(id, "StorageMetals", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add(StatCollector.translateToLocal("metalblock.tooltip"));
    }
}
