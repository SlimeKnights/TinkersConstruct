package tconstruct.smeltery.itemblocks;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class MetalItemBlock extends MultiItemBlock
{
    public static final String blockTypes[] = {
        "Cobalt", "Ardite", "Manyullyn", "Copper", "Bronze",
        "Tin", "Aluminum", "AlBrass", "Alumite", "Steel", "Ender" };

    public static final int COBALT = ArrayUtils.indexOf(blockTypes, "Cobalt");
    public static final int ARDITE = ArrayUtils.indexOf(blockTypes, "Ardite");
    public static final int MANYULLYN = ArrayUtils.indexOf(blockTypes, "Manyullyn");
    public static final int COPPER = ArrayUtils.indexOf(blockTypes, "Copper");
    public static final int BRONZE = ArrayUtils.indexOf(blockTypes, "Bronze");
    public static final int TIN = ArrayUtils.indexOf(blockTypes, "Tin");
    public static final int ALUMINUM = ArrayUtils.indexOf(blockTypes, "Aluminum");
    public static final int ALUMINUM_BRASS = ArrayUtils.indexOf(blockTypes, "AlBrass");
    public static final int ALUMITE = ArrayUtils.indexOf(blockTypes, "Alumite");
    public static final int STEEL = ArrayUtils.indexOf(blockTypes, "Steel");
    public static final int ENDER = ArrayUtils.indexOf(blockTypes, "Ender");

    public MetalItemBlock(Block b)
    {
        super(b, "StorageMetals", blockTypes);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add(StatCollector.translateToLocal("metalblock.tooltip"));
    }
}
