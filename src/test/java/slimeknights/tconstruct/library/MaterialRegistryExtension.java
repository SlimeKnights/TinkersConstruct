package slimeknights.tconstruct.library;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.library.materials.IMaterial;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static slimeknights.tconstruct.fixture.MaterialFixture.*;
import static slimeknights.tconstruct.fixture.MaterialStatsFixture.*;

/**
 * Makes all materials from the {@link MaterialFixture} available during tests.
 */
public class MaterialRegistryExtension implements BeforeEachCallback, AfterAllCallback {

  @Override
  public void beforeEach(ExtensionContext context) {
    MaterialRegistryImpl mock = mock(MaterialRegistryImpl.class);
    MaterialRegistry.INSTANCE = mock;

    ALL_MATERIALS.forEach(material -> mockMaterial(mock, material));
    when(mock.getMaterialStats(eq(MATERIAL_1.getIdentifier()), eq(STATS_TYPE))).thenReturn(Optional.of(MATERIAL_STATS));
    when(mock.getMaterialStats(eq(MATERIAL_2.getIdentifier()), eq(STATS_TYPE_2))).thenReturn(Optional.of(MATERIAL_STATS_2));
  }

  @Override
  public void afterAll(ExtensionContext context) {
    // cleanup
    MaterialRegistry.INSTANCE = mock(MaterialRegistryImpl.class);
  }

  private void mockMaterial(MaterialRegistryImpl mock, IMaterial testMaterial1) {
    when(mock.getMaterial(testMaterial1.getIdentifier())).thenReturn(testMaterial1);
  }
}
