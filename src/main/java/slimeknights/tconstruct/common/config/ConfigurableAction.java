package slimeknights.tconstruct.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;

/** Config prop that runs a runnable assuming its true */
public class ConfigurableAction implements Runnable {
  private final BooleanValue prop;
  private final Runnable action;

  public ConfigurableAction(ModConfigSpec.Builder builder, String name, boolean defaultValue, String comment, Runnable action) {
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
