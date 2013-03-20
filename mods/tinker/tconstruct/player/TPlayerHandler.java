package mods.tinker.tconstruct.player;

import java.util.HashMap;

import mods.tinker.tconstruct.AbilityHelper;
import mods.tinker.tconstruct.PHConstruct;
import mods.tinker.tconstruct.TContent;
import mods.tinker.tconstruct.client.TProxyClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumEntitySize;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.relauncher.Side;

public class TPlayerHandler implements IPlayerTracker
{
	/* Player */
	//public int hunger;
	public HashMap<String, TPlayerStats> playerStats = new HashMap<String, TPlayerStats>();

	@Override
	public void onPlayerLogin (EntityPlayer entityplayer)
	{
		//Lookup player
		NBTTagCompound tags = entityplayer.getEntityData();
		if (!tags.hasKey("TConstruct"))
		{
			tags.setCompoundTag("TConstruct", new NBTTagCompound());
		}
		TPlayerStats stats = new TPlayerStats();
		stats.player = entityplayer;
		stats.level = entityplayer.experienceLevel;
		stats.health = 20; //More hp in the future
		stats.hunger = entityplayer.getFoodStats().getFoodLevel();
		stats.diary = tags.getCompoundTag("TConstruct").getBoolean("diary");
		if (!stats.diary)
		{
			tags.getCompoundTag("TConstruct").setBoolean("diary", true);
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
		//Save player?
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
		stats.player = entityplayer;
		if (PHConstruct.keepLevels)
			entityplayer.experienceLevel = stats.level;
		if (PHConstruct.keepHunger)
			entityplayer.getFoodStats().setFoodLevel(stats.hunger);
		NBTTagCompound tags = entityplayer.getEntityData().getCompoundTag("TConstruct");
		tags.setBoolean("diary", stats.diary);
		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.CLIENT)
		{
			TProxyClient.controlInstance.resetControls();
		}
	}
	
	@ForgeSubscribe
	public void livingFall (LivingFallEvent evt) //Only for negating fall damage
	{
		if (evt.entityLiving instanceof EntityPlayer)
		{
			Side side = FMLCommonHandler.instance().getEffectiveSide();
			if (side == Side.CLIENT)
			{
				TProxyClient.controlInstance.landOnGround();

				//System.out.println("Client side: "+evt.entityLiving.motionY);
			}
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
	TPlayerStats getPlayerStats (String username)
	{
		TPlayerStats stats = playerStats.get(username);
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
			return stats.player;
		}
	}
	
	/* Modify Player */
	public void updateSize(String user, float offset)
	{
		/*EntityPlayer player = getEntityPlayer(user);
		setEntitySize(0.6F, offset, player);
		player.yOffset = offset - 0.18f;*/
	}
	
	public static void setEntitySize(float width, float height, Entity entity)
    {
		System.out.println("Size: "+height);
        if (width != entity.width || height != entity.height)
        {
            entity.width = width;
            entity.height = height;
            entity.boundingBox.maxX = entity.boundingBox.minX + (double)entity.width;
            entity.boundingBox.maxZ = entity.boundingBox.minZ + (double)entity.width;
            entity.boundingBox.maxY = entity.boundingBox.minY + (double)entity.height;
        }

        float que = width % 2.0F;

        if ((double)que < 0.375D)
        {
            entity.myEntitySize = EnumEntitySize.SIZE_1;
        }
        else if ((double)que < 0.75D)
        {
            entity.myEntitySize = EnumEntitySize.SIZE_2;
        }
        else if ((double)que < 1.0D)
        {
            entity.myEntitySize = EnumEntitySize.SIZE_3;
        }
        else if ((double)que < 1.375D)
        {
            entity.myEntitySize = EnumEntitySize.SIZE_4;
        }
        else if ((double)que < 1.75D)
        {
            entity.myEntitySize = EnumEntitySize.SIZE_5;
        }
        else
        {
            entity.myEntitySize = EnumEntitySize.SIZE_6;
        }
        //entity.yOffset = height;
    }
}
