package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.ModifierFixture;

import static org.assertj.core.api.Assertions.assertThat;

public class ModifierNBTest {
  @BeforeAll
  static void before() {
    ModifierFixture.init();
  }

  @Test
  void modifierBuilder_empty() {
    ModifierNBT modifierNBT = ModifierNBT.builder().build();

    assertThat(modifierNBT.getModifiers().size()).isEqualTo(0);
    assertThat(modifierNBT.getLevel(ModifierFixture.TEST_MODIFIER_1)).isEqualTo(0);
  }

  @Test
  void modifierBuilder_single() {
    ModifierNBT.Builder builder = ModifierNBT.builder();
    builder.add(ModifierFixture.TEST_MODIFIER_1, 2);
    ModifierNBT modifierNBT = builder.build();

    assertThat(modifierNBT.getModifiers().size()).isEqualTo(1);
    assertThat(modifierNBT.getLevel(ModifierFixture.TEST_MODIFIER_1)).isEqualTo(2);
  }

  @Test
  void modifierBuilder_added() {
    ModifierNBT.Builder builder = ModifierNBT.builder();
    builder.add(ModifierFixture.TEST_MODIFIER_1, 2);
    builder.add(ModifierFixture.TEST_MODIFIER_1, 1);
    ModifierNBT modifierNBT = builder.build();

    assertThat(modifierNBT.getModifiers().size()).isEqualTo(1);
    assertThat(modifierNBT.getLevel(ModifierFixture.TEST_MODIFIER_1)).isEqualTo(3);
  }

  @Test
  void modifierBuilder_multiple() {
    ModifierNBT.Builder builder = ModifierNBT.builder();
    builder.add(ModifierFixture.TEST_MODIFIER_1, 2);
    builder.add(ModifierFixture.TEST_MODIFIER_2, 1);
    ModifierNBT modifierNBT = builder.build();

    assertThat(modifierNBT.getModifiers().size()).isEqualTo(2);
    assertThat(modifierNBT.getLevel(ModifierFixture.TEST_MODIFIER_1)).isEqualTo(2);
    assertThat(modifierNBT.getLevel(ModifierFixture.TEST_MODIFIER_2)).isEqualTo(1);
  }

  @Test
  void withModifier() {
    ModifierNBT modifierNBT = ModifierNBT.EMPTY.withModifier(ModifierFixture.TEST_MODIFIER_1, 1);
    assertThat(modifierNBT.getModifiers().size()).isEqualTo(1);
    assertThat(modifierNBT.getLevel(ModifierFixture.TEST_MODIFIER_1)).isEqualTo(1);
  }

  @Test
  void serialize() {
    ModifierNBT.Builder builder = ModifierNBT.builder();
    builder.add(ModifierFixture.TEST_MODIFIER_1, 2);
    builder.add(ModifierFixture.TEST_MODIFIER_2, 3);

    ListNBT list = builder.build().serializeToNBT();
    assertThat(list.size()).isEqualTo(2);
    CompoundNBT tag = list.getCompound(0);
    assertThat(tag.getString(ModifierNBT.TAG_MODIFIER)).isEqualTo(ModifierFixture.TEST_1.toString());
    assertThat(tag.getInt(ModifierNBT.TAG_LEVEL)).isEqualTo(2);

    tag = list.getCompound(1);
    assertThat(tag.getString(ModifierNBT.TAG_MODIFIER)).isEqualTo(ModifierFixture.TEST_2.toString());
    assertThat(tag.getInt(ModifierNBT.TAG_LEVEL)).isEqualTo(3);
  }

  @Test
  void deserialize() {
    ListNBT list = new ListNBT();
    CompoundNBT tag = new CompoundNBT();
    tag.putString(ModifierNBT.TAG_MODIFIER, ModifierFixture.TEST_1.toString());
    tag.putInt(ModifierNBT.TAG_LEVEL, 2);
    list.add(tag);
    tag = new CompoundNBT();
    tag.putString(ModifierNBT.TAG_MODIFIER, ModifierFixture.TEST_2.toString());
    tag.putInt(ModifierNBT.TAG_LEVEL, 3);
    list.add(tag);

    ModifierNBT modifierNBT = ModifierNBT.readFromNBT(list);
    assertThat(modifierNBT.getLevel(ModifierFixture.TEST_MODIFIER_1)).isEqualTo(2);
    assertThat(modifierNBT.getLevel(ModifierFixture.TEST_MODIFIER_2)).isEqualTo(3);
  }

  @Test
  void deserializeNoData_empty() {
    ListNBT nbt = new ListNBT();

    ModifierNBT modifierNBT = ModifierNBT.readFromNBT(nbt);

    assertThat(modifierNBT).isEqualTo(ModifierNBT.EMPTY);
  }

  @Test
  void wrongNbtType_empty() {
    INBT wrongNbt = new CompoundNBT();

    ModifierNBT modifierNBT = ModifierNBT.readFromNBT(wrongNbt);

    assertThat(modifierNBT).isEqualTo(ModifierNBT.EMPTY);
  }
}
