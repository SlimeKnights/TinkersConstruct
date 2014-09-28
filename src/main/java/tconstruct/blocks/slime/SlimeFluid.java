package tconstruct.blocks.slime;

import cpw.mods.fml.relauncher.*;
import java.util.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import tconstruct.world.TinkerWorld;
import tconstruct.world.entity.BlueSlime;

public class SlimeFluid extends BlockFluidClassic
{
    IIcon stillIcon;
    IIcon flowIcon;

    public SlimeFluid(Fluid fluid, Material material)
    {
        super(fluid, material);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        stillIcon = iconRegister.registerIcon("tinker:slime_blue");
        flowIcon = iconRegister.registerIcon("tinker:slime_blue_flow");
        TinkerWorld.blueSlimeFluid.setStillIcon(stillIcon);
        TinkerWorld.blueSlimeFluid.setFlowingIcon(flowIcon);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        if (side == 0 || side == 1)
            return stillIcon;
        return flowIcon;
    }

    @Override
    public void updateTick (World world, int x, int y, int z, Random rand)
    {
        super.updateTick(world, x, y, z, rand);
        if (rand.nextInt(100) == 0 && world.getBlockMetadata(x, y, z) == 0 && world.checkNoEntityCollision(AxisAlignedBB.getBoundingBox(x - 1, y - 1, z - 1, x + 2, y + 2, z + 2)))
        {
            BlueSlime entityslime = new BlueSlime(world);
            entityslime.setPosition((double) x + 0.5D, (double) y + 1.5D, (double) z + 0.5D);
            world.spawnEntityInWorld(entityslime);
        }
    }

    public void getSubBlocks (int par1, CreativeTabs par2CreativeTabs, List par3List)
    {

    }
}
