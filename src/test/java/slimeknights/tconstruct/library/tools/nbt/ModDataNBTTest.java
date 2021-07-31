package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

class ModDataNBTTest extends BaseMcTest {
  private static final ResourceLocation testKey = new ResourceLocation("test");
  private static final ResourceLocation testKey2 = new ResourceLocation("test2");

  @Test
  void empty() {
    for (SlotType type : SlotType.getAllSlotTypes()) {
      assertThat(IModDataReadOnly.EMPTY.getSlots(type)).isEqualTo(0);
    }

    CompoundNBT nbt = IModDataReadOnly.EMPTY.getCompound(testKey);
    nbt.putInt("test", 1);
    nbt = IModDataReadOnly.EMPTY.getCompound(testKey);
    assertThat(nbt.contains("test")).overridingErrorMessage("NBT not saved in empty").isFalse();
  }

  @Test
  void defaults() {
    ModDataNBT nbt = new ModDataNBT();

    for (SlotType type : SlotType.getAllSlotTypes()) {
      assertThat(IModDataReadOnly.EMPTY.getSlots(type)).isEqualTo(0);
    }
    assertThat(nbt.getData().isEmpty()).isTrue();
  }

  @Test
  void serialize() {
    ModDataNBT modData = new ModDataNBT();
    modData.setSlots(SlotType.UPGRADE, 2);
    modData.setSlots(SlotType.ABILITY, 3);
    modData.setSlots(SlotType.TRAIT, 4);
    modData.putInt(testKey, 1);
    modData.put(testKey2, new CompoundNBT());

    CompoundNBT nbt = modData.getData();
    assertThat(nbt.getInt(SlotType.UPGRADE.getName())).isEqualTo(2);
    assertThat(nbt.getInt(SlotType.ABILITY.getName())).isEqualTo(3);
    assertThat(nbt.getInt(SlotType.TRAIT.getName())).isEqualTo(4);
    assertThat(nbt.getInt(testKey.toString())).isEqualTo(1);
    assertThat(nbt.contains(testKey2.toString(), NBT.TAG_COMPOUND)).isTrue();
  }

  @Test
  void deserialize() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.putInt(SlotType.UPGRADE.getName(), 4);
    nbt.putInt(SlotType.ABILITY.getName(), 5);
    nbt.putInt(SlotType.TRAIT.getName(), 6);
    nbt.putString(testKey.toString(), "Not sure why you need strings");
    CompoundNBT tag = new CompoundNBT();
    tag.putInt("test", 1);
    nbt.put(testKey2.toString(), tag);

    ModDataNBT modData = ModDataNBT.readFromNBT(nbt);
    assertThat(modData.getSlots(SlotType.UPGRADE)).isEqualTo(4);
    assertThat(modData.getSlots(SlotType.ABILITY)).isEqualTo(5);
    assertThat(modData.getSlots(SlotType.TRAIT)).isEqualTo(6);
    assertThat(modData.getString(testKey)).isEqualTo("Not sure why you need strings");

    tag = modData.getCompound(testKey2);
    assertThat(tag.isEmpty()).isFalse();
    assertThat(tag.contains("test", NBT.TAG_ANY_NUMERIC)).isTrue();
    assertThat(tag.getInt("test")).isEqualTo(1);
  }

  @Test
  void deprecatedMethodsStillFunction() {
    ModDataNBT nbt = new ModDataNBT();
    nbt.setUpgrades(1);
    nbt.setAbilities(2);
    nbt.setTraits(3);
    assertThat(nbt.getSlots(SlotType.UPGRADE)).isEqualTo(1);
    assertThat(nbt.getSlots(SlotType.ABILITY)).isEqualTo(2);
    assertThat(nbt.getSlots(SlotType.TRAIT)).isEqualTo(3);

    nbt = new ModDataNBT();
    nbt.setSlots(SlotType.UPGRADE, 4);
    nbt.setSlots(SlotType.ABILITY, 5);
    nbt.setSlots(SlotType.TRAIT, 6);
    assertThat(nbt.getUpgrades()).isEqualTo(4);
    assertThat(nbt.getAbilities()).isEqualTo(5);
    assertThat(nbt.getTraits()).isEqualTo(6);

    nbt.addUpgrades(7);
    nbt.addAbilities(8);
    nbt.addTraits(9);
    assertThat(nbt.getSlots(SlotType.UPGRADE)).isEqualTo(11);
    assertThat(nbt.getSlots(SlotType.ABILITY)).isEqualTo(13);
    assertThat(nbt.getSlots(SlotType.TRAIT)).isEqualTo(15);
  }
}
