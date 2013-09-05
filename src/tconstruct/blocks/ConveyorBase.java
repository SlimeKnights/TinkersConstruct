package tconstruct.blocks;

import tconstruct.library.TConstructRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ConveyorBase extends Block
{
    public ConveyorBase(int ID, Material material)
    {
        super(ID, material);
        this.setCreativeTab(TConstructRegistry.blockTab);
        setBlockBounds(0f, 0f, 0f, 1f, 0.5f, 1f);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) 
    {
        double moveX = 0;
        double moveZ = 0;
        
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta % 4)
        {
        case 0:
            moveX += 0.1;
            break;
        case 1:
            moveZ += 0.1;
            break;
        case 2:
            moveX -= 0.1;
            break;
        case 3:
            moveZ -= 0.1;
            break;
        }
        
        entity.addVelocity(moveX, 0, moveZ);
    }
}
