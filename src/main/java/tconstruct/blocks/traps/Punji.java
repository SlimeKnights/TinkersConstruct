package tconstruct.blocks.traps;

import java.util.Random;

import mantle.blocks.MantleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tconstruct.client.block.PunjiRender;
import tconstruct.common.TRepo;
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Punji extends MantleBlock
{

    public Punji()
    {
        super(Material.plants);
        this.func_149676_a(0.125f, 0, 0.125f, 0.875f, 0.375f, 0.875f);
        this.setStepSound(Block.soundGrassFootstep);
        this.func_149647_a(TConstructRegistry.blockTab);
        this.func_149711_c(3.0f);
    }

    @Override
    public void onEntityCollidedWithBlock (World world, int x, int y, int z, Entity entity)
    {
        if (entity instanceof EntityLivingBase)
        {
            int damage = world.getBlockMetadata(x, y, z) / 2 + 1;
            if (entity.fallDistance > 0)
                damage += ((entity.fallDistance) * 1.5 + 2);
            entity.attackEntityFrom(DamageSource.cactus, damage);
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 1));
        }
    }

    /* Right-click adds sticks */
    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int par6, float hitX, float hitY, float hitZ)
    {
        /*if (world.isRemote)
            return false;*/

        int meta = world.getBlockMetadata(x, y, z);
        if (meta < 4)
        {
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null && stack == new ItemStack(TRepo.punji))
            {
                world.setBlockMetadataWithNotify(x, y, z, meta + 1, 3);
                world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), this.stepSound.getPlaceSound(),
                        (this.stepSound.getVolume() + 1.0F) / 2.0F, this.stepSound.getPitch() * 0.8F);
                player.swingItem();
                if (!player.capabilities.isCreativeMode)
                {
                    stack.stackSize--;
                    if (stack.stackSize <= 0)
                        player.destroyCurrentEquippedItem();
                }
            }
        }
        return true;
    }

    @Override
    public int quantityDropped (int meta, int fortune, Random random)
    {
        return meta + 1;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube ()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock ()
    {
        return false;
    }

    public int getRenderType ()
    {
        return PunjiRender.model;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister iconRegister)
    {
        this.field_149761_L = iconRegister.registerIcon("tinker:punji");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean func_149646_a (IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }

}
