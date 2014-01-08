package tconstruct.blocks;

import java.util.List;
import java.util.Random;

import tconstruct.client.block.OreberryRender;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.AbilityHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class OreberryBush extends BlockLeavesBase implements IPlantable
{
    Random random;
    public Icon[] fastIcons;
    public Icon[] fancyIcons;
    public String[] textureNames;
    public String[] oreTypes;
    public int itemMeat;
    private int subitems;

    public OreberryBush(int id, String[] textureNames, int meta, int sub, String[] oreTypes)
    {
        super(id, Material.leaves, false);
        this.textureNames = textureNames;
        this.itemMeat = meta;
        this.subitems = sub;
        this.oreTypes = oreTypes;
        this.setTickRandomly(true);
        random = new Random();
        this.setHardness(0.3F);
        this.setStepSound(Block.soundMetalFootstep);
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    /* Berries show up at meta 12-15 */

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        this.fastIcons = new Icon[textureNames.length];
        this.fancyIcons = new Icon[textureNames.length];

        for (int i = 0; i < this.fastIcons.length; i++)
        {
            if (textureNames[i] != "")
            {
                this.fastIcons[i] = iconRegister.registerIcon("tinker:crops/" + textureNames[i] + "_fast");
                this.fancyIcons[i] = iconRegister.registerIcon("tinker:crops/" + textureNames[i] + "_fancy");
            }
        }
    }

    @Override
    public Icon getIcon (int side, int metadata)
    {
        if (graphicsLevel)
        {
            if (metadata < 12)
            {
                return fancyIcons[metadata % 4];
            }
            else
            {
                return fancyIcons[metadata % 4 + 4];
            }
        }
        else
        {
            if (metadata < 12)
            {
                return fastIcons[metadata % 4];
            }
            else
            {
                return fastIcons[metadata % 4 + 4];
            }
        }
    }

    /* Bushes are stored by size then type */
    @Override
    public int damageDropped (int metadata)
    {
        return metadata % 4;
    }

    /* The following methods define a berry bush's size depending on metadata */
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool (World world, int x, int y, int z)
    {
        int l = world.getBlockMetadata(x, y, z);
        if (l < 4)
        {
            return AxisAlignedBB.getBoundingBox((double) x + 0.25D, y, (double) z + 0.25D, (double) x + 0.75D, (double) y + 0.5D, (double) z + 0.75D);
        }
        else if (l < 8)
        {
            return AxisAlignedBB.getBoundingBox((double) x + 0.125D, y, (double) z + 0.125D, (double) x + 0.875D, (double) y + 0.75D, (double) z + 0.875D);
        }
        else
        {
            return AxisAlignedBB.getBoundingBox(x + 0.0625, y, z + 0.0625, (double) x + 0.9375D, (double) y + 0.9375D, (double) z + 0.9375D);
        }
    }

    public AxisAlignedBB getSelectedBoundingBoxFromPool (World world, int x, int y, int z)
    {
        int l = world.getBlockMetadata(x, y, z);
        if (l < 4)
        {
            return AxisAlignedBB.getBoundingBox((double) x + 0.25D, y, (double) z + 0.25D, (double) x + 0.75D, (double) y + 0.5D, (double) z + 0.75D);
        }
        else if (l < 8)
        {
            return AxisAlignedBB.getBoundingBox((double) x + 0.125D, y, (double) z + 0.125D, (double) x + 0.875D, (double) y + 0.75D, (double) z + 0.875D);
        }
        else
        {
            return AxisAlignedBB.getBoundingBox(x, y, z, (double) x + 1.0D, (double) y + 1.0D, (double) z + 1.0D);
        }
    }

    @Override
    public void setBlockBoundsBasedOnState (IBlockAccess iblockaccess, int x, int y, int z)
    {
        int md = iblockaccess.getBlockMetadata(x, y, z);

        float minX;
        float minY = 0F;
        float minZ;
        float maxX;
        float maxY;
        float maxZ;

        if (md < 4)
        {
            minX = minZ = 0.25F;
            maxX = maxZ = 0.75F;
            maxY = 0.5F;
        }
        else

        if (md < 8)
        {
            minX = minZ = 0.125F;
            maxX = maxZ = 0.875F;
            maxY = 0.75F;
        }

        else
        {
            minX = minZ = 0.0F;
            maxX = maxZ = 1.0F;
            maxY = 1.0F;
        }
        setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /* Left-click harvests berries */
    @Override
    public void onBlockClicked (World world, int x, int y, int z, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta >= 12)
            {
                world.setBlock(x, y, z, blockID, meta - 4, 3);
                AbilityHelper.spawnItemAtPlayer(player, new ItemStack(TContent.oreBerries, 1, meta % 4 + itemMeat));
            }
        }
    }

    /* Right-click harvests berries */
    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        /*if (world.isRemote)
            return false;*/

        int meta = world.getBlockMetadata(x, y, z);
        if (meta >= 12)
        {
            if (world.isRemote)
                return true;

            world.setBlock(x, y, z, blockID, meta - 4, 3);
            AbilityHelper.spawnItemAtPlayer(player, new ItemStack(TContent.oreBerries, random.nextInt(3) + 1, meta % 4 + itemMeat));
            return true;
        }

        return false;
    }

    /* Render logic */

    @Override
    public boolean isOpaqueCube ()
    {
        return false;
    }

    public void setGraphicsLevel (boolean flag)
    {
        graphicsLevel = flag;
    }

    @Override
    public boolean renderAsNormalBlock ()
    {
        return false;
    }

    public int getRenderType ()
    {
        return OreberryRender.model;
    }

    public boolean shouldSideBeRendered (IBlockAccess iblockaccess, int x, int y, int z, int meta)
    {
        if (meta > 7 || graphicsLevel)
        {
            return super.shouldSideBeRendered(iblockaccess, x, y, z, meta);
        }
        else
        {
            return true;
        }
    }

    /* Bush growth */

    @Override
    public void updateTick (World world, int x, int y, int z, Random random1)
    {
        if (world.isRemote)
        {
            return;
        }

        if (random1.nextInt(20) == 0)// && world.getBlockLightValue(x, y, z) <= 8)
        {
            if (world.getFullBlockLightValue(x, y, z) < 10)
            {
                int meta = world.getBlockMetadata(x, y, z);
                if (meta < 12)
                {
                    world.setBlock(x, y, z, blockID, meta + 4, 3);
                }
            }
            /*else if (meta < 8)
            {
            	world.setBlock(x, y, z, blockID, meta + 4, 3);
            }*/
        }
    }

    public boolean canSustainPlant (World world, int x, int y, int z, ForgeDirection direction, IPlantable plant)
    {
        if (plant instanceof OreberryBush)
            return (world.getBlockMetadata(x, y, z) > 7);
        return super.canSustainPlant(world, x, y, z, direction, plant);
    }

    public boolean canPlaceBlockAt (World world, int x, int y, int z)
    {
        if (world.getFullBlockLightValue(x, y, z) < 13)
            return super.canPlaceBlockAt(world, x, y, z);
        return false;
    }

    /* Resistance to fire */

    @Override
    public int getFlammability (IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face)
    {
        return 0;
    }

    @Override
    public boolean isFlammable (IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face)
    {
        return false;
    }

    @Override
    public int getFireSpreadSpeed (World world, int x, int y, int z, int metadata, ForgeDirection face)
    {
        return 0;
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks (int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int var4 = 8; var4 < 8 + subitems; ++var4)
        {
            par3List.add(new ItemStack(par1, 1, var4));
        }
    }

    @Override
    public EnumPlantType getPlantType (World world, int x, int y, int z)
    {
        return EnumPlantType.Cave;
    }

    @Override
    public int getPlantID (World world, int x, int y, int z)
    {
        return this.blockID;
    }

    @Override
    public int getPlantMetadata (World world, int x, int y, int z)
    {
        return world.getBlockMetadata(x, y, z) - 4;
    }

    @Override
    public void onEntityCollidedWithBlock (World world, int x, int y, int z, Entity entity)
    {
        if (!(entity instanceof EntityItem))
            entity.attackEntityFrom(DamageSource.cactus, 1);
    }
}
