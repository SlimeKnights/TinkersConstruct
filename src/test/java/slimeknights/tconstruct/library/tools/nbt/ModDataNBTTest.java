package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

class ModDataNBTTest extends BaseMcTest {
  private static final ResourceLocation testKey = new ResourceLocation("test");
  private static final ResourceLocation testKey2 = new ResourceLocation("test2");

  @Test
  void empty() {
    assertThat(IModDataReadOnly.EMPTY.getModifiers()).isEqualTo(0);
    assertThat(IModDataReadOnly.EMPTY.getAbilities()).isEqualTo(0);

    CompoundNBT nbt = IModDataReadOnly.EMPTY.getCompound(testKey);
    nbt.putInt("test", 1);
    nbt = IModDataReadOnly.EMPTY.getCompound(testKey);
    assertThat(nbt.contains("test")).overridingErrorMessage("NBT not saved in empty").isFalse();
  }

  @Test
  void defaults() {
    ModDataNBT nbt = new ModDataNBT();

    assertThat(nbt.getModifiers()).isEqualTo(0);
    assertThat(nbt.getAbilities()).isEqualTo(0);
    assertThat(nbt.getData().isEmpty()).isTrue();
  }

  @Test
  void serialize() {
    ModDataNBT modData = new ModDataNBT();
    modData.setModifiers(2);
    modData.setAbilities(3);
    modData.putInt(testKey, 1);
    modData.put(testKey2, new CompoundNBT());

    CompoundNBT nbt = modData.getData();
    assertThat(nbt.getInt(ModDataNBT.TAG_MODIFIERS)).isEqualTo(2);
    assertThat(nbt.getInt(ModDataNBT.TAG_ABILITIES)).isEqualTo(3);
    assertThat(nbt.getInt(testKey.toString())).isEqualTo(1);
    assertThat(nbt.contains(testKey2.toString(), NBT.TAG_COMPOUND)).isTrue();
  }

  @Test
  void deserialize() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.putInt(ModDataNBT.TAG_MODIFIERS, 4);
    nbt.putInt(ModDataNBT.TAG_ABILITIES, 5);
    nbt.putString(testKey.toString(), "Not sure why you need strings");
    CompoundNBT tag = new CompoundNBT();
    tag.putInt("test", 1);
    nbt.put(testKey2.toString(), tag);

    ModDataNBT modData = ModDataNBT.readFromNBT(nbt);
    assertThat(modData.getModifiers()).isEqualTo(4);
    assertThat(modData.getAbilities()).isEqualTo(5);
    assertThat(modData.getString(testKey)).isEqualTo("Not sure why you need strings");

    tag = modData.getCompound(testKey2);
    assertThat(tag.isEmpty()).isFalse();
    assertThat(tag.contains("test", NBT.TAG_ANY_NUMERIC)).isTrue();
    assertThat(tag.getInt("test")).isEqualTo(1);
  }
}
