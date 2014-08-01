package tconstruct.blocks.slime;

import java.util.List;
import java.util.Random;

import mantle.blocks.MantleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.library.TConstructRegistry;
import tconstruct.tools.TinkerTools;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
    public void updateTick (World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (!par1World.isRemote)
        {
            if (par1World.getBlockLightValue(par2, par3 + 1, par4) < 4 && par1World.getBlockLightOpacity(par2, par3 + 1, par4) > 2)
            {
                par1World.setBlock(par2, par3, par4, TinkerTools.craftedSoil, 5, 3);
            }
            else if (par1World.getBlockLightValue(par2, par3 + 1, par4) >= 9)
            {
                for (int l = 0; l < 4; ++l)
                {
                    int posX = par2 + par5Random.nextInt(3) - 1;
                    int posY = par3 + par5Random.nextInt(5) - 3;
                    int posZ = par4 + par5Random.nextInt(3) - 1;
                    if (par1World.getBlockLightValue(posX, posY + 1, posZ) >= 4 && par1World.getBlockLightOpacity(posX, posY + 1, posZ) <= 2)
                    {
                        Block block = par1World.getBlock(posX, posY, posZ);
                        if (block == Blocks.dirt)
                        {
                            par1World.setBlock(posX, posY, posZ, (Block) this, 1, 3);
                            return;
                        }
                        int blockMeta = par1World.getBlockMetadata(posX, posY, posZ);
                        if (block == TinkerTools.craftedSoil)
                        {
                            if (blockMeta == 5)
                                par1World.setBlock(posX, posY, posZ, (Block) this, 0, 3);
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
