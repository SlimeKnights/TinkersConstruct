package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
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
      assertThat(IModDataView.EMPTY.getSlots(type)).isEqualTo(0);
    }

    CompoundTag nbt = IModDataView.EMPTY.getCompound(testKey);
    nbt.putInt("test", 1);
    nbt = IModDataView.EMPTY.getCompound(testKey);
    assertThat(nbt.contains("test")).overridingErrorMessage("NBT not saved in empty").isFalse();
  }

  @Test
  void defaults() {
    ModDataNBT nbt = new ModDataNBT();

    for (SlotType type : SlotType.getAllSlotTypes()) {
      assertThat(IModDataView.EMPTY.getSlots(type)).isEqualTo(0);
    }
    assertThat(nbt.getData().isEmpty()).isTrue();
  }

  @Test
  void serialize() {
    ModDataNBT modData = new ModDataNBT();
    modData.setSlots(SlotType.UPGRADE, 2);
    modData.setSlots(SlotType.ABILITY, 3);
    modData.setSlots(SlotType.SOUL, 4);
    modData.putInt(testKey, 1);
    modData.put(testKey2, new CompoundTag());

    CompoundTag nbt = modData.getData();
    assertThat(nbt.getInt(SlotType.UPGRADE.getName())).isEqualTo(2);
    assertThat(nbt.getInt(SlotType.ABILITY.getName())).isEqualTo(3);
    assertThat(nbt.getInt(SlotType.SOUL.getName())).isEqualTo(4);
    assertThat(nbt.getInt(testKey.toString())).isEqualTo(1);
    assertThat(nbt.contains(testKey2.toString(), Tag.TAG_COMPOUND)).isTrue();
  }

  @Test
  void deserialize() {
    CompoundTag nbt = new CompoundTag();
    nbt.putInt(SlotType.UPGRADE.getName(), 4);
    nbt.putInt(SlotType.ABILITY.getName(), 5);
    nbt.putInt(SlotType.SOUL.getName(), 6);
    nbt.putString(testKey.toString(), "Not sure why you need strings");
    CompoundTag tag = new CompoundTag();
    tag.putInt("test", 1);
    nbt.put(testKey2.toString(), tag);

    ModDataNBT modData = ModDataNBT.readFromNBT(nbt);
    assertThat(modData.getSlots(SlotType.UPGRADE)).isEqualTo(4);
    assertThat(modData.getSlots(SlotType.ABILITY)).isEqualTo(5);
    assertThat(modData.getSlots(SlotType.SOUL)).isEqualTo(6);
    assertThat(modData.getString(testKey)).isEqualTo("Not sure why you need strings");

    tag = modData.getCompound(testKey2);
    assertThat(tag.isEmpty()).isFalse();
    assertThat(tag.contains("test", Tag.TAG_ANY_NUMERIC)).isTrue();
    assertThat(tag.getInt("test")).isEqualTo(1);
  }
}
