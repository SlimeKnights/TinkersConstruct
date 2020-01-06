package slimeknights.tconstruct.library;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.library.materials.IMaterial;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Makes all materials from the {@link MaterialFixture} available during tests.
 */
public class MaterialRegistryExtension implements BeforeEachCallback, AfterAllCallback {

  @Override
  public void beforeEach(ExtensionContext context) {
    MaterialRegistryImpl mock = mock(MaterialRegistryImpl.class);
    new MaterialRegistry().init(mock);

    MaterialFixture.ALL_MATERIALS.forEach(material -> mockMaterial(mock, material));
  }

  @Override
  public void afterAll(ExtensionContext context) {
    // cleanup
    new MaterialRegistry().init(mock(MaterialRegistryImpl.class));
  }

  private void mockMaterial(MaterialRegistryImpl mock, IMaterial testMaterial1) {
    when(mock.getMaterial(testMaterial1.getIdentifier())).thenReturn(testMaterial1);
  }
}
