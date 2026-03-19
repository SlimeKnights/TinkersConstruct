package slimeknights.tconstruct.library.tools.definition.module.material;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ArmorItem;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.ArmorModuleBuilder;

import java.util.List;

/** Module to repair a tool using materials which are not conventionally repair materials */
public record StatlessPartRepairModule(int partIndex, int repairAmount) implements MaterialRepairToolHook, ToolModule {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MaterialRepairModule>defaultHooks(ToolHooks.MATERIAL_REPAIR);
  public static final RecordLoadable<StatlessPartRepairModule> LOADER = RecordLoadable.create(
    IntLoadable.FROM_ZERO.requiredField("part_index", StatlessPartRepairModule::partIndex),
    IntLoadable.FROM_ONE.requiredField("repair_amount", StatlessPartRepairModule::repairAmount),
    StatlessPartRepairModule::new);

  /** Creates a builder for armor */
  public static ArmorBuilder armor(int partIndex) {
    return new ArmorBuilder(partIndex);
  }

  @Override
  public RecordLoadable<StatlessPartRepairModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean isRepairMaterial(IToolStackView tool, MaterialId material) {
    return material.equals(tool.getMaterial(partIndex).getId());
  }

  @Override
  public float getRepairAmount(IToolStackView tool, MaterialId material) {
    return isRepairMaterial(tool, material) ? repairAmount : 0;
  }

  /** Builder logic */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class ArmorBuilder implements ArmorModuleBuilder<StatlessPartRepairModule> {
    private final int partIndex;
    private final int[] durability = new int[4];

    /** Sets the durability for the piece based on the given factor */
    public ArmorBuilder durabilityFactor(float maxDamageFactor) {
      for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
        int index = slotType.ordinal();
        durability[index] = (int)(ArmorModuleBuilder.MAX_DAMAGE_ARRAY[index] * maxDamageFactor);
      }
      return this;
    }

    @Override
    public StatlessPartRepairModule build(ArmorItem.Type slot) {
      return new StatlessPartRepairModule(partIndex, durability[slot.ordinal()]);
    }
  }
}
