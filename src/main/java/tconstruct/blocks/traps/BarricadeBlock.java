package tconstruct.blocks.traps;

import cpw.mods.fml.relauncher.*;
import mantle.blocks.MantleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import tconstruct.library.TConstructRegistry;
import tconstruct.world.model.BarricadeRender;

public class BarricadeBlock extends MantleBlock
{
    Block modelBlock;
    int modelMeta;

    public BarricadeBlock(Block model, int meta)
    {
        super(Material.wood);
        this.modelBlock = model;
        this.modelMeta = meta;
        setHardness(4.0F);
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        return modelBlock.getIcon(2, modelMeta);
    }

    @Override
    public void registerBlockIcons (IIconRegister par1IconRegister)
    {

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
    public int getRenderType ()
    {
        return BarricadeRender.model;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool (World par1World, int x, int y, int z)
    {
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
    }

    @Override
    public void setBlockBoundsBasedOnState (IBlockAccess par1IBlockAccess, int x, int y, int z)
    {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void harvestBlock (World world, EntityPlayer player, int x, int y, int z, int meta)
    {
        if (meta % 4 > 0)
        {
            world.setBlock(x, y, z, this, meta - 1, 3);
            dropBlockAsItem(world, x, y, z, new ItemStack(this));
        }
        else
        {
            dropBlockAsItem(world, x, y, z, new ItemStack(this));
        }
    }

    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        ItemStack stack = player.getCurrentEquippedItem();
        if ((stack != null) && (stack.getItem() == Item.getItemFromBlock(this)) && (!player.isSneaking()))
        {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta % 4 != 3)
            {
                world.setBlock(x, y, z, this, meta + 1, 3);
                this.onBlockPlacedBy(world, x, y, z, player, stack);
                this.onPostBlockPlaced(world, x, y, z, meta);

                Block var9 = this;
                world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, var9.stepSound.soundName, (var9.stepSound.getVolume() + 1.0F) / 2.0F, var9.stepSound.getPitch() * 0.8F);
                player.swingItem();
                if (!player.capabilities.isCreativeMode)
                    stack.stackSize -= 1;

                return true;
            }
        }
        return false;
    }

    @Override
    public void onBlockExploded (World world, int x, int y, int z, Explosion explosion)
    {
        double distance = (x - explosion.explosionX) + (y - explosion.explosionY) + (z - explosion.explosionZ);
        distance = Math.abs(distance);
        double power = (explosion.explosionSize * 2) / distance;
        int meta = world.getBlockMetadata(x, y, z);
        int trueMeta = meta % 4;
        trueMeta -= power;
        if (trueMeta < 0)
            world.setBlock(x, y, z, Blocks.air, 0, 0);
        else
            world.setBlockMetadataWithNotify(x, y, z, (int) (meta - power), 3);
        onBlockDestroyedByExplosion(world, x, y, z, explosion);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered (IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }
}
