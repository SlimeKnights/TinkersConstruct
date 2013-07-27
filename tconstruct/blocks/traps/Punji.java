package tconstruct.blocks.traps;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class Punji extends Block
{

    public Punji(int id)
    {
        super(id, Material.circuits);
    }

    @Override
    public void onEntityCollidedWithBlock (World world, int x, int y, int z, Entity entity)
    {
        entity.fallDistance *= 2.0F;
    }

}
