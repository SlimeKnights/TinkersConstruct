package slimeknights.tconstruct.library.modifiers.nbt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.nbt.CompoundNBT;

@AllArgsConstructor
@Getter
public class ModifierAndExtraPair {
  private final ModifierNBT modifierNBT;
  private final CompoundNBT compoundNBT;
}
