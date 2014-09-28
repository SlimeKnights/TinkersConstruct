package tconstruct.smeltery.blocks;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.world.World;
import tconstruct.blocks.TConstructBlock;

public class SpeedBlock extends TConstructBlock
{
    public static String[] textureNames = new String[] { "brownstone_rough", "brownstone_rough_road", "brownstone_smooth", "brownstone_smooth_brick", "brownstone_smooth_road", "brownstone_smooth_fancy", "brownstone_smooth_chiseled" };

    public SpeedBlock()
    {
        super(Material.rock, 3.0f, textureNames);
        // this.setBlockBounds(0f, 0f, 0f, 1.0f, 0.5f, 1.0f);
    }

    @Override
    public void onEntityWalking (World world, int x, int y, int z, Entity entity)
    {
        double boost = 2.2D;
        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 1 || metadata == 4)
            boost = 2.7D;

        double mX = Math.abs(entity.motionX);
        double mZ = Math.abs(entity.motionZ);
        if (mX < 0.5D)
        {
            entity.motionX *= boost;
        }
        if (mZ < 0.5D)
        {
            entity.motionZ *= boost;
        }
    }

    @Override
    public int damageDropped (int meta)
    {
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks (Item b, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < textureNames.length; iter++)
        {
            list.add(new ItemStack(b, 1, iter));
        }
    }
}
