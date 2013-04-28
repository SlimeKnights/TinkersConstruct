package mods.tinker.tconstruct.util.player;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import mods.tinker.tconstruct.client.TProxyClient;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.AbilityHelper;
import mods.tinker.tconstruct.util.PHConstruct;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumEntitySize;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.relauncher.Side;

public class TPlayerHandler implements IPlayerTracker
{
	/* Player */
	//public int hunger;
	public ConcurrentHashMap<String, TPlayerStats> playerStats = new ConcurrentHashMap<String, TPlayerStats>();

	@Override
	public void onPlayerLogin (EntityPlayer entityplayer)
	{
		//Lookup player
	    TFoodStats food = new TFoodStats();
	    food.readStats(entityplayer.foodStats);
	    entityplayer.foodStats = food;
		NBTTagCompound tags = entityplayer.getEntityData();
		if (!tags.hasKey("TConstruct"))
		{
			tags.setCompoundTag("TConstruct", new NBTTagCompound());
		}
		TPlayerStats stats = new TPlayerStats();
		stats.player = new WeakReference<EntityPlayer>(entityplayer);
        stats.armor = new ArmorExtended();
        stats.armor.init(entityplayer);
        stats.armor.loadFromNBT(entityplayer);
        
		stats.level = entityplayer.experienceLevel;
		stats.health = entityplayer.maxHealth;
		stats.hunger = entityplayer.getFoodStats().getFoodLevel();
		stats.beginnerManual = tags.getCompoundTag("TConstruct").getBoolean("beginnerManual");
		stats.materialManual = tags.getCompoundTag("TConstruct").getBoolean("materialManual");
		stats.smelteryManual = tags.getCompoundTag("TConstruct").getBoolean("smelteryManual");
		if (!stats.beginnerManual)
		{
			tags.getCompoundTag("TConstruct").setBoolean("beginnerManual", true);
			ItemStack diary = new ItemStack(TContent.manualBook);
			if (!entityplayer.inventory.addItemStackToInventory(diary))
			{
				AbilityHelper.spawnItemAtPlayer(entityplayer, diary);
			}
		}

		playerStats.put(entityplayer.username, stats);
	}

	@Override
	public void onPlayerLogout (EntityPlayer entityplayer)
	{
		getPlayerStats(entityplayer.username).armor.saveToNBT(entityplayer);
		playerStats.remove(entityplayer.username);
	}

	@Override
	public void onPlayerChangedDimension (EntityPlayer entityplayer)
	{
		//Nothing?

	}

	@Override
	public void onPlayerRespawn (EntityPlayer entityplayer)
	{
		//Boom!
		TPlayerStats stats = getPlayerStats(entityplayer.username);
		stats.player = new WeakReference<EntityPlayer>(entityplayer);

        TFoodStats food = new TFoodStats();
        entityplayer.foodStats = food;
		
		if (PHConstruct.keepLevels)
			entityplayer.experienceLevel = stats.level;
		if (PHConstruct.keepHunger)
			entityplayer.getFoodStats().addStats(-1*(20 - stats.hunger), 0);
		NBTTagCompound tags = entityplayer.getEntityData();
		NBTTagCompound tTag = new NBTTagCompound();
		tTag.setBoolean("beginnerManual", stats.beginnerManual);
		tTag.setBoolean("materialManual", stats.materialManual);
		tTag.setBoolean("smelteryManual", stats.smelteryManual);
		tags.setCompoundTag("TConstruct", tTag);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.CLIENT)
		{
			//TProxyClient.controlInstance.resetControls();
			if (PHConstruct.keepHunger)
				entityplayer.getFoodStats().setFoodLevel(stats.hunger);
		}
	}

	@ForgeSubscribe
	public void livingFall (LivingFallEvent evt) //Only for negating fall damage
	{
		if (evt.entityLiving instanceof EntityPlayer)
		{
			/*Side side = FMLCommonHandler.instance().getEffectiveSide();
			if (side == Side.CLIENT)
			{
				TProxyClient.controlInstance.landOnGround();
			}*/
			//else
			//System.out.println("Server side");

			evt.distance -= 1;
			//evt.distance = 0;
			//TPlayerStats stats = playerStats.get(((EntityPlayer)evt.entityLiving).username);
			//stats.prevMotionY = evt.entityLiving.motionY;
			//evt.entityLiving.motionY = 10.2;
		}
	}

	/*@ForgeSubscribe
	public void livingUpdate (LivingUpdateEvent evt)
	{
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.CLIENT && evt.entityLiving instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) evt.entityLiving;
			TPlayerStats stats = playerStats.get(player.username);
			if (player.onGround != stats.prevOnGround)
			{
				if (player.onGround)// && -stats.prevMotionY > 0.1)
					//player.motionY = 0.5;
					player.motionY = -stats.prevMotionY * 0.8;
					//player.motionY *= -1.2;
				stats.prevOnGround = player.onGround;
				//if ()
					
				//System.out.println("Fall: "+player.fallDistance);
			}
		}
	}*/

	@ForgeSubscribe
	public void playerDrops (PlayerDropsEvent evt)
	{
		TPlayerStats stats = getPlayerStats(evt.entityPlayer.username);
		stats.level = evt.entityPlayer.experienceLevel / 2;
		//stats.health = 20;
		int hunger = evt.entityPlayer.getFoodStats().getFoodLevel();
		if (hunger < 6)
			stats.hunger = 6;
		else
			stats.hunger = evt.entityPlayer.getFoodStats().getFoodLevel();
	}

	/* Find the right player */
	public TPlayerStats getPlayerStats (String username)
	{
		TPlayerStats stats = playerStats.get(username);
        System.out.println("Stats: "+stats);
		if (stats == null)
		{
			stats = new TPlayerStats();
			playerStats.put(username, stats);
		}
		return stats;
	}

	public EntityPlayer getEntityPlayer (String username)
	{
		TPlayerStats stats = playerStats.get(username);
		if (stats == null)
		{
			return null;
		}
		else
		{
			return stats.player.get();
		}
	}

	/* Modify Player */
	public void updateSize (String user, float offset)
	{
		/*EntityPlayer player = getEntityPlayer(user);
		setEntitySize(0.6F, offset, player);
		player.yOffset = offset - 0.18f;*/
	}

	public static void setEntitySize (float width, float height, Entity entity)
	{
		System.out.println("Size: " + height);
		if (width != entity.width || height != entity.height)
		{
			entity.width = width;
			entity.height = height;
			entity.boundingBox.maxX = entity.boundingBox.minX + (double) entity.width;
			entity.boundingBox.maxZ = entity.boundingBox.minZ + (double) entity.width;
			entity.boundingBox.maxY = entity.boundingBox.minY + (double) entity.height;
		}

		float que = width % 2.0F;

		if ((double) que < 0.375D)
		{
			entity.myEntitySize = EnumEntitySize.SIZE_1;
		}
		else if ((double) que < 0.75D)
		{
			entity.myEntitySize = EnumEntitySize.SIZE_2;
		}
		else if ((double) que < 1.0D)
		{
			entity.myEntitySize = EnumEntitySize.SIZE_3;
		}
		else if ((double) que < 1.375D)
		{
			entity.myEntitySize = EnumEntitySize.SIZE_4;
		}
		else if ((double) que < 1.75D)
		{
			entity.myEntitySize = EnumEntitySize.SIZE_5;
		}
		else
		{
			entity.myEntitySize = EnumEntitySize.SIZE_6;
		}
		//entity.yOffset = height;
	}

	Random rand = new Random();

	/* Bows */
	/*@ForgeSubscribe
	public void arrowShoot (ArrowLooseEvent event)
	{
		event.setCanceled(true);
		int j = event.charge;

		boolean flag = event.entityPlayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, event.bow) > 0;

		if (flag || event.entityPlayer.inventory.hasItem(Item.arrow.itemID))
		{
			float f = (float) j / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;

			if ((double) f < 0.1D)
			{
				return;
			}

			if (f > 1.0F)
			{
				f = 1.0F;
			}

			EntityArrow entityarrow = new EntityArrow(event.entityPlayer.worldObj, event.entityPlayer, f * 2.0F);

			if (f == 1.0F)
			{
				entityarrow.setIsCritical(true);
			}

			int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, event.bow);

			entityarrow.setDamage(1.5D + k * 0.45D);

			int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, event.bow);

			if (l > 0)
			{
				entityarrow.setKnockbackStrength(l);
			}

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, event.bow) > 0)
			{
				entityarrow.setFire(100);
			}

			event.bow.damageItem(1, event.entityPlayer);
			event.entityPlayer.worldObj.playSoundAtEntity(event.entityPlayer, "random.bow", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

			if (flag)
			{
				entityarrow.canBePickedUp = 2;
			}
			else
			{
				event.entityPlayer.inventory.consumeInventoryItem(Item.arrow.itemID);
			}

			if (!event.entityPlayer.worldObj.isRemote)
			{
				event.entityPlayer.worldObj.spawnEntityInWorld(entityarrow);
			}
		}
	}*/
}
