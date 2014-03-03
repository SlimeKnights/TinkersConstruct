package tconstruct.library.tools;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class Weapon extends ToolCore
{

    public Weapon(int itemID, int baseDamage)
    {
        super(itemID, baseDamage);
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
    public float getStrVsBlock (ItemStack stack, Block block, int meta)
    {
        if (stack.getTagCompound().getCompoundTag("InfiTool").getBoolean("Broken"))
            return 0.1f;

        for (int i = 0; i < web.length; i++)
        {
            if (web[i] == block.blockMaterial)
            {
                return effectiveSpeed();
            }
        }
        return baseSpeed();
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction (ItemStack par1ItemStack)
    {
        return EnumAction.block;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration (ItemStack par1ItemStack)
    {
        return 72000;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        return false;
    }

    /**
     * Returns if the item (tool) can harvest results from the block type.
     */
    public boolean canHarvestBlock (Block block)
    {
        for (int i = 0; i < web.length; i++)
        {
            if (block.blockMaterial == web[i])
                return true;
        }
        return super.canHarvestBlock(block);
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

    /*@Override
    public boolean onLeftClickEntity (ItemStack stack, EntityPlayer player, Entity entity)
    {
        TContent.modL.midStreamModify(stack);
        return super.onLeftClickEntity(stack, player, entity);
    }*/

    @Override
    public String[] toolCategories ()
    {
        return new String[] { "weapon", "melee" };
    }

    public static Material[] web = new Material[] { Material.web };
    public static Material[] none = new Material[0];

}
