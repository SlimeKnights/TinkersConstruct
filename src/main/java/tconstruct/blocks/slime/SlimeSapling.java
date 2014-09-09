package tconstruct.blocks.slime;

import cpw.mods.fml.relauncher.*;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import tconstruct.library.TConstructRegistry;
import tconstruct.tools.TinkerTools;
import tconstruct.world.TinkerWorld;
import tconstruct.world.gen.SlimeTreeGen;

public class SlimeSapling extends BlockSapling
{
    public IIcon[] icons;
    public String[] textureNames = new String[] { "blue" };

    public SlimeSapling()
    {
        super();
        float f = 0.4F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
        this.setHardness(0.0F);
        this.setStepSound(Block.soundTypeGrass);
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:slimesapling_" + textureNames[i]);
        }
    }

    @Override
    public boolean canPlaceBlockOn (Block id)
    {
        return id == Blocks.grass || id == Blocks.dirt || id == TinkerWorld.slimeGrass || id == TinkerTools.craftedSoil;
    }

    @Override
    public void updateTick (World world, int x, int y, int z, Random random)
    {
        if (world.isRemote)
        {
            return;
        }
        super.updateTick(world, x, y, z, random);
        int md = world.getBlockMetadata(x, y, z);
        if (random.nextInt(10) == 0 && world.getBlockLightValue(x, y + 1, z) >= 9)
        {
            if ((md & 8) == 0)
                world.setBlockMetadataWithNotify(x, y, z, md | 8, 4);

            else
                growTree(world, x, y, z, random);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        return icons[meta % 8];
    }

    @Override
    public void func_149879_c (World world, int x, int y, int z, Random random)
    {
        boneFertilize(world, x, y, z, random, null);
    }

    public boolean boneFertilize (World world, int x, int y, int z, Random random, EntityPlayer player)
    {
        int meta = world.getBlockMetadata(x, y, z);

        if (meta % 8 == 0 && (player == null || !player.capabilities.isCreativeMode))
            return false;

        if ((meta & 8) == 0)
        {
            world.setBlockMetadataWithNotify(x, y, z, meta | 8, 4);
        }
        else
        {
            this.growTree(world, x, y, z, random);
        }

        return true;
    }

    public void growTree (World world, int x, int y, int z, Random random)
    {
        int md = world.getBlockMetadata(x, y, z) % 8;
        world.setBlock(x, y, z, Blocks.air);
        WorldGenerator obj = null;

        obj = new SlimeTreeGen(true, 5, 4, 1, 0);

        if (!(obj.generate(world, random, x, y, z)))
            world.setBlock(x, y, z, this, md + 8, 3);
    }

    public int damageDropped (int i)
    {
        return i % 8;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks (Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < 1; i++)
            par3List.add(new ItemStack(par1, 1, i));
    }
}