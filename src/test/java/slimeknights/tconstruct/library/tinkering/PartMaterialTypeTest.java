package slimeknights.tconstruct.library.tinkering;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.fixture.MaterialItemFixture;
import slimeknights.tconstruct.fixture.PartMaterialTypeFixture;
import slimeknights.tconstruct.library.MaterialRegistryExtension;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MaterialRegistryExtension.class)
class PartMaterialTypeTest extends BaseMcTest {

  @Test
  void isValid() {
    MaterialItem materialItem = MaterialItemFixture.MATERIAL_ITEM;
    Material material1 = MaterialFixture.MATERIAL_1;

    boolean valid = PartMaterialTypeFixture.PART_MATERIAL_TYPE.isValid(materialItem, material1);

    assertThat(valid).isTrue();
  }
}
