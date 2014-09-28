package tconstruct.library.tools;

import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.world.World;

public abstract class Weapon extends ToolCore
{

    public Weapon(int baseDamage)
    {
        super(baseDamage);
    }

    protected float baseSpeed ()
    {
        return 1.5f;
    }

    protected float effectiveSpeed ()
    {
        return 15f;
    }

    @Override
    public float getDigSpeed (ItemStack stack, Block block, int meta)
    {
        if (stack.getTagCompound().getCompoundTag("InfiTool").getBoolean("Broken"))
            return 0.1f;

        for (int i = 0; i < web.length; i++)
        {
            if (web[i] == block.getMaterial())
            {
                return effectiveSpeed();
            }
        }
        return baseSpeed();
    }

    /**
     * returns the action that specifies what animation to play when the items
     * is being used
     */
    @Override
    public EnumAction getItemUseAction (ItemStack par1ItemStack)
    {
        return EnumAction.block;
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getMaxItemUseDuration (ItemStack par1ItemStack)
    {
        return 72000;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is
     * pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        return false;
    }

    /**
     * Returns if the item (tool) can harvest results from the block type.
     */
    @Override
    public boolean canHarvestBlock (Block block, ItemStack is)
    {
        for (int i = 0; i < web.length; i++)
        {
            if (block.getMaterial() == web[i])
                return true;
        }
        return super.canHarvestBlock(block, is);
    }

    protected Material[] getEffectiveMaterials ()
    {
        return web;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onUpdate (ItemStack stack, World world, Entity entity, int par4, boolean par5)
    {
        super.onUpdate(stack, world, entity, par4, par5);
        if (entity instanceof EntityPlayerSP)
        {
            EntityPlayerSP player = (EntityPlayerSP) entity;
            ItemStack usingItem = player.getItemInUse();
            if (usingItem != null && usingItem.getItem() == this)
            {
                player.movementInput.moveForward *= 2.5F;
                player.movementInput.moveStrafe *= 2.5F;
            }
        }
    }

    @Override
    public String[] getTraits ()
    {
        return new String[] { "weapon", "melee" };
    }

    public static Material[] web = new Material[] { Material.web, Material.cloth, Material.coral, Material.cake };
    public static Material[] none = new Material[0];

}
