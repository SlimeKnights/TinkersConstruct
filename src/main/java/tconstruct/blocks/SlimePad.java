package tconstruct.blocks;

import mantle.blocks.MantleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import tconstruct.client.block.SlimePadRender;
import tconstruct.common.TContent;
import tconstruct.common.TRepo;
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlimePad extends MantleBlock
{

    public SlimePad(Material par2Material)
    {
        super(par2Material);
        func_149676_a(0.125F, 0.0F, 0.125F, 0.875F, 0.625F, 0.875F);
        this.func_149647_a(TConstructRegistry.blockTab);
    }

    @Override
    public AxisAlignedBB func_149668_a (World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public void func_149670_a (World world, int x, int y, int z, Entity entity)
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
            world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, this.field_149762_H.func_150498_e(), (this.field_149762_H.func_150497_c()) / 2.0F, this.field_149762_H.func_150494_d() * 0.65F);
        }
    }

    public boolean isBlockReplaceable (World world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public void func_149689_a (World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
    {
        int face = MathHelper.floor_double((double) (entity.rotationYaw * 8.0F / 360.0F) + 0.5D) + (entity.isSneaking() ? 4 : 0) & 7;
        int meta = world.getBlockMetadata(x, y, z) & 8;
        world.setBlockMetadataWithNotify(x, y, z, face | meta, 2);
    }

    @Override
    public boolean func_149686_d ()
    {
        return false;
    }

    @Override
    public boolean func_149662_c ()
    {
        return false;
    }

    @Override
    public int func_149701_w ()
    {
        return 1;
    }

    @Override
    public int func_149645_b ()
    {
        return SlimePadRender.model;
    }

    @Override
    public void func_149651_a (IIconRegister iconRegister)
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a (int side, int meta)
    {
        return TRepo.slimeGel.func_149691_a(side, 1);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getFluidIcon (int meta)
    {
        return TRepo.slimeChannel.func_149691_a(2, 0);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getNubIcon (int meta)
    {
        return TRepo.slimeGel.func_149691_a(0, 0);
    }
}
