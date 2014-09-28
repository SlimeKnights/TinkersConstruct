package tconstruct.world.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import tconstruct.blocks.TConstructBlock;

public class TMetalBlock extends TConstructBlock
{

    static String[] metalTypes = new String[] { "compressed_cobalt", "compressed_ardite", "compressed_manyullyn", "compressed_copper", "compressed_bronze", "compressed_tin", "compressed_aluminum", "compressed_alubrass", "compressed_alumite", "compressed_steel", "compressed_ender" };

    public TMetalBlock(Material material, float hardness)
    {
        super(material, hardness, metalTypes);
        this.setStepSound(Block.soundTypeMetal);
    }

    @Override
    public boolean isBeaconBase (IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ)
    {
        return true;
    }
}
