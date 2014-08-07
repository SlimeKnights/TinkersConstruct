package tconstruct.plugins.mfr;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;

public class HarvestableOreBerry implements IFactoryHarvestable
{
    private Block _sourceBlock;
    private Item _berryItem;
    private int _metaOffset;

    public HarvestableOreBerry(Block sourceBlock, Item berryItem, int metaOffset)
    {
        _sourceBlock = sourceBlock;
        _berryItem = berryItem;
        _metaOffset = metaOffset;
    }

    @Override
    public Block getPlant ()
    {
        return _sourceBlock;
    }

    @Override
    public HarvestType getHarvestType ()
    {
        return HarvestType.Column;
    }

    @Override
    public boolean breakBlock ()
    {
        return false;
    }

    @Override
    public boolean canBeHarvested (World world, Map<String, Boolean> harvesterSettings, int x, int y, int z)
    {
        return world.getBlockMetadata(x, y, z) >= 12;
    }

    @Override
    public List<ItemStack> getDrops (World world, Random rand, Map<String, Boolean> harvesterSettings, int x, int y, int z)
    {
        ItemStack[] returnItems = { new ItemStack(_berryItem, 1, world.getBlockMetadata(x, y, z) % 4 + _metaOffset) };
        return Arrays.asList(returnItems);
    }

    @Override
    public void preHarvest (World world, int x, int y, int z)
    {
    }

    @Override
    public void postHarvest (World world, int x, int y, int z)
    {
        world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) - 4, 2);
    }
}
