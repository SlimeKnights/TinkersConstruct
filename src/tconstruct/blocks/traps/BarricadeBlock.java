package tconstruct.blocks.traps;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tconstruct.client.block.BarricadeRender;
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BarricadeBlock extends Block
{
    Block modelBlock;
    int modelMeta;

    public BarricadeBlock(int blockID, Block model, int meta)
    {
        super(blockID, Material.wood);
        this.modelBlock = model;
        this.modelMeta = meta;
        setHardness(4.0F);
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    public Icon getIcon (int side, int meta)
    {
        return modelBlock.getIcon(2, modelMeta);
    }

    @Override
    public void registerIcons (IconRegister par1IconRegister)
    {

    }

    public boolean renderAsNormalBlock ()
    {
        return false;
    }

    public boolean isOpaqueCube ()
    {
        return false;
    }

    public int getRenderType ()
    {
        return BarricadeRender.model;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool (World par1World, int x, int y, int z)
    {
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
    }

    public void setBlockBoundsBasedOnState (IBlockAccess par1IBlockAccess, int x, int y, int z)
    {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void harvestBlock (World world, EntityPlayer player, int x, int y, int z, int meta)
    {
        if (meta % 4 > 0)
        {
            world.setBlock(x, y, z, this.blockID, meta - 1, 3);
            dropBlockAsItem_do(world, x, y, z, new ItemStack(this));
        }
        else
        {
            dropBlockAsItem_do(world, x, y, z, new ItemStack(this));
        }
    }

    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        ItemStack stack = player.getCurrentEquippedItem();
        if ((stack != null) && (stack.itemID == this.blockID) && (!player.isSneaking()))
        {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta % 4 != 3)
            {
                world.setBlock(x, y, z, this.blockID, meta + 1, 3);
                Block.blocksList[this.blockID].onBlockPlacedBy(world, x, y, z, player, stack);
                Block.blocksList[this.blockID].onPostBlockPlaced(world, x, y, z, meta);

                Block var9 = Block.blocksList[this.blockID];
                world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, var9.stepSound.getStepSound(), (var9.stepSound.getVolume() + 1.0F) / 2.0F, var9.stepSound.getPitch() * 0.8F);
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
            world.setBlockToAir(x, y, z);
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