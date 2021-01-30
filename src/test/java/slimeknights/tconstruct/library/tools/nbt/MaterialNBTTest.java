package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.library.MaterialRegistryExtension;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MaterialRegistryExtension.class)
class MaterialNBTTest extends BaseMcTest {

  public static final IMaterial TEST_MATERIAL_1 = MaterialFixture.MATERIAL_1;
  public static final IMaterial TEST_MATERIAL_2 = MaterialFixture.MATERIAL_2;
  private final MaterialNBT testMaterialNBT = new MaterialNBT(ImmutableList.of(TEST_MATERIAL_1, TEST_MATERIAL_2));

  @Test
  void serialize() {
    ListNBT nbtList = testMaterialNBT.serializeToNBT();

    assertThat(nbtList).hasSize(2);
    assertThat(nbtList).extracting(INBT::getString).containsExactly(
      TEST_MATERIAL_1.getIdentifier().toString(),
      TEST_MATERIAL_2.getIdentifier().toString()
    );
  }

  @Test
  void serializeEmpty_emptyList() {
    ListNBT nbtList = MaterialNBT.EMPTY.serializeToNBT();

    assertThat(nbtList).isEmpty();
  }

  @Test
  void deserialize() {
    ListNBT nbtList = new ListNBT();
    // note we switched the order here to ensure that the order is as defined, and not ordered in some way
    nbtList.add(StringNBT.valueOf(TEST_MATERIAL_2.getIdentifier().toString()));
    nbtList.add(StringNBT.valueOf(TEST_MATERIAL_1.getIdentifier().toString()));

    MaterialNBT materialNBT = MaterialNBT.readFromNBT(nbtList);

    assertThat(materialNBT.getMaterials()).containsExactly(TEST_MATERIAL_2, TEST_MATERIAL_1);
  }

  @Test
  void deserialize_emptyList() {
    ListNBT nbtList = new ListNBT();

    MaterialNBT materialNBT = MaterialNBT.readFromNBT(nbtList);

    assertThat(materialNBT.getMaterials()).isEmpty();
  }

  @Test
  void wrongNbtType_emptyList() {
    INBT wrongNbt = new CompoundNBT();

    MaterialNBT materialNBT = MaterialNBT.readFromNBT(wrongNbt);

    assertThat(materialNBT.getMaterials()).isEmpty();
  }

  @Test
  void wrongListNbtType_emptyList() {
    ListNBT wrongNbt = new ListNBT();
    wrongNbt.add(new CompoundNBT());

    MaterialNBT materialNBT = MaterialNBT.readFromNBT(wrongNbt);

    assertThat(materialNBT.getMaterials()).isEmpty();
  }

  @Test
  void replaceMaterial() {
    MaterialNBT nbt = MaterialNBT.EMPTY;
    assertThat(nbt.getMaterial(1)).isEqualTo(IMaterial.UNKNOWN);

    // note 1 is out of bounds of empty, but replace material needs to be able to extend the material list to deal with errors
    nbt = nbt.replaceMaterial(1, MaterialFixture.MATERIAL_1);

    assertThat(nbt).overridingErrorMessage("replaceMaterial should not modify the original").isNotEqualTo(MaterialNBT.EMPTY);
    assertThat(nbt.getMaterial(1)).isEqualTo(MaterialFixture.MATERIAL_1);
  }
}
