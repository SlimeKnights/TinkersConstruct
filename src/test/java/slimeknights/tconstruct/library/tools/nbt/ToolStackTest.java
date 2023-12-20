package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.fixture.ToolDefinitionFixture;
import slimeknights.tconstruct.library.modifiers.ModifierFixture;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ToolItemTest;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ToolStackTest extends ToolItemTest {
  private final StatsNBT testStatsNBT = StatsNBT.builder()
                                                .set(ToolStats.DURABILITY, 100f)
                                                .set(ToolStats.HARVEST_TIER, Tiers.NETHERITE)
                                                .set(ToolStats.ATTACK_DAMAGE, 2f)
                                                .set(ToolStats.MINING_SPEED, 3f)
                                                .set(ToolStats.ATTACK_SPEED, 5f)
                                                .build();

  @BeforeAll
  static void before() {
    ModifierFixture.init();
  }

  /* From */

  @Test
  void from_preservesItem() {
    ToolStack stack = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundTag());
    assertThat(stack.getItem()).isEqualTo(stack.getItem());
  }

  @Test
  void from_findToolCoreDefinition() {
    ItemStack stack = new ItemStack(tool);
    ToolStack tool = ToolStack.from(stack);
    assertThat(tool.getDefinition()).isEqualTo(tool.getDefinition());
  }

  @Test
  void from_shareNBT() {
    ToolStack tool = ToolStack.from(testItemStack);
    tool.setDamage(10);
    assertThat(testItemStack.getDamageValue()).overridingErrorMessage("ToolStack damage was not transferred to the original stack").isEqualTo(10);
  }

  @Test
  void copyFrom_notSharedNBT() {
    ToolStack tool = ToolStack.copyFrom(testItemStack);
    tool.setDamage(10);
    assertThat(testItemStack.getDamageValue()).overridingErrorMessage("Copied ToolStack damage was transferred to the original stack").isEqualTo(0);
  }

  @Test
  void copy_notSharedNBT() {
    ToolStack tool = ToolStack.from(testItemStack);
    ToolStack copy = tool.copy();
    tool.setDamage(10);
    assertThat(copy.getDamage()).overridingErrorMessage("Copied ToolStack damage was transferred to the original stack").isEqualTo(0);
  }

  @Test
  void deserialize_empty() {
    ToolStack tool = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundTag());
    assertThat(tool.getItem()).isNotNull();
    assertThat(tool.getDefinition()).isNotNull();
    assertThat(tool.getDamage()).isEqualTo(0);
    assertThat(tool.isBroken()).isFalse();
    assertThat(tool.getMaterials()).isEqualTo(MaterialNBT.EMPTY);
    assertThat(tool.getUpgrades()).isEqualTo(ModifierNBT.EMPTY);
    assertThat(tool.getPersistentData()).isEqualTo(new ModDataNBT());
    assertThat(tool.getModifiers()).isEqualTo(ModifierNBT.EMPTY);
    assertThat(tool.getStats()).isEqualTo(StatsNBT.EMPTY);
    assertThat(tool.getVolatileData()).isEqualTo(IModDataView.EMPTY);
  }


  /* Creating and update stacks */

  @Test
  void createStack_setsNBT() {
    ToolStack tool = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundTag());
    tool.setBrokenRaw(true);
    ItemStack stack = tool.createStack();
    assertThat(stack.getTag()).isEqualTo(tool.getNbt());
  }

  @Test
  void updateStack_copiesNBT() {
    ToolStack tool = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundTag());
    // vanilla's setTag function will set the damage to 0 if applicable, so just ensuring its set
    tool.setDamage(0);
    tool.setBrokenRaw(true);

    ItemStack stack = tool.updateStack(new ItemStack(Items.DIAMOND_PICKAXE));
    assertThat(stack.getTag()).isEqualTo(tool.getNbt());
    assertThat(stack.getTag()).isNotSameAs(tool.getNbt());
  }

  @Test
  void updateStack_validatesItem() {
    ToolStack tool = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundTag());
    assertThatThrownBy(() -> tool.updateStack(new ItemStack(Items.DIAMOND_AXE))).isInstanceOf(IllegalArgumentException.class);
  }


  /* Damage and broken */

  @Test
  void serialize_damageBroken() {
    ToolStack stack = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinitionFixture.getStandardToolDefinition(), new CompoundTag());
    stack.setStats(StatsNBT.builder().set(ToolStats.DURABILITY, 100f).build());
    stack.setDamage(1);
    stack.setBrokenRaw(true);

    CompoundTag nbt = stack.getNbt();
    assertThat(nbt.contains(ToolStack.TAG_BROKEN)).isTrue();
    assertThat(nbt.getTagType(ToolStack.TAG_BROKEN)).isEqualTo(Tag.TAG_BYTE);
    assertThat(nbt.getBoolean(ToolStack.TAG_BROKEN)).isTrue();
    assertThat(nbt.contains(ToolStack.TAG_DAMAGE)).isTrue();
    assertThat(nbt.getTagType(ToolStack.TAG_DAMAGE)).isEqualTo(Tag.TAG_INT);
    assertThat(nbt.getInt(ToolStack.TAG_DAMAGE)).isEqualTo(1);
  }

  @Test
  void deserialize_damageBroken() {
    CompoundTag nbt = new CompoundTag();
    nbt.putInt(ToolStack.TAG_DAMAGE, 4);
    nbt.putBoolean(ToolStack.TAG_BROKEN, true);
    ToolStack stack = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, nbt);

    assertThat(stack.getDamageRaw()).isEqualTo(4);
    assertThat(stack.isBroken()).isTrue();
  }

  @Test
  void damage_getDamageValidates() {
    CompoundTag nbt = testItemStack.getTag();
    assertThat(nbt).isNotNull();
    nbt.putInt(ToolStack.TAG_DAMAGE, 9999);

    ToolStack tool = ToolStack.from(testItemStack);
    assertThat(tool.getDamage()).isLessThanOrEqualTo(tool.getStats().getInt(ToolStats.DURABILITY));
  }

  @Test
  void damage_setDamageBreaksTool() {
    ToolStack tool = ToolStack.from(testItemStack);
    assertThat(tool.isBroken()).isFalse();
    tool.setDamage(99999);
    assertThat(tool.isBroken()).isTrue();
  }

  @Test
  void damage_setDamageUnbreaksTool() {
    CompoundTag nbt = testItemStack.getTag();
    assertThat(nbt).isNotNull();
    nbt.putBoolean(ToolStack.TAG_BROKEN, true);

    ToolStack tool = ToolStack.from(testItemStack);
    assertThat(tool.isBroken()).isTrue();
    tool.setDamage(10);
    assertThat(tool.isBroken()).isFalse();
  }

  @Test
  void damage_damageTool() {
    ToolStack tool = ToolStack.from(testItemStack);
    int oldDamage = tool.getDamage();
    ToolDamageUtil.damage(tool, 100, null, null);
    assertThat(tool.getDamage()).isEqualTo(oldDamage + 100);
  }

  @Test
  void damage_repairTool() {
    ToolStack tool = ToolStack.from(testItemStack);
    tool.setDamage(50);
    int oldDamage = tool.getDamage();
    ToolDamageUtil.repair(tool, 25);
    assertThat(tool.getDamage()).isEqualTo(oldDamage - 25);
  }

  @Test
  void broken_quickCheck() {
    ToolStack tool = ToolStack.from(testItemStack);
    tool.breakTool();
    ItemStack stack = tool.createStack();
    assertThat(ToolDamageUtil.isBroken(stack)).isTrue();
  }


  /* Materials */

  @Test
  void deserializeNBT_materials() {
    CompoundTag nbt = new CompoundTag();
    nbt.put(ToolStack.TAG_MATERIALS, new ListTag());
    ToolStack tool = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, nbt);
    assertThat(tool.getMaterials()).isNotNull();
  }

  @Test
  void deserializeNBT_stats() {
    CompoundTag nbt = new CompoundTag();
    nbt.put(ToolStack.TAG_STATS, new CompoundTag());
    ToolStack tool = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, nbt);
    assertThat(tool.getMaterials()).isNotNull();
  }


  /* Stats */

  @Test
  void stats_serialize() {
    ToolStack tool = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundTag());
    tool.setStats(testStatsNBT);
    CompoundTag nbt = tool.createStack().getTag();

    assertThat(nbt).isNotNull();
    assertThat(nbt.contains(ToolStack.TAG_STATS)).isTrue();
    // assumes stats NBT properly deserializes
    StatsNBT readStats = StatsNBT.readFromNBT(nbt.get(ToolStack.TAG_STATS));
    assertThat(readStats).isEqualTo(testStatsNBT);
  }

  @Test
  void stats_deserialize() {
    ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
    stack.getOrCreateTag().put(ToolStack.TAG_STATS, testStatsNBT.serializeToNBT());

    ToolStack tool = ToolStack.from(stack);
    StatsNBT readStats = tool.getStats();
    assertThat(readStats).isNotEqualTo(StatsNBT.EMPTY);
    assertThat(readStats).isEqualTo(testStatsNBT);
  }

  @Test
  void stats_lowDurabilityUpdatesDurability() {
    ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
    stack.setDamageValue(100);

    ToolStack tool = ToolStack.from(stack);
    tool.setStats(StatsNBT.builder().set(ToolStats.DURABILITY, 50f).build());
    assertThat(tool.getDamageRaw()).isEqualTo(50);
    assertThat(tool.isBroken()).isTrue();
  }


  /* Materials */

  @Test
  void materials_serialize() {
    ToolStack toolStack = ToolStack.from(tool, tool.getToolDefinition(), new CompoundTag());
    MaterialNBT setMaterials = MaterialNBT.of(MaterialFixture.MATERIAL_WITH_HEAD, MaterialFixture.MATERIAL_WITH_HANDLE, MaterialFixture.MATERIAL_WITH_EXTRA);
    toolStack.setMaterialsRaw(setMaterials);

    CompoundTag nbt = toolStack.getNbt();
    assertThat(nbt.contains(ToolStack.TAG_MATERIALS)).isTrue();
    MaterialNBT readMaterials = MaterialNBT.readFromNBT(nbt.get(ToolStack.TAG_MATERIALS));
    assertThat(readMaterials).isNotEqualTo(MaterialNBT.EMPTY);
    assertThat(readMaterials).isEqualTo(setMaterials);
  }

  @Test
  void materials_deserialize() {
    ItemStack stack = new ItemStack(tool);
    MaterialNBT setMaterials = MaterialNBT.of(MaterialFixture.MATERIAL_WITH_HEAD, MaterialFixture.MATERIAL_WITH_HANDLE, MaterialFixture.MATERIAL_WITH_EXTRA);
    stack.getOrCreateTag().put(ToolStack.TAG_MATERIALS, setMaterials.serializeToNBT());

    ToolStack tool = ToolStack.from(stack);
    MaterialNBT readMaterials = tool.getMaterials();
    assertThat(readMaterials).isNotEqualTo(MaterialNBT.EMPTY);
    assertThat(readMaterials).isEqualTo(setMaterials);
  }

  @Test
  void materials_replaceMaterial() {
    ToolStack toolStack = ToolStack.from(testItemStack);
    assertThat(toolStack.getMaterials().size()).isEqualTo(3);
    assertThat(toolStack.getMaterial(0).get()).isEqualTo(MaterialFixture.MATERIAL_WITH_HEAD);
    assertThat(toolStack.getMaterial(1).get()).isEqualTo(MaterialFixture.MATERIAL_WITH_HANDLE);
    assertThat(toolStack.getMaterial(2).get()).isEqualTo(MaterialFixture.MATERIAL_WITH_EXTRA);

    // ensure it updated and no side-effects
    toolStack.replaceMaterial(0, MaterialFixture.MATERIAL_WITH_ALL_STATS.getIdentifier());
    assertThat(toolStack.getMaterials().size()).isEqualTo(3);
    assertThat(toolStack.getMaterial(0).get()).isEqualTo(MaterialFixture.MATERIAL_WITH_ALL_STATS);
    assertThat(toolStack.getMaterial(1).get()).isEqualTo(MaterialFixture.MATERIAL_WITH_HANDLE);
    assertThat(toolStack.getMaterial(2).get()).isEqualTo(MaterialFixture.MATERIAL_WITH_EXTRA);
  }


  /* Modifiers */

  @Test
  void modifiers_addModifier() {
    ToolStack toolStack = ToolStack.from(testItemStack);
    assertThat(toolStack.getUpgrades().getLevel(ModifierFixture.TEST_1)).isEqualTo(0);
    toolStack.addModifier(ModifierFixture.TEST_1, 1);
    assertThat(toolStack.getUpgrades().getLevel(ModifierFixture.TEST_1)).isEqualTo(1);
  }

  @Test
  void modifiers_serialize() {
    ToolStack toolStack = ToolStack.from(testItemStack);
    toolStack.addModifier(ModifierFixture.TEST_1, 1);

    CompoundTag nbt = toolStack.getNbt();
    assertThat(nbt.contains(ToolStack.TAG_UPGRADES)).isTrue();
    ModifierNBT readModifiers = ModifierNBT.readFromNBT(nbt.get(ToolStack.TAG_UPGRADES));
    assertThat(readModifiers).isNotEqualTo(ModifierNBT.EMPTY);
    assertThat(readModifiers).isEqualTo(ModifierNBT.EMPTY.withModifier(ModifierFixture.TEST_1, 1));
  }

  @Test
  void modifiers_deserialize() {
    ModifierNBT setModifiers = ModifierNBT.EMPTY.withModifier(ModifierFixture.TEST_1, 1);
    testItemStack.getOrCreateTag().put(ToolStack.TAG_UPGRADES, setModifiers.serializeToNBT());

    ToolStack tool = ToolStack.from(testItemStack);
    ModifierNBT readModifiers = tool.getUpgrades();
    assertThat(readModifiers).isNotEqualTo(ModifierNBT.EMPTY);
    assertThat(readModifiers).isEqualTo(setModifiers);
  }

  @Test
  void allMods_serialize() {
    ToolStack toolStack = ToolStack.from(testItemStack);
    ModifierNBT setModifiers = ModifierNBT.EMPTY.withModifier(ModifierFixture.TEST_1, 1);
    toolStack.setModifiers(setModifiers);

    CompoundTag nbt = toolStack.getNbt();
    assertThat(nbt.contains(ToolStack.TAG_MODIFIERS)).isTrue();
    ModifierNBT readModifiers = ModifierNBT.readFromNBT(nbt.get(ToolStack.TAG_MODIFIERS));
    assertThat(readModifiers).isNotEqualTo(ModifierNBT.EMPTY);
    assertThat(readModifiers).isEqualTo(setModifiers);
  }

  @Test
  void allMods_deserialize() {
    ModifierNBT setModifiers = ModifierNBT.EMPTY.withModifier(ModifierFixture.TEST_1, 1);
    testItemStack.getOrCreateTag().put(ToolStack.TAG_MODIFIERS, setModifiers.serializeToNBT());

    ToolStack tool = ToolStack.from(testItemStack);
    ModifierNBT readModifiers = tool.getModifiers();
    assertThat(readModifiers).isNotEqualTo(ModifierNBT.EMPTY);
    assertThat(readModifiers).isEqualTo(setModifiers);
  }


  /* Mod data */

  @Test
  void persistentModData_serialize() {
    ToolStack toolStack = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundTag());
    assertThat(toolStack.getNbt().contains(ToolStack.TAG_PERSISTENT_MOD_DATA)).isFalse();

    ModDataNBT modData = toolStack.getPersistentData();
    modData.setSlots(SlotType.UPGRADE, 1);

    assertThat(toolStack.getNbt().contains(ToolStack.TAG_PERSISTENT_MOD_DATA)).isTrue();
    assertThat(toolStack.getNbt().getCompound(ToolStack.TAG_PERSISTENT_MOD_DATA)).isEqualTo(modData.getData());
  }

  @Test
  void persistentModData_deserialize() {
    ModDataNBT modData = new ModDataNBT();
    modData.setSlots(SlotType.UPGRADE, 1);
    testItemStack.getOrCreateTag().put(ToolStack.TAG_PERSISTENT_MOD_DATA, modData.getData());

    ToolStack toolStack = ToolStack.from(testItemStack);
    assertThat(toolStack.getPersistentData().getData()).isEqualTo(modData.getData());
  }

  @Test
  void volatileModData_serialize() {
    ToolStack toolStack = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundTag());
    ModDataNBT modData = new ModDataNBT();
    modData.setSlots(SlotType.UPGRADE, 1);
    toolStack.setVolatileModData(modData);

    assertThat(toolStack.getNbt().contains(ToolStack.TAG_VOLATILE_MOD_DATA)).isTrue();
    assertThat(toolStack.getNbt().getCompound(ToolStack.TAG_VOLATILE_MOD_DATA)).isEqualTo(modData.getData());
  }

  @Test
  void volatileModData_deserialize() {
    ModDataNBT modData = new ModDataNBT();
    modData.setSlots(SlotType.UPGRADE, 1);
    testItemStack.getOrCreateTag().put(ToolStack.TAG_VOLATILE_MOD_DATA, modData.getData());

    ToolStack toolStack = ToolStack.from(testItemStack);
    assertThat(toolStack.getVolatileData()).isEqualTo(modData);
  }


  /* Rebuild */

  @Test
  void setMaterials_refreshesData() {
    ToolStack toolStack = ToolStack.from(tool, tool.getToolDefinition(), new CompoundTag());
    assertThat(toolStack.getStats()).isEqualTo(StatsNBT.EMPTY);

    MaterialNBT materials = MaterialNBT.of(MaterialFixture.MATERIAL_WITH_HEAD, MaterialFixture.MATERIAL_WITH_HANDLE, MaterialFixture.MATERIAL_WITH_EXTRA);
    toolStack.setMaterials(materials);
    assertThat(toolStack.getStats()).isNotEqualTo(StatsNBT.EMPTY);
  }

  @Test
  void addModifier_refreshesData() {
    ToolStack toolStack = ToolStack.from(tool, tool.getToolDefinition(), new CompoundTag());
    // need materials for rebuild
    toolStack.setMaterialsRaw(MaterialNBT.of(MaterialFixture.MATERIAL_WITH_HEAD, MaterialFixture.MATERIAL_WITH_HANDLE, MaterialFixture.MATERIAL_WITH_EXTRA));
    // set some data that will get cleared out
    ModDataNBT volatileData = new ModDataNBT();
    volatileData.setSlots(SlotType.UPGRADE, 4);
    toolStack.setVolatileModData(volatileData);
    assertThat(toolStack.getModifiers()).isEqualTo(ModifierNBT.EMPTY);

    toolStack.addModifier(ModifierFixture.TEST_1, 2);
    assertThat(toolStack.getVolatileData()).isNotEqualTo(volatileData);
    assertThat(toolStack.getModifiers().getLevel(ModifierFixture.TEST_1)).isEqualTo(2);
  }

  @Test
  void rebuildStats_migratePersistentDataSlots() {
    // sanity check: definition is what we expect
    ToolDefinition definition = ToolDefinitionFixture.getStandardToolDefinition();
    assertThat(definition.getData().getStartingSlots(SlotType.UPGRADE)).isEqualTo(3);
    assertThat(definition.getData().getStartingSlots(SlotType.DEFENSE)).isEqualTo(0);
    assertThat(definition.getData().getStartingSlots(SlotType.ABILITY)).isEqualTo(1);

    // control test: this definition should by default grant 3 upgrades and 1 ability as it started with no data
    ToolStack toolStack = ToolStack.from(tool, definition, new CompoundTag());
    toolStack.rebuildStats();
    assertThat(toolStack.getFreeSlots(SlotType.UPGRADE)).isEqualTo(3);
    assertThat(toolStack.getFreeSlots(SlotType.DEFENSE)).isEqualTo(0);
    assertThat(toolStack.getFreeSlots(SlotType.ABILITY)).isEqualTo(1);

    // create legacy NBT with 5 upgrades and 2 defense, then check if that properly migrates
    // however many slots we had before via persistent data, we should still have on the final tool moved to volatile data
    ModDataNBT persistentData = new ModDataNBT();
    persistentData.addSlots(SlotType.UPGRADE, 5);
    persistentData.addSlots(SlotType.DEFENSE, 2);
    CompoundTag stackTag = new CompoundTag();
    stackTag.put(ToolStack.TAG_PERSISTENT_LEGACY_DATA, persistentData.getData());
    toolStack = ToolStack.from(tool, definition, stackTag);
    toolStack.rebuildStats();
    // expect the legacy data to be removed
    assertThat(stackTag.getAllKeys()).doesNotContain(ToolStack.TAG_PERSISTENT_LEGACY_DATA);
    // expect the new key to be added
    assertThat(stackTag.getAllKeys()).contains(ToolStack.TAG_PERSISTENT_MOD_DATA);
    // expect the same number of slots to still exist on the tool
    assertThat(toolStack.getFreeSlots(SlotType.UPGRADE)).isEqualTo(5);
    assertThat(toolStack.getFreeSlots(SlotType.DEFENSE)).isEqualTo(2);
    assertThat(toolStack.getFreeSlots(SlotType.ABILITY)).isEqualTo(0);

    // ensure that if slots need to be built, then migration is skipped
    persistentData = new ModDataNBT();
    persistentData.putBoolean(ToolStack.NEEDS_SLOTS_BUILT, true);
    stackTag = new CompoundTag();
    stackTag.put(ToolStack.TAG_PERSISTENT_LEGACY_DATA, persistentData.getData());
    toolStack = ToolStack.from(tool, definition, stackTag);
    toolStack.rebuildStats();
    assertThat(toolStack.getFreeSlots(SlotType.UPGRADE)).isEqualTo(3);
    assertThat(toolStack.getFreeSlots(SlotType.DEFENSE)).isEqualTo(0);
    assertThat(toolStack.getFreeSlots(SlotType.ABILITY)).isEqualTo(1);
  }
}
