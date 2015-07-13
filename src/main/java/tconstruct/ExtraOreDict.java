package tconstruct;

import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;

class ExtraOreDict {
  private ExtraOreDict() {}

  public static void oredictThings() {
    // crafting table
    OreDictionary.registerOre("workbench", Blocks.crafting_table);
  }
}
