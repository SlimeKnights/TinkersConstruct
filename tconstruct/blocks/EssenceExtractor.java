package tconstruct.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;
import tconstruct.blocks.logic.EssenceExtractorLogic;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EssenceExtractor extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    private Icon field_94461_a;
    @SideOnly(Side.CLIENT)
    private Icon field_94460_b;

    public EssenceExtractor(int par1)
    {
        super(par1, Material.rock);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
        this.setLightOpacity(0);
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        super.randomDisplayTick(par1World, par2, par3, par4, par5Random);

        for (int l = par2 - 2; l <= par2 + 2; ++l)
        {
            for (int i1 = par4 - 2; i1 <= par4 + 2; ++i1)
            {
                if (l > par2 - 2 && l < par2 + 2 && i1 == par4 - 1)
                {
                    i1 = par4 + 2;
                }

                if (par5Random.nextInt(16) == 0)
                {
                    for (int j1 = par3; j1 <= par3 + 1; ++j1)
                    {
                        if (par1World.getBlockId(l, j1, i1) == Block.bookShelf.blockID)
                        {
                            if (!par1World.isAirBlock((l - par2) / 2 + par2, j1, (i1 - par4) / 2 + par4))
                            {
                                break;
                            }

                            par1World.spawnParticle("enchantmenttable", (double)par2 + 0.5D, (double)par3 + 2.0D, (double)par4 + 0.5D, (double)((float)(l - par2) + par5Random.nextFloat()) - 0.5D, (double)((float)(j1 - par3) - par5Random.nextFloat() - 1.0F), (double)((float)(i1 - par4) + par5Random.nextFloat()) - 0.5D);
                        }
                    }
                }
            }
        }
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(int par1, int par2)
    {
        return par1 == 0 ? this.field_94460_b : (par1 == 1 ? this.field_94461_a : this.blockIcon);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World par1World)
    {
        return new EssenceExtractorLogic();
    }
    
    /* Right-click to form a crystal */

    @Override
    public void onBlockClicked (World world, int x, int y, int z, EntityPlayer player)
    {
        if (world.isRemote)
        {
            return;
        }
        else
        {
            EssenceExtractorLogic logic = (EssenceExtractorLogic)world.getBlockTileEntity(x, y, z);
            int amount = logic.removeEssence();
            if (amount > 0)
            {
                ItemStack crystal = new ItemStack(TContent.essenceCrystal, 1);
                NBTTagCompound tags = new NBTTagCompound();
                tags.setInteger("Essence", amount);
                crystal.setTagCompound(tags);
                
                EntityItem entityitem = new EntityItem(world, player.posX, player.posY - 1.0D, player.posZ, crystal);
                world.spawnEntityInWorld(entityitem);
                if (!(player instanceof FakePlayer))
                    entityitem.onCollideWithPlayer(player);
            }
        }
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        if (par1World.isRemote)
        {
            return true;
        }
        else
        {
            EssenceExtractorLogic logic = (EssenceExtractorLogic)par1World.getBlockTileEntity(x, y, z);
            logic.addEssence(player);
            logic.getEssenceMessage(player);
            return true;
        }
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving, ItemStack par6ItemStack)
    {
        super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLiving, par6ItemStack);

        if (par6ItemStack.hasDisplayName())
        {
            ((EssenceExtractorLogic)par1World.getBlockTileEntity(par2, par3, par4)).func_94134_a(par6ItemStack.getDisplayName());
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("tinker:extractor_side");
        this.field_94461_a = par1IconRegister.registerIcon("tinker:extractor_top");
        this.field_94460_b = par1IconRegister.registerIcon("tinker:extractor_bottom");
    }
}
