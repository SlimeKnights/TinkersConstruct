package slimeknights.tconstruct.plugin;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** Plugin to help us work with the test dummy mod */
public class DummmmmmyPlugin {
  @SubscribeEvent
  public void commonSetup(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      try {
        Method disableShield = TargetDummyEntity.class.getDeclaredMethod("disableShield");
        disableShield.setAccessible(true);
        ModifierUtil.registerShieldDisabler(entity -> {
          if (entity instanceof TargetDummyEntity target && target.isBlocking()) {
            try {
              disableShield.invoke(target);
            } catch (IllegalAccessException | InvocationTargetException e) {
              TConstruct.LOG.error("Failed to disable target dummy shield.", e);
            }
          }
        }, Dummmmmmy.TARGET_DUMMY.get());
      } catch (NoSuchMethodException e) {
        TConstruct.LOG.error("Failed to locate TargetDummyEntity::disableShield, unable to disable shields.", e);
      }
    });
  }
}
