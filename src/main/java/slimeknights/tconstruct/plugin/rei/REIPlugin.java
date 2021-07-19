package slimeknights.tconstruct.plugin.rei;

import me.shedaniel.rei.api.plugins.REIPluginV0;
import slimeknights.tconstruct.TConstruct;

import net.minecraft.util.Identifier;

public class REIPlugin implements REIPluginV0 {

  @Override
  public Identifier getPluginIdentifier() {
    return new Identifier(TConstruct.modID, "rei");
  }
}
