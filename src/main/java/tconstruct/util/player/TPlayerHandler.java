package tconstruct.util.player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumEntitySize;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import tconstruct.common.TContent;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.util.config.PHConstruct;
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
    public static HashSet<Integer> knapsackDimensions = new HashSet<Integer>();

    @Override
    public void onPlayerLogin (EntityPlayer player)
    {
        //TConstruct.logger.info("Player: "+entityplayer);
        //Lookup player
        NBTTagCompound tags = player.getEntityData();
        if (!tags.hasKey("TConstruct"))
        {
            tags.setCompoundTag("TConstruct", new NBTTagCompound());
        }
        TPlayerStats stats = new TPlayerStats();
        stats.player = new WeakReference<EntityPlayer>(player);
        stats.armor = new ArmorExtended();
        stats.armor.init(player);
        stats.armor.readFromNBT(player);

        stats.knapsack = new KnapsackInventory();
        if (knapsackDimensions.contains(player.dimension))
        {
            stats.knapsack.init(player, "", player.dimension, false);
        }
        else
        {
            stats.knapsack.init(player);
        }
        stats.knapsack.readFromNBT(player);

        stats.level = player.experienceLevel;
        stats.hunger = player.getFoodStats().getFoodLevel();
        stats.previousDimension = player.dimension;
        stats.beginnerManual = tags.getCompoundTag("TConstruct").getBoolean("beginnerManual");
        stats.materialManual = tags.getCompoundTag("TConstruct").getBoolean("materialManual");
        stats.smelteryManual = tags.getCompoundTag("TConstruct").getBoolean("smelteryManual");
        stats.battlesignBonus = tags.getCompoundTag("TConstruct").getBoolean("battlesignBonus");

        if (!PHConstruct.enableHealthRegen)
            player.worldObj.getGameRules().setOrCreateGameRule("naturalRegeneration", "false");

        if (!stats.beginnerManual)
        {
            stats.beginnerManual = true;
            stats.battlesignBonus = true;
            tags.getCompoundTag("TConstruct").setBoolean("beginnerManual", true);
            tags.getCompoundTag("TConstruct").setBoolean("battlesignBonus", true);
            if (PHConstruct.beginnerBook)
            {
                ItemStack diary = new ItemStack(TContent.manualBook);
                if (!player.inventory.addItemStackToInventory(diary))
                {
                    AbilityHelper.spawnItemAtPlayer(player, diary);
                }
            }

            if (player.username.toLowerCase().equals("fudgy_fetus"))
            {
                ItemStack pattern = new ItemStack(TContent.woodPattern, 1, 22);

                NBTTagCompound compound = new NBTTagCompound();
                compound.setCompoundTag("display", new NBTTagCompound());
                compound.getCompoundTag("display").setString("Name", "\u00A7f" + "Fudgy_Fetus' Full Guard Pattern");
                NBTTagList list = new NBTTagList();
                list.appendTag(new NBTTagString("Lore", "\u00A72\u00A7o" + "The creator and the creation"));
                list.appendTag(new NBTTagString("Lore", "\u00A72\u00A7o" + "are united at last!"));
                compound.getCompoundTag("display").setTag("Lore", list);
                pattern.setTagCompound(compound);

                AbilityHelper.spawnItemAtPlayer(player, pattern);
            }

            if (player.username.toLowerCase().equals("zerokyuuni"))
            {
                ItemStack pattern = new ItemStack(Item.stick);

                NBTTagCompound compound = new NBTTagCompound();
                compound.setCompoundTag("display", new NBTTagCompound());
                compound.getCompoundTag("display").setString("Name", "\u00A78" + "Cheaty Inventory");
                NBTTagList list = new NBTTagList();
                list.appendTag(new NBTTagString("Lore", "\u00A72\u00A7o" + "Nyaa~"));
                compound.getCompoundTag("display").setTag("Lore", list);
                pattern.setTagCompound(compound);

                AbilityHelper.spawnItemAtPlayer(player, pattern);
            }

            if (player.username.toLowerCase().equals("zisteau"))
            {
                spawnPigmanModifier(player);
            }
        }
        else
        {
            if (!stats.battlesignBonus)
            {
                stats.battlesignBonus = true;
                ItemStack modifier = new ItemStack(TContent.creativeModifier);
                tags.getCompoundTag("TConstruct").setBoolean("battlesignBonus", true);

                NBTTagCompound compound = new NBTTagCompound();
                compound.setCompoundTag("display", new NBTTagCompound());
                NBTTagList list = new NBTTagList();
                list.appendTag(new NBTTagString("Lore", "Battlesigns were buffed recently."));
                list.appendTag(new NBTTagString("Lore", "This might make up for it."));
                compound.getCompoundTag("display").setTag("Lore", list);
                compound.setString("TargetLock", TContent.battlesign.getToolName());
                modifier.setTagCompound(compound);

                AbilityHelper.spawnItemAtPlayer(player, modifier);

                if (player.username.toLowerCase().equals("zisteau"))
                {
                    spawnPigmanModifier(player);
                }
            }
        }

        playerStats.put(player.username, stats);

        if (PHConstruct.gregtech && Loader.isModLoaded("GregTech-Addon"))
        {
            PHConstruct.gregtech = false;
            if (PHConstruct.lavaFortuneInteraction)
            {
                player.addChatMessage("Warning: Cross-mod Exploit Present!");
                player.addChatMessage("Solution 1: Disable Reverse Smelting recipes from GregTech.");
                player.addChatMessage("Solution 2: Disable Auto-Smelt/Fortune interaction from TConstruct.");
            }
        }

        updatePlayerInventory(player, stats);
    }

    void spawnPigmanModifier (EntityPlayer entityplayer)
    {
        ItemStack modifier = new ItemStack(TContent.creativeModifier);

        NBTTagCompound compound = new NBTTagCompound();
        compound.setCompoundTag("display", new NBTTagCompound());
        compound.getCompoundTag("display").setString("Name", "Zistonian Bonus Modifier");
        NBTTagList list = new NBTTagList();
        list.appendTag(new NBTTagString("Lore", "Zombie Pigmen seem to have a natural affinty"));
        list.appendTag(new NBTTagString("Lore", "for these types of weapons."));
        compound.getCompoundTag("display").setTag("Lore", list);
        compound.setString("TargetLock", TContent.battlesign.getToolName());
        modifier.setTagCompound(compound);

        AbilityHelper.spawnItemAtPlayer(entityplayer, modifier);
    }

    void updatePlayerInventory (EntityPlayer player, TPlayerStats stats)
    {
        updatePlayerInventory(player, stats, true);
    }

    void updatePlayerInventory (EntityPlayer player, TPlayerStats stats, boolean armor)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try
        {
            if (armor)
            {
                outputStream.writeByte(4);
                stats.armor.writeInventoryToStream(outputStream);
            }
            else
            {
                outputStream.writeByte(3);
                stats.knapsack.writeInventoryToStream(outputStream);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

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
    public void onPlayerChangedDimension (EntityPlayer player)
    {
        savePlayerStats(player, false);
        updatePlayerInventory(player, getPlayerStats(player.username));

        //Reload knapsack
        TPlayerStats stats = getPlayerStats(player.username);
        boolean global = knapsackDimensions.contains(player.dimension);
        if (!global || !stats.knapsack.globalKnapsack)
        {
            stats.knapsack.init(player, "", player.dimension, !global);
            stats.knapsack.readFromNBT(player);
            updatePlayerInventory(player, stats, false);
        }
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
        tTag.setBoolean("battlesignBonus", stats.battlesignBonus);
        tags.setCompoundTag("TConstruct", tTag);

        Side side = FMLCommonHandler.instance().getEffectiveSide();
        if (side == Side.CLIENT)
        {
            //TProxyClient.controlInstance.resetControls();
            if (PHConstruct.keepHunger)
                entityplayer.getFoodStats().setFoodLevel(stats.hunger);
        }

        updatePlayerInventory(entityplayer, getPlayerStats(entityplayer.username));
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
    				
    			//TConstruct.logger.info("Fall: "+player.fallDistance);
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
        //TConstruct.logger.info("Stats: "+stats);
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
        //TConstruct.logger.info("Size: " + height);
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
