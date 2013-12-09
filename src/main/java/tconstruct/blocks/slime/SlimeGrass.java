package tconstruct.blocks.slime;

import java.util.List;
import java.util.Random;

import mantle.blocks.MantleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import tconstruct.common.TContent;
import tconstruct.common.TRepo;
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlimeGrass extends MantleBlock
{
    public String[] textureNames = { "slimegrass_green_top", "slimedirt_blue", "slimegrass_green_blue_side", "slimegrass_green_dirt_side" };//green, purple
    public Icon[] icons;

    public SlimeGrass(int id)
    {
        super(id, Material.grass);
        setHardness(0.6f);
        this.setTickRandomly(true);
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    @Override
    public int damageDropped (int meta)
    {
        if (meta == 1) //dirt
            return 0;

        else
            //slime dirt
            return 5;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int meta)
    {
        if (meta >= 2)
            meta = 0;

        if (side == 0)
        {
            return meta % 2 == 1 ? Block.dirt.getIcon(0, 0) : icons[1];
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
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 1; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    @Override
    public boolean canSustainPlant (World world, int x, int y, int z, ForgeDirection direction, IPlantable plant)
    {
        EnumPlantType plantType = plant.getPlantType(world, x, y + 1, z);
        return plantType == EnumPlantType.Plains && plant.getPlantID(world, x, y + 1, z) != Block.tallGrass.blockID;
    }

    public void onPlantGrow (World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ)
    {
        world.setBlock(x, y, z, TRepo.craftedSoil.blockID, 5, 3);
    }

    public void updateTick (World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (!par1World.isRemote)
        {
            if (par1World.getBlockLightValue(par2, par3 + 1, par4) < 4 && par1World.getBlockLightOpacity(par2, par3 + 1, par4) > 2)
            {
                par1World.setBlock(par2, par3, par4, TRepo.craftedSoil.blockID, 5, 3);
            }
            else if (par1World.getBlockLightValue(par2, par3 + 1, par4) >= 9)
            {
                for (int l = 0; l < 4; ++l)
                {
                    int posX = par2 + par5Random.nextInt(3) - 1;
                    int posY = par3 + par5Random.nextInt(5) - 3;
                    int posZ = par4 + par5Random.nextInt(3) - 1;
                    int l1 = par1World.getBlockId(posX, posY + 1, posZ);

                    if (par1World.getBlockLightValue(posX, posY + 1, posZ) >= 4 && par1World.getBlockLightOpacity(posX, posY + 1, posZ) <= 2)
                    {
                        int blockID = par1World.getBlockId(posX, posY, posZ);
                        if (blockID == Block.dirt.blockID)
                        {
                            par1World.setBlock(posX, posY, posZ, this.blockID, 1, 3);
                            return;
                        }
                        int blockMeta = par1World.getBlockMetadata(posX, posY, posZ);
                        if (blockID == TRepo.craftedSoil.blockID)
                        {
                            if (blockMeta == 5)
                                par1World.setBlock(posX, posY, posZ, this.blockID, 0, 3);
                        }
                    }
                }
            }
        }
    }

    public int idDropped (int metadata, Random random, int fortune)
    {
        if (metadata == 1)
            return TRepo.craftedSoil.blockID;
        else
            return Block.dirt.blockID;
    }
}
