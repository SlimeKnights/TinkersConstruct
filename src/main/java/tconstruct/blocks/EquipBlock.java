package tconstruct.blocks;

import tconstruct.blocks.logic.EquipLogic;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.library.blocks.InventoryLogic;

import java.util.*;

import cpw.mods.fml.relauncher.*;

import tconstruct.TConstruct;
import tconstruct.blocks.logic.FrypanLogic;
import tconstruct.client.block.FrypanRender;
import tconstruct.library.blocks.InventoryBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class EquipBlock extends InventoryBlock
{

    public EquipBlock(int id, Material material)
    {
        super(id, material);
        this.setHardness(0.3f);
        this.setBlockBounds(0, 0, 0, 1, 0.25f, 1);
        //this.setCreativeTab(ToolConstruct.materialTab);
    }

    @Override
    public String[] getTextureNames ()
    {
        return new String[] { "toolstation_top" };
    }

    public Icon getIcon (int side, int meta)
    {
        return Block.blockIron.getIcon(side, meta);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister par1IconRegister)
    {
        //this.blockIcon = par1iconRegister.registerIcon(Block.blockIron.getUnlocalizedName());
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
        return FrypanRender.frypanModelID;
    }

    @Override
    public int idDropped (int par1, Random par2Random, int par3)
    {
        return 0;
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        return new FrypanLogic();
    }

    public void randomDisplayTick (World world, int x, int y, int z, Random random)
    {
        if (isActive(world, x, y, z))
        {
            float f = (float) x + 0.5F;
            float f1 = (float) y + 0.25F + (random.nextFloat() * 6F) / 16F;
            float f2 = (float) z + 0.5F;
            float f4 = random.nextFloat() * 0.6F - 0.3F;
            world.spawnParticle("smoke", f, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("flame", f, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
        }
    }

    public void onBlockPlacedBy (World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack)
    {
        super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLivingBase, par6ItemStack);
        int i3 = MathHelper.floor_double((par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        int newMeta = 0;

        switch (i3)
        {
        case 3:
            newMeta = 0;
            break;
        case 0:
            newMeta = 3;
            break;
        case 1:
            newMeta = 1;
            break;
        case 2:
            newMeta = 2;
            break;
        }
        par1World.setBlockMetadataWithNotify(par2, par3, par4, newMeta, 2);
    }

    @Override
    public void breakBlock (World par1World, int x, int y, int z, int par5, int meta)
    {
        TileEntity te = par1World.getBlockTileEntity(x, y, z);

        if (te != null && te instanceof EquipLogic)
        {
            EquipLogic logic = (EquipLogic) te;
            for (int iter = 0; iter < logic.getSizeInventory(); ++iter)
            {
                ItemStack stack = iter == 0 ? logic.getEquipmentItem() : logic.getStackInSlot(iter);

                if (stack != null && logic.canDropInventorySlot(iter))
                {
                    float jumpX = rand.nextFloat() * 0.8F + 0.1F;
                    float jumpY = rand.nextFloat() * 0.8F + 0.1F;
                    float jumpZ = rand.nextFloat() * 0.8F + 0.1F;

                    while (stack.stackSize > 0)
                    {
                        int itemSize = rand.nextInt(21) + 10;

                        if (itemSize > stack.stackSize)
                        {
                            itemSize = stack.stackSize;
                        }

                        stack.stackSize -= itemSize;
                        EntityItem entityitem = new EntityItem(par1World, (double) ((float) x + jumpX), (double) ((float) y + jumpY), (double) ((float) z + jumpZ), new ItemStack(stack.itemID,
                                itemSize, stack.getItemDamage()));

                        if (stack.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
                        }

                        float offset = 0.05F;
                        entityitem.motionX = (double) ((float) rand.nextGaussian() * offset);
                        entityitem.motionY = (double) ((float) rand.nextGaussian() * offset + 0.2F);
                        entityitem.motionZ = (double) ((float) rand.nextGaussian() * offset);
                        par1World.spawnEntityInWorld(entityitem);
                    }
                }
            }
        }

        super.breakBlock(par1World, x, y, z, par5, meta);
    }

    public int getLightValue (IBlockAccess world, int x, int y, int z)
    {
        return !isActive(world, x, y, z) ? 0 : 9;
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        return TConstruct.proxy.frypanGuiID;
    }

    @Override
    public Object getModInstance ()
    {
        return TConstruct.instance;
    }
}
