package slimeknights.tconstruct.common.config;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;
import java.util.Set;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;

public class ConfigGui extends GuiConfig {

  public ConfigGui(GuiScreen parentScreen) {
    super(parentScreen, getConfigElements(), TConstruct.modID, false, false, Util.prefix("configgui.title"));
  }

  private static List<IConfigElement> getConfigElements() {
    List<IConfigElement> list = Lists.newArrayList();

    list.add(new ConfigElement(Config.Modules));
    list.add(new ConfigElement(Config.Gameplay));
    list.add(new ConfigElement(Config.Worldgen));
    list.add(new ConfigElement(Config.ClientSide));

    return list;
  }


  public static class ConfigGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {

    }

    @Override
    public boolean hasConfigGui() {
      return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
      return new ConfigGui(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      // dead code
      return null;
    }

  }
}
