package slimeknights.tconstruct.common.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;

import slimeknights.tconstruct.common.TinkerNetwork;

public class ConfigSync {

  @SideOnly(Side.CLIENT)
  private static boolean needsRestart;

  @SubscribeEvent
  @SideOnly(Side.SERVER)
  public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if(event.player == null || !(event.player instanceof EntityPlayerMP) || FMLCommonHandler.instance().getSide()
                                                                                            .isClient()) {
      return;
    }

    ConfigSyncPacket packet = new ConfigSyncPacket();
    packet.categories.add(Config.Modules);
    packet.categories.add(Config.Gameplay);
    TinkerNetwork.sendTo(packet, (EntityPlayerMP) event.player);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public void playerJoinedWorld(TickEvent.ClientTickEvent event) {
    EntityPlayerSP player = Minecraft.getMinecraft().player;
    if(needsRestart) {
        player.sendMessage(new TextComponentString("[TConstruct] " + I18n.translateToLocal("config.synced.restart")));
      }
      else {
        player.sendMessage(new TextComponentString("[TConstruct] " + I18n.translateToLocal("config.synced.ok")));
      }
    MinecraftForge.EVENT_BUS.unregister(this);
  }

  // syncs the data to the current config
  public static void syncConfig(List<ConfigCategory> categories) {
    needsRestart = false;
    boolean changed = false;
    Config.log.info("Syncing Config with Server");

    for(ConfigCategory serverCategory : categories) {
      // get the local equivalent
      ConfigCategory category = Config.pulseConfig.getCategory();
      if(!serverCategory.getName().equals(category.getName())) {
        category = Config.configFile.getCategory(serverCategory.getName());
      }

      // sync all the properties
      for(Map.Entry<String, Property> entry : serverCategory.entrySet()) {
        String name = entry.getKey();
        Property serverProp = entry.getValue();

        // hopefully present locally?
        Property prop = category.get(name);
        if(prop == null) {
          // use the server one
          category.put(name, serverProp);
        }
        else {
          // we try to use the preset one because it contains comments n stuff
          if(!prop.getString().equals(serverProp.getString())) {
            // new value, update it
            prop.setValue(serverProp.getString());
            needsRestart |= prop.requiresMcRestart();
            changed = true;
            Config.log.debug("Syncing %s - %s: %s", category.getName(), prop.getName(), prop.getString());
          }
        }
      }
    }

    // if we changed something... disconnect and tell the player to restart?
    if(Config.configFile.hasChanged()) {
      Config.configFile.save();
    }
    Config.pulseConfig.flush();

    if(changed) {
      MinecraftForge.EVENT_BUS.register(new ConfigSync());
    }
  }
}
