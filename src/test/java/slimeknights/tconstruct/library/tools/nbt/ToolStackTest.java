package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableMap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.fixture.ModifierFixture;
import slimeknights.tconstruct.fixture.ToolDefinitionFixture;
import slimeknights.tconstruct.library.tools.ToolCoreTest;
import slimeknights.tconstruct.library.tools.ToolDefinition;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ToolStackTest extends ToolCoreTest {
  @BeforeAll
  static void before() {
    ModifierFixture.init();
  }

  /* From */

  @Test
  void from_preservesItem() {
    ToolStack stack = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundNBT());
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
    assertThat(testItemStack.getDamage()).overridingErrorMessage("ToolStack damage was not transferred to the original stack").isEqualTo(10);
  }

  @Test
  void copyFrom_notSharedNBT() {
    ToolStack tool = ToolStack.copyFrom(testItemStack);
    tool.setDamage(10);
    assertThat(testItemStack.getDamage()).overridingErrorMessage("Copied ToolStack damage was transferred to the original stack").isEqualTo(0);
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
    ToolStack tool = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundNBT());
    assertThat(tool.getItem()).isNotNull();
    assertThat(tool.getDefinition()).isNotNull();
    assertThat(tool.getDamage()).isEqualTo(0);
    assertThat(tool.isBroken()).isFalse();
    assertThat(tool.getMaterials()).isEqualTo(MaterialNBT.EMPTY);
    assertThat(tool.getModifiers()).isEqualTo(ModifierNBT.EMPTY);
    assertThat(tool.getPersistentData()).isEqualTo(new ModDataNBT());
    assertThat(tool.getAllMods()).isEqualTo(ModifierNBT.EMPTY);
    assertThat(tool.getStats()).isEqualTo(StatsNBT.EMPTY);
    assertThat(tool.getVolatileModData()).isEqualTo(IModDataReadOnly.EMPTY);
  }


  /* Creating and update stacks */

  @Test
  void createStack_setsNBT() {
    ToolStack tool = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundNBT());
    tool.setBroken(true);
    ItemStack stack = tool.createStack();
    assertThat(stack.getTag()).isEqualTo(tool.getNbt());
  }

  @Test
  void updateStack_copiesNBT() {
    ToolStack tool = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundNBT());
    // vanilla's setTag function will set the damage to 0 if applicable, so just ensuring its set
    tool.setDamage(0);
    tool.setBroken(true);

    ItemStack stack = tool.updateStack(new ItemStack(Items.DIAMOND_PICKAXE));
    assertThat(stack.getTag()).isEqualTo(tool.getNbt());
    assertThat(stack.getTag()).isNotSameAs(tool.getNbt());
  }

  @Test
  void updateStack_validatesItem() {
    ToolStack tool = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundNBT());
    assertThatThrownBy(() -> tool.updateStack(new ItemStack(Items.DIAMOND_AXE))).isInstanceOf(IllegalArgumentException.class);
  }


  /* Damage and broken*/

  @Test
  void serialize_damageBroken() {
    ToolStack stack = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinitionFixture.getStandardToolDefinition(), new CompoundNBT());
    stack.setStats(new StatsNBT(100, 0, 0, 0, 0));
    stack.setDamage(1);
    stack.setBroken(true);

    CompoundNBT nbt = stack.getNbt();
    assertThat(nbt.contains(ToolStack.TAG_BROKEN)).isTrue();
    assertThat(nbt.getTagId(ToolStack.TAG_BROKEN)).isEqualTo((byte) Constants.NBT.TAG_BYTE);
    assertThat(nbt.getBoolean(ToolStack.TAG_BROKEN)).isTrue();
    assertThat(nbt.contains(ToolStack.TAG_DAMAGE)).isTrue();
    assertThat(nbt.getTagId(ToolStack.TAG_DAMAGE)).isEqualTo((byte) Constants.NBT.TAG_INT);
    assertThat(nbt.getInt(ToolStack.TAG_DAMAGE)).isEqualTo(1);
  }

  @Test
  void deserialize_damageBroken() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.putInt(ToolStack.TAG_DAMAGE, 4);
    nbt.putBoolean(ToolStack.TAG_BROKEN, true);
    ToolStack stack = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, nbt);

    assertThat(stack.getDamageRaw()).isEqualTo(4);
    assertThat(stack.isBroken()).isTrue();
  }

  @Test
  void damage_getDamageValidates() {
    CompoundNBT nbt = testItemStack.getTag();
    assertThat(nbt).isNotNull();
    nbt.putInt(ToolStack.TAG_DAMAGE, 9999);

    ToolStack tool = ToolStack.from(testItemStack);
    assertThat(tool.getDamage()).isLessThanOrEqualTo(tool.getStats().getDurability());
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
    CompoundNBT nbt = testItemStack.getTag();
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
    tool.damage(100, null, null);
    assertThat(tool.getDamage()).isEqualTo(oldDamage + 100);
  }

  @Test
  void damage_repairTool() {
    ToolStack tool = ToolStack.from(testItemStack);
    tool.setDamage(50);
    int oldDamage = tool.getDamage();
    tool.repair(25);
    assertThat(tool.getDamage()).isEqualTo(oldDamage - 25);
  }


  @Test
  void deserializeNBT_materials() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.put(ToolStack.TAG_MATERIALS, new ListNBT());
    ToolStack tool = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, nbt);
    assertThat(tool.getMaterials()).isNotNull();
  }

  @Test
  void deserializeNBT_stats() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.put(ToolStack.TAG_STATS, new CompoundNBT());
    ToolStack tool = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, nbt);
    assertThat(tool.getMaterials()).isNotNull();
  }


  /* Stats */

  @Test
  void stats_serialize() {
    ToolStack tool = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundNBT());
    StatsNBT setStats = new StatsNBT(100, 2, 3f, 4f, 5f);
    tool.setStats(setStats);
    CompoundNBT nbt = tool.createStack().getTag();

    assertThat(nbt).isNotNull();
    assertThat(nbt.contains(ToolStack.TAG_STATS));
    // assumes stats NBT properly deserializes
    StatsNBT readStats = StatsNBT.readFromNBT(nbt.get(ToolStack.TAG_STATS));
    assertThat(readStats).isEqualTo(setStats);
  }

  @Test
  void stats_deserialize() {
    StatsNBT setStats = new StatsNBT(100, 2, 3f, 4f, 5f);
    ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
    stack.getOrCreateTag().put(ToolStack.TAG_STATS, setStats.serializeToNBT());

    ToolStack tool = ToolStack.from(stack);
    StatsNBT readStats = tool.getStats();
    assertThat(readStats).isNotEqualTo(StatsNBT.EMPTY);
    assertThat(readStats).isEqualTo(setStats);
  }

  @Test
  void stats_lowDurabilityUpdatesDurability() {
    ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
    stack.setDamage(100);

    ToolStack tool = ToolStack.from(stack);
    tool.setStats(new StatsNBT(50, 0, 0, 0, 0));
    assertThat(tool.getDamageRaw()).isEqualTo(49);
    assertThat(tool.isBroken()).isTrue();
  }


  /* Materials */

  @Test
  void materials_serialize() {
    ToolStack toolStack = ToolStack.from(tool, tool.getToolDefinition(), new CompoundNBT());
    MaterialNBT setMaterials = new MaterialNBT(Arrays.asList(MaterialFixture.MATERIAL_WITH_HEAD, MaterialFixture.MATERIAL_WITH_HANDLE, MaterialFixture.MATERIAL_WITH_EXTRA));
    toolStack.setMaterialsRaw(setMaterials);

    CompoundNBT nbt = toolStack.getNbt();
    assertThat(nbt.contains(ToolStack.TAG_MATERIALS));
    MaterialNBT readMaterials = MaterialNBT.readFromNBT(nbt.get(ToolStack.TAG_MATERIALS));
    assertThat(readMaterials).isNotEqualTo(MaterialNBT.EMPTY);
    assertThat(readMaterials).isEqualTo(setMaterials);
  }

  @Test
  void materials_deserialize() {
    ItemStack stack = new ItemStack(tool);
    MaterialNBT setMaterials = new MaterialNBT(Arrays.asList(MaterialFixture.MATERIAL_WITH_HEAD, MaterialFixture.MATERIAL_WITH_HANDLE, MaterialFixture.MATERIAL_WITH_EXTRA));
    stack.getOrCreateTag().put(ToolStack.TAG_MATERIALS, setMaterials.serializeToNBT());

    ToolStack tool = ToolStack.from(stack);
    MaterialNBT readMaterials = tool.getMaterials();
    assertThat(readMaterials).isNotEqualTo(MaterialNBT.EMPTY);
    assertThat(readMaterials).isEqualTo(setMaterials);
  }

  @Test
  void materials_replaceMaterial() {
    ToolStack toolStack = ToolStack.from(testItemStack);
    assertThat(toolStack.getMaterialsList().size()).isEqualTo(3);
    assertThat(toolStack.getMaterial(0)).isEqualTo(MaterialFixture.MATERIAL_WITH_HEAD);
    assertThat(toolStack.getMaterial(1)).isEqualTo(MaterialFixture.MATERIAL_WITH_HANDLE);
    assertThat(toolStack.getMaterial(2)).isEqualTo(MaterialFixture.MATERIAL_WITH_EXTRA);

    // ensure it updated and no side-effects
    toolStack.replaceMaterial(0, MaterialFixture.MATERIAL_WITH_ALL_STATS);
    assertThat(toolStack.getMaterialsList().size()).isEqualTo(3);
    assertThat(toolStack.getMaterial(0)).isEqualTo(MaterialFixture.MATERIAL_WITH_ALL_STATS);
    assertThat(toolStack.getMaterial(1)).isEqualTo(MaterialFixture.MATERIAL_WITH_HANDLE);
    assertThat(toolStack.getMaterial(2)).isEqualTo(MaterialFixture.MATERIAL_WITH_EXTRA);
  }


  /* Modifiers */

  @Test
  void modifiers_addModifier() {
    ToolStack toolStack = ToolStack.from(testItemStack);
    assertThat(toolStack.getModifiers().getLevel(ModifierFixture.TEST_MODIFIER_1)).isEqualTo(0);
    toolStack.addModifier(ModifierFixture.TEST_MODIFIER_1, 1);
    assertThat(toolStack.getModifiers().getLevel(ModifierFixture.TEST_MODIFIER_1)).isEqualTo(1);
  }

  @Test
  void modifiers_serialize() {
    ToolStack toolStack = ToolStack.from(testItemStack);
    toolStack.addModifier(ModifierFixture.TEST_MODIFIER_1, 1);

    CompoundNBT nbt = toolStack.getNbt();
    assertThat(nbt.contains(ToolStack.TAG_MODIFIERS));
    ModifierNBT readModifiers = ModifierNBT.readFromNBT(nbt.get(ToolStack.TAG_MODIFIERS));
    assertThat(readModifiers).isNotEqualTo(ModifierNBT.EMPTY);
    assertThat(readModifiers).isEqualTo(ModifierNBT.EMPTY.withModifier(ModifierFixture.TEST_MODIFIER_1, 1));
  }

  @Test
  void modifiers_deserialize() {
    ModifierNBT setModifiers = ModifierNBT.EMPTY.withModifier(ModifierFixture.TEST_MODIFIER_1, 1);
    testItemStack.getOrCreateTag().put(ToolStack.TAG_MODIFIERS, setModifiers.serializeToNBT());

    ToolStack tool = ToolStack.from(testItemStack);
    ModifierNBT readModifiers = tool.getModifiers();
    assertThat(readModifiers).isNotEqualTo(ModifierNBT.EMPTY);
    assertThat(readModifiers).isEqualTo(setModifiers);
  }

  @Test
  void allMods_serialize() {
    ToolStack toolStack = ToolStack.from(testItemStack);
    ModifierNBT setModifiers = ModifierNBT.EMPTY.withModifier(ModifierFixture.TEST_MODIFIER_1, 1);
    toolStack.setAllMods(setModifiers);

    CompoundNBT nbt = toolStack.getNbt();
    assertThat(nbt.contains(ToolStack.TAG_ALL_MODS));
    ModifierNBT readModifiers = ModifierNBT.readFromNBT(nbt.get(ToolStack.TAG_ALL_MODS));
    assertThat(readModifiers).isNotEqualTo(ModifierNBT.EMPTY);
    assertThat(readModifiers).isEqualTo(setModifiers);
  }

  @Test
  void allMods_deserialize() {
    ModifierNBT setModifiers = ModifierNBT.EMPTY.withModifier(ModifierFixture.TEST_MODIFIER_1, 1);
    testItemStack.getOrCreateTag().put(ToolStack.TAG_ALL_MODS, setModifiers.serializeToNBT());

    ToolStack tool = ToolStack.from(testItemStack);
    ModifierNBT readModifiers = tool.getAllMods();
    assertThat(readModifiers).isNotEqualTo(ModifierNBT.EMPTY);
    assertThat(readModifiers).isEqualTo(setModifiers);
  }


  /* Mod data */

  @Test
  void persistentModData_serialize() {
    ToolStack toolStack = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundNBT());
    assertThat(toolStack.getNbt().contains(ToolStack.TAG_PERSISTENT_MOD_DATA)).isFalse();

    ModDataNBT modData = toolStack.getPersistentData();
    modData.setModifiers(1);

    assertThat(toolStack.getNbt().contains(ToolStack.TAG_PERSISTENT_MOD_DATA)).isTrue();
    assertThat(toolStack.getNbt().getCompound(ToolStack.TAG_PERSISTENT_MOD_DATA)).isEqualTo(modData.getData());
  }

  @Test
  void persistentModData_deserialize() {
    ModDataNBT modData = new ModDataNBT();
    modData.setModifiers(1);
    testItemStack.getOrCreateTag().put(ToolStack.TAG_PERSISTENT_MOD_DATA, modData.getData());

    ToolStack toolStack = ToolStack.from(testItemStack);
    assertThat(toolStack.getPersistentData().getData()).isEqualTo(modData.getData());
  }

  @Test
  void volatileModData_serialize() {
    ToolStack toolStack = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundNBT());
    ModDataNBT modData = new ModDataNBT();
    modData.setModifiers(1);
    toolStack.setVolatileModData(modData);

    assertThat(toolStack.getNbt().contains(ToolStack.TAG_VOLATILE_MOD_DATA)).isTrue();
    assertThat(toolStack.getNbt().getCompound(ToolStack.TAG_VOLATILE_MOD_DATA)).isEqualTo(modData.getData());
  }

  @Test
  void volatileModData_deserialize() {
    ModDataNBT modData = new ModDataNBT();
    modData.setModifiers(1);
    testItemStack.getOrCreateTag().put(ToolStack.TAG_VOLATILE_MOD_DATA, modData.getData());

    ToolStack toolStack = ToolStack.from(testItemStack);
    assertThat(toolStack.getVolatileModData()).isEqualTo(modData);
  }


  /* Rebuild */

  @Test
  void setEnchantments() {
    ToolStack toolStack = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundNBT());
    toolStack.setEnchantments(ImmutableMap.of(Enchantments.KNOCKBACK, 2));

    ItemStack stack = toolStack.createStack();
    assertThat(EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, stack)).isEqualTo(2);
  }

  @Test
  void setMaterials_refreshesData() {
    ToolStack toolStack = ToolStack.from(tool, tool.getToolDefinition(), new CompoundNBT());
    assertThat(toolStack.getStats()).isEqualTo(StatsNBT.EMPTY);

    MaterialNBT materials = new MaterialNBT(Arrays.asList(MaterialFixture.MATERIAL_WITH_HEAD, MaterialFixture.MATERIAL_WITH_HANDLE, MaterialFixture.MATERIAL_WITH_EXTRA));
    toolStack.setMaterials(materials);
    assertThat(toolStack.getStats()).isNotEqualTo(StatsNBT.EMPTY);
  }

  @Test
  void addModifier_refreshesData() {
    ToolStack toolStack = ToolStack.from(tool, tool.getToolDefinition(), new CompoundNBT());
    // need materials for rebuild
    toolStack.setMaterialsRaw(new MaterialNBT(Arrays.asList(MaterialFixture.MATERIAL_WITH_HEAD, MaterialFixture.MATERIAL_WITH_HANDLE, MaterialFixture.MATERIAL_WITH_EXTRA)));
    // set some data that will get cleared out
    toolStack.setEnchantments(ImmutableMap.of(Enchantments.KNOCKBACK, 2));
    ModDataNBT volatileData = new ModDataNBT();
    volatileData.setModifiers(4);
    toolStack.setVolatileModData(volatileData);
    assertThat(toolStack.getAllMods()).isEqualTo(ModifierNBT.EMPTY);

    toolStack.addModifier(ModifierFixture.TEST_MODIFIER_1, 2);
    assertThat(toolStack.getVolatileModData()).isEqualTo(IModDataReadOnly.EMPTY);
    assertThat(toolStack.getNbt().contains(ToolStack.TAG_ENCHANTMENTS)).isFalse();
    assertThat(toolStack.getAllMods().getLevel(ModifierFixture.TEST_MODIFIER_1)).isEqualTo(2);
  }
}
