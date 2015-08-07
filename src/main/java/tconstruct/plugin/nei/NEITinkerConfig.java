package tconstruct.plugin.nei;


import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import tconstruct.TConstruct;
import tconstruct.tools.client.GuiCraftingStation;

public class NEITinkerConfig implements IConfigureNEI {

  @Override
  public void loadConfig() {
    API.registerGuiOverlayHandler(GuiCraftingStation.class, new CraftingStationOverlayHandler(), "crafting");

  }

  @Override
  public String getName() {
    return TConstruct.modID;
  }

  @Override
  public String getVersion() {
    return TConstruct.modVersion;
  }
}
