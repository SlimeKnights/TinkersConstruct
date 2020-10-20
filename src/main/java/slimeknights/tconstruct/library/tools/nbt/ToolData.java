package slimeknights.tconstruct.library.tools.nbt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.With;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tools.ToolCore;

import java.util.List;

/**
 * The data of t he tool. Persisted in NBT.
 */
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ToolData {

  protected static final String TAG_ITEM = "tic_item";
  protected static final String TAG_MATERIALS = "tic_materials";
  protected static final String TAG_STATS = "tic_stats";
  protected static final String TAG_MODIFIERS = "tic_modifiers";

  // tool so we don't need categories and original stats?
  private final ToolItemNBT toolItem;
  //components=materials
  private final MaterialNBT materials;
  @With(AccessLevel.PRIVATE)
  private final StatsNBT stats;
  // modifiers
  @With(AccessLevel.PRIVATE)
  private final ModifierListNBT modifiers;

  public ToolCore getToolItem() {
    return toolItem.getToolItem();
  }

  public List<IMaterial> getMaterials() {
    return materials.getMaterials();
  }

  /**
   * Gets the material at the given index
   * @param index  Index
   * @return  Material, or unknown if index is invalid
   */
  public IMaterial getMaterial(int index) {
    return materials.getMaterial(index);
  }

  public StatsNBT getStats() {
    return stats;
  }

  public ModifierListNBT getModifiers() {
    return modifiers;
  }

  public ToolData createNewDataWithFreeModifiers(int freeModifiers) {
    StatsNBT newStats = getStats().withFreeModifiers(freeModifiers);
    return this.withStats(newStats);
  }

  public ToolData createNewDataWithBroken(boolean isBroken) {
    StatsNBT newStats = getStats().withBroken(isBroken);
    return this.withStats(newStats);
  }

  public ToolData createNewDataWithStats(StatsNBT newStats) {
    return this.withStats(newStats);
  }

  public ToolData createNewDataWithStatsAndModifiers(StatsNBT newStats, ModifierListNBT newModifiers) {
    return this.withStats(newStats).withModifiers(newModifiers);
  }

  public void updateStack(ItemStack stack) {
    stack.getOrCreateTag().merge(serializeToNBT());
  }

  // todo: keep backing NBT and lazily initialize all the values? Might be worth it.. or a severe case of optimizing too early

  public static boolean isBroken(ItemStack stack) {
    return from(stack).getStats().broken;
  }

  public static ToolData from(ItemStack stack) {
    return readFromNBT(stack.getOrCreateTag());
  }

  public static ToolData readFromNBT(CompoundNBT nbt) {
    ToolItemNBT toolCore = ToolItemNBT.readFromNBT(nbt.get(TAG_ITEM));
    MaterialNBT materialNBT = MaterialNBT.readFromNBT(nbt.get(TAG_MATERIALS));
    StatsNBT statsNBT = StatsNBT.readFromNBT(nbt.get(TAG_STATS));
    ModifierListNBT modifierListNBT = ModifierListNBT.readFromNBT(nbt.get(TAG_MODIFIERS));

    return new ToolData(toolCore, materialNBT, statsNBT, modifierListNBT);
  }

  public CompoundNBT serializeToNBT() {
    CompoundNBT nbt = new CompoundNBT();

    nbt.put(TAG_ITEM, toolItem.serializeToNBT());
    nbt.put(TAG_MATERIALS, materials.serializeToNBT());
    nbt.put(TAG_STATS, stats.serializeToNBT());
    nbt.put(TAG_MODIFIERS, modifiers.serializeToNBT());

    return nbt;
  }
}
