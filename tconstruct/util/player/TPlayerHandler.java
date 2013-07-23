package tconstruct.util.player;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumEntitySize;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import tconstruct.common.TContent;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.util.PHConstruct;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class TPlayerHandler implements IPlayerTracker
{
    /* Player */
    //public int hunger;
    public ConcurrentHashMap<String, TPlayerStats> playerStats = new ConcurrentHashMap<String, TPlayerStats>();

    @Override
    public void onPlayerLogin (EntityPlayer entityplayer)
    {
        //System.out.println("Player: "+entityplayer);
        //Lookup player
        NBTTagCompound tags = entityplayer.getEntityData();
        if (!tags.hasKey("TConstruct"))
        {
            tags.setCompoundTag("TConstruct", new NBTTagCompound());
        }
        TPlayerStats stats = new TPlayerStats();
        stats.player = new WeakReference<EntityPlayer>(entityplayer);
        stats.armor = new ArmorExtended();
        stats.armor.init(entityplayer);
        stats.armor.readFromNBT(entityplayer);

        stats.knapsack = new KnapsackInventory();
        stats.knapsack.init(entityplayer);
        stats.knapsack.readFromNBT(entityplayer);

        stats.level = entityplayer.experienceLevel;
        stats.hunger = entityplayer.getFoodStats().getFoodLevel();
        stats.beginnerManual = tags.getCompoundTag("TConstruct").getBoolean("beginnerManual");
        stats.materialManual = tags.getCompoundTag("TConstruct").getBoolean("materialManual");
        stats.smelteryManual = tags.getCompoundTag("TConstruct").getBoolean("smelteryManual");
        if (!stats.beginnerManual)
        {
            stats.beginnerManual = true;
            tags.getCompoundTag("TConstruct").setBoolean("beginnerManual", true);
            if (PHConstruct.beginnerBook)
            {
                ItemStack diary = new ItemStack(TContent.manualBook);
                if (!entityplayer.inventory.addItemStackToInventory(diary))
                {
                    AbilityHelper.spawnItemAtPlayer(entityplayer, diary);
                }
            }
        }

        playerStats.put(entityplayer.username, stats);

        if (PHConstruct.gregtech)
        {
            PHConstruct.gregtech = false;
            if (PHConstruct.lavaFortuneInteraction)
            {
                entityplayer.addChatMessage("Warning: Cross-mod Exploit Present!");
                entityplayer.addChatMessage("Solution 1: Disable Reverse Smelting recipes from GregTech.");
                entityplayer.addChatMessage("Solution 2: Disable Auto-Smelt/Fortune interaction from TConstruct.");
            }
            entityplayer.addChatMessage("Warning: Cross-mod Exploit Present!");
        }
        
        updatePlayerInventory(entityplayer, stats);
    }

    void updatePlayerInventory (EntityPlayer entityplayer, TPlayerStats stats)
    {

    }

    void updateClientPlayer (ByteArrayOutputStream bos, EntityPlayer player)
    {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "TConstruct";
        packet.data = bos.toByteArray();
        packet.length = bos.size();

        PacketDispatcher.sendPacketToPlayer(packet, (Player) player);
    }

    @Override
    public void onPlayerLogout (EntityPlayer entityplayer)
    {
        savePlayerStats(entityplayer, true);
    }

    @Override
    public void onPlayerChangedDimension (EntityPlayer entityplayer)
    {
        savePlayerStats(entityplayer, false);
    }

    void savePlayerStats (EntityPlayer player, boolean clean)
    {
        if (player != null)
        {
            TPlayerStats stats = getPlayerStats(player.username);
            if (stats != null && stats.armor != null)
            {
                stats.armor.saveToNBT(player);
                stats.knapsack.saveToNBT(player);
                if (clean)
                    playerStats.remove(player.username);
            }
            else
            //Revalidate all players
            {

            }
        }
    }

    @Override
    public void onPlayerRespawn (EntityPlayer entityplayer)
    {
        //Boom!
        TPlayerStats stats = getPlayerStats(entityplayer.username);
        stats.player = new WeakReference<EntityPlayer>(entityplayer);
        stats.armor.recalculateHealth(entityplayer, stats);

        /*TFoodStats food = new TFoodStats();
        entityplayer.foodStats = food;*/

        if (PHConstruct.keepLevels)
            entityplayer.experienceLevel = stats.level;
        if (PHConstruct.keepHunger)
            entityplayer.getFoodStats().addStats(-1 * (20 - stats.hunger), 0);
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
            evt.distance -= 1;
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
        //System.out.println("Stats: "+stats);
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
        //System.out.println("Size: " + height);
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

}
