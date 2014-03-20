package tconstruct.blocks.slime;

import java.util.List;
import java.util.Random;

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
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlimeGrass extends Block
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
        world.setBlock(x, y, z, TContent.craftedSoil.blockID, 5, 3);
    }

    public void updateTick (World world, int x, int y, int z, Random random)
    {
        if (!world.isRemote)
        {
            int aboveID = world.getBlockId(x, y + 1, z);
            if ((world.getBlockLightValue(x, y + 1, z) < 4 && world.getBlockLightOpacity(x, y + 1, z) > 2) || (aboveID == TContent.craftedSoil.blockID || aboveID == this.blockID))
            {
                world.setBlock(x, y, z, TContent.craftedSoil.blockID, 5, 3);
            }
            else if (world.getBlockLightValue(x, y + 1, z) >= 9)
            {
                for (int l = 0; l < 4; ++l)
                {
                    int posX = x + random.nextInt(3) - 1;
                    int posY = y + random.nextInt(5) - 3;
                    int posZ = z + random.nextInt(3) - 1;
                    int l1 = world.getBlockId(posX, posY + 1, posZ);

                    aboveID = world.getBlockId(posX, posY + 1, posZ);
                    if (world.getBlockLightValue(posX, posY + 1, posZ) >= 4 && world.getBlockLightOpacity(posX, posY + 1, posZ) <= 2
                            && (aboveID != TContent.craftedSoil.blockID && aboveID != this.blockID))
                    {
                        int blockID = world.getBlockId(posX, posY, posZ);
                        if (blockID == Block.dirt.blockID)
                        {
                            world.setBlock(posX, posY, posZ, this.blockID, 1, 3);
                            return;
                        }
                        int blockMeta = world.getBlockMetadata(posX, posY, posZ);
                        if (blockID == TContent.craftedSoil.blockID)
                        {
                            if (blockMeta == 5)
                                world.setBlock(posX, posY, posZ, this.blockID, 0, 3);
                        }
                    }
                }
            }
        }
    }

    public int idDropped (int metadata, Random random, int fortune)
    {
        if (metadata == 1)
            return Block.dirt.blockID;
        else
            return TContent.craftedSoil.blockID;
    }
}
