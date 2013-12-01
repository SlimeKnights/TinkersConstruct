package tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import tconstruct.client.block.SlimePadRender;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlimePad extends Block
{

    public SlimePad(int par1, Material par2Material)
    {
        super(par1, par2Material);
        setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 0.625F, 0.875F);
        this.setCreativeTab(TConstructRegistry.blockTab);
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

            double speed = 0.25;

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

            if (entity instanceof EntityItem)
            {
                entity.posY += 1;
            }
            entity.addVelocity(moveX, speed * 2, moveZ);
            world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, this.stepSound.getStepSound(), (this.stepSound.getVolume()) / 2.0F, this.stepSound.getPitch() * 0.65F);
        }
    }

    public boolean isBlockReplaceable (World world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
    {
        int face = MathHelper.floor_double((double) (entity.rotationYaw * 8.0F / 360.0F) + 0.5D) + (entity.isSneaking() ? 4 : 0) & 7;
        int meta = world.getBlockMetadata(x, y, z) & 8;
        world.setBlockMetadataWithNotify(x, y, z, face | meta, 2);
    }

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
        return SlimePadRender.model;
    }

    @Override
    public void registerIcons (IconRegister iconRegister)
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int meta)
    {
        return TContent.slimeGel.getIcon(side, 1);
    }

    public Icon getFluidIcon (int meta)
    {
        return TContent.slimeChannel.getIcon(2, 0);
    }

    public Icon getNubIcon (int meta)
    {
        return TContent.slimeGel.getIcon(0, 0);
    }
}
