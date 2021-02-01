package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public class OverlordModifier extends Modifier {
  public static final ResourceLocation OVERSLIME_CAP = Util.getResource("overslime_cap");
  public OverlordModifier() {
    super(0x236c45);
  }

  @Override
  public void addVolatileData(int level, ModDataNBT data) {
    int current = 50;
    if (data.contains(OVERSLIME_CAP, NBT.TAG_ANY_NUMERIC)) {
      current = data.getInt(OVERSLIME_CAP);
    }
    // extra 50 per level
    data.putInt(OVERSLIME_CAP, current + level * 50);
  }
}
