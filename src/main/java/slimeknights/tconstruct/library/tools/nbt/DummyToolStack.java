package slimeknights.tconstruct.library.tools.nbt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.Item;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;

/** Tool stack instance in a context that does not have a proper tool, used for projectils */
@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor @Getter
public class DummyToolStack implements IToolStackView {
  private final Item item;
  private final ModifierNBT modifiers;
  private final ModDataNBT persistentData;

  @Override
  public ToolDefinition getDefinition() {
    return ToolDefinition.EMPTY;
  }

  @Override
  public MaterialNBT getMaterials() {
    return MaterialNBT.EMPTY;
  }

  @Override
  public ModifierNBT getUpgrades() {
    return ModifierNBT.EMPTY;
  }

  @Override
  public IModDataView getVolatileData() {
    return ModDataNBT.EMPTY;
  }

  @Override
  public int getDamage() {
    return 0;
  }

  @Override
  public int getCurrentDurability() {
    return 0;
  }

  @Override
  public boolean isBroken() {
    return false;
  }

  @Override
  public boolean isUnbreakable() {
    return false;
  }

  @Override
  public void setDamage(int damage) {}

  @Override
  public StatsNBT getStats() {
    return StatsNBT.EMPTY;
  }
}
