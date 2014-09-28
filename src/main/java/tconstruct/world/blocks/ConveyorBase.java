package tconstruct.world.blocks;

import cpw.mods.fml.relauncher.*;
import mantle.blocks.MantleBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.*;
import tconstruct.library.TConstructRegistry;
import tconstruct.world.model.SlimeChannelRender;

public class ConveyorBase extends MantleBlock
{
    String texturename;

    public ConveyorBase(Material material, String name)
    {
        super(material);
        this.setCreativeTab(TConstructRegistry.blockTab);
        setBlockBounds(0f, 0f, 0f, 1f, 0.5f, 1f);
        texturename = name;
    }

    public boolean isBlockReplaceable (World world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool (World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public void onEntityCollidedWithBlock (World world, int x, int y, int z, Entity entity)
    {
        if (!world.isBlockIndirectlyGettingPowered(x, y, z))
        {
            double moveX = 0;
            double moveZ = 0;

            double speed = 0.01;

            int meta = world.getBlockMetadata(x, y, z);
            switch (meta % 8)
            {
            case 6:
                moveX += speed;
                break;
            case 7:
                moveX += speed;
                moveZ += speed;
                break;
            case 0:
                moveZ += speed;
                break;
            case 1:
                moveZ += speed;
                moveX -= speed;
                break;
            case 2:
                moveX -= speed;
                break;
            case 3:
                moveX -= speed;
                moveZ -= speed;
                break;
            case 4:
                moveZ -= speed;
                break;
            case 5:
                moveZ -= speed;
                moveX += speed;
                break;
            }

            entity.addVelocity(moveX, 0, moveZ);
        }

        if (entity instanceof EntityItem)
        {
            ((EntityItem) entity).age = 0;
        }
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
    {
        int face = MathHelper.floor_double((double) (entity.rotationYaw * 8.0F / 360.0F) + 0.5D) + (entity.isSneaking() ? 4 : 0) & 7;
        int meta = world.getBlockMetadata(x, y, z) & 8;
        world.setBlockMetadataWithNotify(x, y, z, face | meta, 2);
    }

    @Override
    public boolean shouldSideBeRendered (IBlockAccess world, int x, int y, int z, int side)
    {
        if (side == 1)
            return false;
        return super.shouldSideBeRendered(world, x, y, z, side);
    }

    public IIcon[] icons;

    /* Rendering */
    @Override
    public boolean renderAsNormalBlock ()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube ()
    {
        return false;
    }

    @Override
    public int getRenderBlockPass ()
    {
        return 1;
    }

    @Override
    public int getRenderType ()
    {
        return SlimeChannelRender.model;
    }

    @Override
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        String[] textureNames = new String[] { texturename, texturename + "_flow" };
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
        if (meta >= 8)
            return icons[0];
        return side == 1 ? icons[1] : icons[0];
    }
}
