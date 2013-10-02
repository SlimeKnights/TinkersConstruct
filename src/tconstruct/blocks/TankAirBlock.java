package tconstruct.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tconstruct.blocks.logic.TankAirLogic;

public class TankAirBlock extends BlockContainer
{

    public TankAirBlock(int id, Material material)
    {
        super(id, material);
    }

    @Override
    public TileEntity createNewTileEntity (World world)
    {
        return new TankAirLogic();
    }
}
