package tconstruct.blocks.slime;

import cpw.mods.fml.relauncher.*;
import java.util.*;
import mantle.blocks.MantleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.library.TConstructRegistry;
import tconstruct.tools.TinkerTools;

public class SlimeGrass extends MantleBlock
{
    public String[] textureNames = { "slimegrass_green_top", "slimedirt_blue", "slimegrass_green_blue_side", "slimegrass_green_dirt_side" };// green,
                                                                                                                                            // purple
    public IIcon[] icons;

    public SlimeGrass()
    {
        super(Material.grass);
        setHardness(0.6f);
        this.setTickRandomly(true);
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    @Override
    public int damageDropped (int meta)
    {
        if (meta == 1) // dirt
            return 0;

        else
            // slime dirt
            return 5;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        if (meta >= 2)
            meta = 0;

        if (side == 0)
        {
            return meta % 2 == 1 ? Blocks.dirt.getIcon(0, 0) : icons[1];
        }
        else if (side == 1)
        {
            return icons[0];
        }
        else
        {
            return icons[meta + 2];
        }
    }

    @Override
    public void getSubBlocks (Item b, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 1; iter++)
        {
            list.add(new ItemStack(b, 1, iter));
        }
    }

    @Override
    public boolean canSustainPlant (IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plant)
    {
        EnumPlantType plantType = plant.getPlantType(world, x, y + 1, z);
        return plantType == EnumPlantType.Plains && plant.getPlant(world, x, y + 1, z) != Blocks.tallgrass;
    }

    @Override
    public void onPlantGrow (World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ)
    {
        world.setBlock(x, y, z, TinkerTools.craftedSoil, 5, 3);
    }

    @Override
    public void updateTick (World world, int x, int y, int z, Random random)
    {
        if (!world.isRemote)
        {
            if (world.getBlockLightValue(x, y + 1, z) < 4 && world.getBlockLightOpacity(x, y + 1, z) > 2)
            {
                world.setBlock(x, y, z, TinkerTools.craftedSoil, 5, 3);
            }
            else if (world.getBlockLightValue(x, y + 1, z) >= 9)
            {
                for (int l = 0; l < 4; ++l)
                {
                    int posX = x + random.nextInt(3) - 1;
                    int posY = y + random.nextInt(5) - 3;
                    int posZ = z + random.nextInt(3) - 1;
                    Block blockAbove = world.getBlock(posX, posY + 1, posZ);

                    if (world.getBlockLightValue(posX, posY + 1, posZ) >= 4 && world.getBlockLightOpacity(posX, posY + 1, posZ) <= 2 && blockAbove != TinkerTools.craftedSoil && blockAbove != this)
                    {
                        Block block = world.getBlock(posX, posY, posZ);
                        if (block == Blocks.dirt)
                        {
                            world.setBlock(posX, posY, posZ, (Block) this, 1, 3);
                            return;
                        }
                        int blockMeta = world.getBlockMetadata(posX, posY, posZ);
                        if (block == TinkerTools.craftedSoil)
                        {
                            if (blockMeta == 5)
                                world.setBlock(posX, posY, posZ, (Block) this, 0, 3);
                        }
                    }
                }
            }
        }
    }

    public Block blockDropped (int metadata, Random random, int fortune)
    {
        if (metadata == 1)
            return Blocks.dirt;
        else
            return TinkerTools.craftedSoil;
    }
}
