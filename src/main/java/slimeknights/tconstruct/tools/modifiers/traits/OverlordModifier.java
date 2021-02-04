package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public class OverlordModifier extends Modifier {
  public static final ResourceLocation OVERSLIME_CAP = Util.getResource("overslime_cap");
  public OverlordModifier() {
    super(0x236c45);
  }

  @Override
  public void addVolatileData(IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    int current = 50;
    if (volatileData.contains(OVERSLIME_CAP, NBT.TAG_ANY_NUMERIC)) {
      current = volatileData.getInt(OVERSLIME_CAP);
    }
    // extra 50 per level
    volatileData.putInt(OVERSLIME_CAP, current + level * 50);
  }
}
