package mods.tinker.tconstruct;

import mods.tinker.tconstruct.TContent;
import mods.tinker.tconstruct.library.ToolCore;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class RangedWeapon extends ToolCore
{

	public RangedWeapon(int itemID)
	{
		super(itemID, 0);
	}

	@Override
	public int getHeadType ()
	{
		return 0;
	}

	@Override
	protected Item getHeadItem ()
	{
		return TContent.toolRod;
	}

	@Override
	protected Item getAccessoryItem ()
	{
		return TContent.toolRod;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderPasses (int metadata)
	{
		return 7;
	}
	
	/* Bow usage */
	public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, int par4)
    {
        int var6 = this.getMaxItemUseDuration(par1ItemStack) - par4;

        ArrowLooseEvent event = new ArrowLooseEvent(par3EntityPlayer, par1ItemStack, var6);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return;
        }
        var6 = event.charge;

        boolean var5 = par3EntityPlayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, par1ItemStack) > 0;

        if (var5 || par3EntityPlayer.inventory.hasItem(Item.arrow.itemID))
        {
            float var7 = (float)var6 / 20.0F;
            var7 = (var7 * var7 + var7 * 2.0F) / 3.0F;

            if ((double)var7 < 0.1D)
            {
                return;
            }

            if (var7 > 1.0F)
            {
                var7 = 1.0F;
            }

            EntityArrow var8 = new EntityArrow(par2World, par3EntityPlayer, var7 * 2.0F);

            if (var7 == 1.0F)
            {
                var8.setIsCritical(true);
            }

            int var9 = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, par1ItemStack);

            if (var9 > 0)
            {
                var8.setDamage(var8.getDamage() + (double)var9 * 0.5D + 0.5D);
            }

            int var10 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, par1ItemStack);

            if (var10 > 0)
            {
                var8.setKnockbackStrength(var10);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, par1ItemStack) > 0)
            {
                var8.setFire(100);
            }

            par1ItemStack.damageItem(1, par3EntityPlayer);
            par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + var7 * 0.5F);

            if (var5)
            {
                var8.canBePickedUp = 2;
            }
            else
            {
                par3EntityPlayer.inventory.consumeInventoryItem(Item.arrow.itemID);
            }

            if (!par2World.isRemote)
            {
                par2World.spawnEntityInWorld(var8);
            }
        }
    }

    public ItemStack onFoodEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        return par1ItemStack;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 72000;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        ArrowNockEvent event = new ArrowNockEvent(par3EntityPlayer, par1ItemStack);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return event.result;
        }

        if (par3EntityPlayer.capabilities.isCreativeMode || par3EntityPlayer.inventory.hasItem(Item.arrow.itemID))
        {
            par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        }

        return par1ItemStack;
    }
    
    @SideOnly(Side.CLIENT)
	@Override
	public boolean requiresMultipleRenderPasses ()
	{
		return true;
	}
	
	/* Rendering */
	@SideOnly(Side.CLIENT)
	//@Override
	public int getIconIndex (ItemStack stack, int pass)
	{
		if (!stack.hasTagCompound())
			return 255;

		NBTTagCompound tags = stack.getTagCompound();
		if (tags.hasKey("InfiTool"))
		{
			if (pass == 0) // Handle
			{
				return tags.getCompoundTag("InfiTool").getInteger("RenderHandle");
			}

			if (pass == 1) // Head
			{
				return tags.getCompoundTag("InfiTool").getInteger("RenderHead") + 64;
			}

			if (pass == 2) // Accessory
			{
				if (tags.getCompoundTag("InfiTool").hasKey("RenderAccessory"))
				{
					int index = tags.getCompoundTag("InfiTool").getInteger("RenderAccessory");
					if (index == -1)
						return 32;
					return index + 32;
				}
			}

			if (pass == 4)
			{
				if (tags.getCompoundTag("InfiTool").hasKey("Effect1"))
					return tags.getCompoundTag("InfiTool").getInteger("Effect1") + 224;
				else
					return 255;
			}

			if (pass == 5)
			{
				if (tags.getCompoundTag("InfiTool").hasKey("Effect2"))
					return tags.getCompoundTag("InfiTool").getInteger("Effect2") + 224;
				else
					return 255;
			}

			if (pass == 6)
			{
				if (tags.getCompoundTag("InfiTool").hasKey("Effect3"))
					return tags.getCompoundTag("InfiTool").getInteger("Effect3") + 224;
				else
					return 255;
			}
		}

		return 255; //Keep 255 blank
	}
	/* Animations */
	//@Override
	public int getIconIndex(ItemStack stack, int pass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
		/*
         * Here is an example usage for Vanilla bows.
        if (usingItem != null && usingItem.getItem().itemID == Item.bow.itemID)
        {
            int k = usingItem.getMaxItemUseDuration() - useRemaining;
            if (k >= 18) return 133;
            if (k >  13) return 117;
            if (k >   0) return 101;
        }
         */
		
		/*if (!stack.hasTagCompound())
			return 255;

		NBTTagCompound tags = stack.getTagCompound();
		if (tags.hasKey("InfiTool"))
		{
			if (pass == 0) // Handle
			{
				return tags.getCompoundTag("InfiTool").getInteger("RenderHandle");
			}

			if (pass == 1) // Head
			{
				return tags.getCompoundTag("InfiTool").getInteger("RenderHead") + 64;
			}

			if (pass == 2) // Accessory
			{
				if (tags.getCompoundTag("InfiTool").hasKey("RenderAccessory"))
				{
					int index = tags.getCompoundTag("InfiTool").getInteger("RenderAccessory");
					if (index == -1)
						return 32;
					return index + 32;
				}
			}

			if (pass == 4)
			{
				if (tags.getCompoundTag("InfiTool").hasKey("Effect1"))
					return tags.getCompoundTag("InfiTool").getInteger("Effect1") + 224;
				else
					return 255;
			}

			if (pass == 5)
			{
				if (tags.getCompoundTag("InfiTool").hasKey("Effect2"))
					return tags.getCompoundTag("InfiTool").getInteger("Effect2") + 224;
				else
					return 255;
			}

			if (pass == 6)
			{
				if (tags.getCompoundTag("InfiTool").hasKey("Effect3"))
					return tags.getCompoundTag("InfiTool").getInteger("Effect3") + 224;
				else
					return 255;
			}
		}

		return 255; //Keep 255 blank
		*/
		if (usingItem != null && usingItem.getItem().itemID == this.itemID)
        {
            int k = usingItem.getMaxItemUseDuration() - useRemaining;
            if (k >= 18) return 0;
            if (k >  13) return 1;
            if (k >   0) return 2;
        }
		return 240;
    }

}
