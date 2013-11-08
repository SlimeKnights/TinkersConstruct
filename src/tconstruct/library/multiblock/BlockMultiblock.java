package tconstruct.library.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockMultiblock extends BlockContainer
{

    protected BlockMultiblock(int par1, Material par2Material)
    {
        super(par1, par2Material);
    }

    @Override
    public abstract TileEntity createNewTileEntity (World world);

}
