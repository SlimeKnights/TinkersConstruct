package slimeknights.tconstruct.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

/** Config prop that runs a runnable assuming its true */
public class ConfigurableAction implements Runnable {
  private final BooleanValue prop;
  private final Runnable action;

  public ConfigurableAction(ForgeConfigSpec.Builder builder, String name, boolean defaultValue, String comment, Runnable action) {
    prop = builder.comment(comment).worldRestart().define(name, defaultValue);
    this.action = action;
  }

  @Override
  public void run() {
    if (prop.get()) {
      action.run();
    }
  }
}
