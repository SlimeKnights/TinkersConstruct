package tinker.tconstruct.blocks.liquids;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStationary;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.liquids.ILiquid;
import tinker.tconstruct.TConstruct;
import tinker.tconstruct.TContent;
import tinker.tconstruct.client.liquidrender.RenderLiquidMetal;

public abstract class LiquidMetalStill extends BlockStationary implements ILiquid
{
	public LiquidMetalStill(int id)
	{
		super(id, Material.lava);
		//this.setCreativeTab(TConstruct.blockTab);
	}

	@Override
	public int getRenderType ()
	{
		return RenderLiquidMetal.liquidModel;
	}

	@Override
	public String getTextureFile()
    {
        return TContent.liquidTexture;
    }

	@Override
	public int stillLiquidId ()
	{
		return this.blockID;
	}
	
	public abstract int flowingLiquidID();

	@Override
	public boolean isMetaSensitive ()
	{
		return false;
	}

	@Override
	public int stillLiquidMeta ()
	{
		return 0;
	}

	@Override
	public boolean isBlockReplaceable (World world, int i, int j, int k)
	{
		return true;
	}
	
	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
		this.tryToHarden(par1World, par2, par3, par4);

        if (par1World.getBlockId(par2, par3, par4) == this.blockID)
        {
            this.unsetStationary(par1World, par2, par3, par4);
        }
    }
	
	private void tryToHarden(World par1World, int par2, int par3, int par4)
    {
        if (par1World.getBlockId(par2, par3, par4) == this.blockID)
        {
            if (this.blockMaterial == Material.lava)
            {
                boolean var5 = false;

                if (var5 || par1World.getBlockMaterial(par2, par3, par4 - 1) == Material.water)
                {
                    var5 = true;
                }

                if (var5 || par1World.getBlockMaterial(par2, par3, par4 + 1) == Material.water)
                {
                    var5 = true;
                }

                if (var5 || par1World.getBlockMaterial(par2 - 1, par3, par4) == Material.water)
                {
                    var5 = true;
                }

                if (var5 || par1World.getBlockMaterial(par2 + 1, par3, par4) == Material.water)
                {
                    var5 = true;
                }

                if (var5 || par1World.getBlockMaterial(par2, par3 + 1, par4) == Material.water)
                {
                    var5 = true;
                }

                if (var5)
                {
                    /*int var6 = par1World.getBlockMetadata(par2, par3, par4);

                    if (var6 == 0)
                    {
                        par1World.setBlockWithNotify(par2, par3, par4, Block.obsidian.blockID);
                    }
                    else if (var6 <= 4)
                    {
                        par1World.setBlockWithNotify(par2, par3, par4, Block.cobblestone.blockID);
                    }*/

                    this.triggerLavaMixEffects(par1World, par2, par3, par4);
                }
            }
        }
    }

    /**
     * Creates fizzing sound and smoke. Used when lava flows over block or mixes with water.
     */
    protected void triggerLavaMixEffects(World par1World, int par2, int par3, int par4)
    {
        par1World.playSoundEffect((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), "random.fizz", 0.5F, 2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);

        for (int var5 = 0; var5 < 8; ++var5)
        {
            par1World.spawnParticle("largesmoke", (double)par2 + Math.random(), (double)par3 + 1.2D, (double)par4 + Math.random(), 0.0D, 0.0D, 0.0D);
        }
    }
	
	private void unsetStationary(World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        world.editingBlocks = true;
        world.setBlockAndMetadata(x, y, z, flowingLiquidID(), meta);
        world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
        world.scheduleBlockUpdate(x, y, z, flowingLiquidID(), this.tickRate());
        world.editingBlocks = false;
    }
	
	/*@Override
	public int tickRate()
    {
        return 18;
    }*/

}
