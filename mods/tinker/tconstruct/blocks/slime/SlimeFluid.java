package mods.tinker.tconstruct.blocks.slime;

import java.util.List;
import java.util.Random;

import mods.tinker.tconstruct.blocks.fluid.BlockFluidClassic;
import mods.tinker.tconstruct.entity.BlueSlime;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlimeFluid extends BlockFluidClassic
{
    Icon stillIcon;
    Icon flowIcon;
    public SlimeFluid(int id, Material material)
    {
        super(id, material);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        stillIcon = iconRegister.registerIcon("tinker:slime_blue");
        flowIcon = iconRegister.registerIcon("tinker:slime_blue_flow");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int meta)
    {
        if (side == 0 || side == 1)
            return stillIcon;
        return flowIcon;
    }
    
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        super.updateTick(world, x, y, z, rand);
        if (rand.nextInt(100) == 0 && world.checkNoEntityCollision(AxisAlignedBB.getBoundingBox(x - 1, y - 1, z - 1, x + 2, y + 2, z + 2)) )
        {
            BlueSlime entityslime = new BlueSlime(world);
            entityslime.setPosition((double)x + 0.5D, (double)y + 1.5D, (double)z + 0.5D);
            world.spawnEntityInWorld(entityslime);
        }
    }
    
    public void getSubBlocks (int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        
    }
}
