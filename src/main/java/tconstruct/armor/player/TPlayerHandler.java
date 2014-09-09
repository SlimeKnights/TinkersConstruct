package tconstruct.armor.player;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.relauncher.Side;
import java.io.*;
import java.lang.ref.WeakReference;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import mantle.player.PlayerUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.*;
import net.minecraft.entity.Entity.EnumEntitySize;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import tconstruct.TConstruct;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.tools.TinkerTools;
import tconstruct.util.config.PHConstruct;

//TODO: Redesign this class
public class TPlayerHandler
{
    /* Player */
    // public int hunger;

    private ConcurrentHashMap<UUID, TPlayerStats> playerStats = new ConcurrentHashMap<UUID, TPlayerStats>();

    @SubscribeEvent
    public void PlayerLoggedInEvent (PlayerLoggedInEvent event)
    {
        onPlayerLogin(event.player);
    }

    @SubscribeEvent
    public void onPlayerRespawn (PlayerRespawnEvent event)
    {
        onPlayerRespawn(event.player);
    }

    @SubscribeEvent
    public void onEntityConstructing (EntityEvent.EntityConstructing event)
    {
        if (event.entity instanceof EntityPlayer && TPlayerStats.get((EntityPlayer) event.entity) == null)
        {
            TPlayerStats.register((EntityPlayer) event.entity);
        }
    }

    public void onPlayerLogin (EntityPlayer player)
    {
        // Lookup player
        TPlayerStats stats = TPlayerStats.get(player);

        stats.level = player.experienceLevel;
        stats.hunger = player.getFoodStats().getFoodLevel();

        //stats.battlesignBonus = tags.getCompoundTag("TConstruct").getBoolean("battlesignBonus");

        // gamerule naturalRegeneration false
        if (!PHConstruct.enableHealthRegen)
            player.worldObj.getGameRules().setOrCreateGameRule("naturalRegeneration", "false");
        if (!stats.beginnerManual)
        {
            stats.beginnerManual = true;
            stats.battlesignBonus = true;
            if (PHConstruct.beginnerBook)
            {
                ItemStack diary = new ItemStack(TinkerTools.manualBook);
                if (!player.inventory.addItemStackToInventory(diary))
                {
                    AbilityHelper.spawnItemAtPlayer(player, diary);
                }
            }

            if (player.getDisplayName().toLowerCase().equals("fudgy_fetus"))
            {
                ItemStack pattern = new ItemStack(TinkerTools.woodPattern, 1, 22);

                NBTTagCompound compound = new NBTTagCompound();
                compound.setTag("display", new NBTTagCompound());
                compound.getCompoundTag("display").setString("Name", "\u00A7f" + "Fudgy_Fetus' Full Guard Pattern");
                NBTTagList list = new NBTTagList();
                list.appendTag(new NBTTagString("\u00A72\u00A7o" + "The creator and the creation"));
                list.appendTag(new NBTTagString("\u00A72\u00A7o" + "are united at last!"));
                compound.getCompoundTag("display").setTag("Lore", list);
                pattern.setTagCompound(compound);

                AbilityHelper.spawnItemAtPlayer(player, pattern);
            }

            if (player.getDisplayName().toLowerCase().equals("zerokyuuni"))
            {
                ItemStack pattern = new ItemStack(Items.stick);

                NBTTagCompound compound = new NBTTagCompound();
                compound.setTag("display", new NBTTagCompound());
                compound.getCompoundTag("display").setString("Name", "\u00A78" + "Cheaty Inventory");
                NBTTagList list = new NBTTagList();
                list.appendTag(new NBTTagString("\u00A72\u00A7o" + "Nyaa~"));
                compound.getCompoundTag("display").setTag("Lore", list);
                pattern.setTagCompound(compound);

                AbilityHelper.spawnItemAtPlayer(player, pattern);
            }
            if (player.getDisplayName().toLowerCase().equals("zisteau"))
            {
                spawnPigmanModifier(player);
            }

            NBTTagCompound tags = player.getEntityData();
            NBTTagCompound persistTag = tags.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
            if (stickUsers.contains(player.getDisplayName()) && !persistTag.hasKey("TCon-Stick"))
            {
                ItemStack stick = new ItemStack(Items.stick);
                persistTag.setBoolean("TCon-Stick", true);

                NBTTagCompound compound = new NBTTagCompound();
                compound.setTag("display", new NBTTagCompound());
                compound.getCompoundTag("display").setString("Name", "\u00A7f" + "Stick of Patronage");
                NBTTagList list = new NBTTagList();
                list.appendTag(new NBTTagString("Thank you for supporting"));
                list.appendTag(new NBTTagString("Tinkers' Construct!"));
                compound.getCompoundTag("display").setTag("Lore", list);
                stick.setTagCompound(compound);

                stick.addEnchantment(Enchantment.knockback, 2);
                stick.addEnchantment(Enchantment.sharpness, 3);

                AbilityHelper.spawnItemAtPlayer(player, stick);
                tags.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistTag);
            }
        }
        else
        {
            if (!stats.battlesignBonus)
            {
                stats.battlesignBonus = true;
                ItemStack modifier = new ItemStack(TinkerTools.creativeModifier);

                NBTTagCompound compound = new NBTTagCompound();
                compound.setTag("display", new NBTTagCompound());
                NBTTagList list = new NBTTagList();
                list.appendTag(new NBTTagString("Battlesigns were buffed recently."));
                list.appendTag(new NBTTagString("This might make up for it."));
                compound.getCompoundTag("display").setTag("Lore", list);
                compound.setString("TargetLock", TinkerTools.battlesign.getToolName());
                modifier.setTagCompound(compound);

                AbilityHelper.spawnItemAtPlayer(player, modifier);

                if (player.getDisplayName().toLowerCase().equals("zisteau"))
                {
                    spawnPigmanModifier(player);
                }
            }
        }

        if (PHConstruct.gregtech && Loader.isModLoaded("GregTech-Addon"))
        {
            PHConstruct.gregtech = false;
            if (PHConstruct.lavaFortuneInteraction)
            {
                PlayerUtils.sendChatMessage(player, "Warning: Cross-mod Exploit Present!");
                PlayerUtils.sendChatMessage(player, "Solution 1: Disable Reverse Smelting recipes from GregTech.");
                PlayerUtils.sendChatMessage(player, "Solution 2: Disable Auto-Smelt/Fortune interaction from TConstruct.");
            }
        }
    }

    void spawnPigmanModifier (EntityPlayer entityplayer)
    {
        ItemStack modifier = new ItemStack(TinkerTools.creativeModifier);

        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("display", new NBTTagCompound());
        compound.getCompoundTag("display").setString("Name", "Zistonian Bonus Modifier");
        NBTTagList list = new NBTTagList();
        list.appendTag(new NBTTagString("Zombie Pigmen seem to have a natural affinty"));
        list.appendTag(new NBTTagString("for these types of weapons."));
        compound.getCompoundTag("display").setTag("Lore", list);
        compound.setString("TargetLock", TinkerTools.battlesign.getToolName());
        modifier.setTagCompound(compound);

        AbilityHelper.spawnItemAtPlayer(entityplayer, modifier);
    }

    public void onPlayerRespawn (EntityPlayer entityplayer)
    {
        // Boom!
        TPlayerStats playerData = playerStats.remove(entityplayer.getPersistentID());
        TPlayerStats stats = TPlayerStats.get(entityplayer);
        if (playerData != null)
        {
            stats.copyFrom(playerData, false);
            stats.level = playerData.level;
            stats.hunger = playerData.hunger;
        }

        stats.player = new WeakReference<EntityPlayer>(entityplayer);
        stats.armor.recalculateHealth(entityplayer, stats);

        /*
         * TFoodStats food = new TFoodStats(); entityplayer.foodStats = food;
         */

        if (PHConstruct.keepLevels)
            entityplayer.experienceLevel = stats.level;
        if (PHConstruct.keepHunger)
            entityplayer.getFoodStats().addStats(-1 * (20 - stats.hunger), 0);

        Side side = FMLCommonHandler.instance().getEffectiveSide();
        if (side == Side.CLIENT)
        {
            // TProxyClient.controlInstance.resetControls();
            if (PHConstruct.keepHunger)
                entityplayer.getFoodStats().setFoodLevel(stats.hunger);
        }
    }

    @SubscribeEvent
    public void livingFall (LivingFallEvent evt) // Only for negating fall damage
    {
        if (evt.entityLiving instanceof EntityPlayer)
        {
            evt.distance -= 1;
        }
    }

    @SubscribeEvent
    public void playerDeath (LivingDeathEvent event)
    {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
        {
            TPlayerStats properties = (TPlayerStats) event.entity.getExtendedProperties(TPlayerStats.PROP_NAME);
            properties.hunger = ((EntityPlayer) event.entity).getFoodStats().getFoodLevel();
            playerStats.put(((EntityPlayer) event.entity).getPersistentID(), properties);
        }

    }

    @SubscribeEvent
    public void playerDrops (PlayerDropsEvent evt)
    {
        // After playerDeath event. Modifying saved data.
        TPlayerStats stats = playerStats.get(evt.entityPlayer.getPersistentID());

        stats.level = evt.entityPlayer.experienceLevel / 2;
        // stats.health = 20;
        int hunger = evt.entityPlayer.getFoodStats().getFoodLevel();
        if (hunger < 6)
            stats.hunger = 6;
        else
            stats.hunger = evt.entityPlayer.getFoodStats().getFoodLevel();

        stats.armor.dropItems(evt.drops);
        stats.knapsack.dropItems(evt.drops);

        playerStats.put(evt.entityPlayer.getPersistentID(), stats);
    }

    /* Modify Player */
    public void updateSize (String user, float offset)
    {
        /*
         * EntityPlayer player = getEntityPlayer(user); setEntitySize(0.6F,
         * offset, player); player.yOffset = offset - 0.18f;
         */
    }

    public static void setEntitySize (float width, float height, Entity entity)
    {
        // TConstruct.logger.info("Size: " + height);
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
        // entity.yOffset = height;
    }

    private final String serverLocation = "https://dl.dropboxusercontent.com/u/42769935/sticks.txt";
    private final int timeout = 1000;
    private HashSet<String> stickUsers = new HashSet<String>();

    public void buildStickURLDatabase (String location)
    {
        URL url;
        try
        {
            url = new URL(location);
            URLConnection con = url.openConnection();
            con.setConnectTimeout(timeout);
            con.setReadTimeout(timeout);
            InputStream io = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(io));

            String nick;
            int linetracker = 1;
            while ((nick = br.readLine()) != null)
            {
                if (!nick.startsWith("--"))
                {
                    stickUsers.add(nick);
                }
                linetracker++;
            }

            br.close();
        }
        catch (Exception e)
        {
            TConstruct.logger.error(e.getMessage() != null ? e.getMessage() : "UNKOWN DL ERROR", e);
        }
    }

    Random rand = new Random();

}
