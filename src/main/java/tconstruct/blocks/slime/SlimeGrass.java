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
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import tconstruct.common.TContent;
import tconstruct.common.TRepo;
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlimeGrass extends MantleBlock
{
    public String[] textureNames = { "slimegrass_green_top", "slimedirt_blue", "slimegrass_green_blue_side", "slimegrass_green_dirt_side" };//green, purple
    public IIcon[] icons;

    public SlimeGrass()
    {
        super(Material.field_151577_b);
        func_149711_c(0.6f);
        this.func_149675_a(true);
        this.func_149647_a(TConstructRegistry.blockTab);
    }

    @Override
    public int func_149692_a (int meta)
    {
        if (meta == 1) //dirt
            return 0;

        else
            //slime dirt
            return 5;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void func_149651_a (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a (int side, int meta)
    {
        if (meta >= 2)
            meta = 0;

        if (side == 0)
        {
            return meta % 2 == 1 ? Blocks.dirt.func_149691_a(0, 0) : icons[1];
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
    public void func_149666_a (Item b, CreativeTabs tab, List list)
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

    public void onPlantGrow (World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ)
    {
        world.func_147465_d(x, y, z, TRepo.craftedSoil, 5, 3);
    }

    public void updateTick (World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (!par1World.isRemote)
        {
            if (par1World.getBlockLightValue(par2, par3 + 1, par4) < 4 && par1World.getBlockLightOpacity(par2, par3 + 1, par4) > 2)
            {
                par1World.func_147465_d(par2, par3, par4, TRepo.craftedSoil, 5, 3);
            }
            else if (par1World.getBlockLightValue(par2, par3 + 1, par4) >= 9)
            {
                for (int l = 0; l < 4; ++l)
                {
                    int posX = par2 + par5Random.nextInt(3) - 1;
                    int posY = par3 + par5Random.nextInt(5) - 3;
                    int posZ = par4 + par5Random.nextInt(3) - 1;
                    Block l1 = par1World.func_147439_a(posX, posY + 1, posZ);

                    if (par1World.getBlockLightValue(posX, posY + 1, posZ) >= 4 && par1World.getBlockLightOpacity(posX, posY + 1, posZ) <= 2)
                    {
                        Block block = par1World.func_147439_a(posX, posY, posZ);
                        if (block == Blocks.dirt)
                        {
                            par1World.func_147465_d(posX, posY, posZ,(Block) this, 1, 3);
                            return;
                        }
                        int blockMeta = par1World.getBlockMetadata(posX, posY, posZ);
                        if (block == TRepo.craftedSoil)
                        {
                            if (blockMeta == 5)
                                par1World.func_147465_d(posX, posY, posZ, (Block) this, 0, 3);
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
            return TRepo.craftedSoil;
    }
}
