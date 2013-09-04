package tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class TMetalBlock extends TConstructBlock
{

    static String[] metalTypes = new String[] { "compressed_cobalt", "compressed_ardite", "compressed_manyullyn", "compressed_copper", "compressed_bronze", "compressed_tin", "compressed_aluminum",
            "compressed_alubrass", "compressed_alumite", "compressed_steel" };

    public TMetalBlock(int id, Material material, float hardness)
    {
        super(id, material, hardness, metalTypes);
        this.setStepSound(Block.soundMetalFootstep);
    }

    public boolean isBeaconBase (World worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ)
    {
        return true;
    }
}
