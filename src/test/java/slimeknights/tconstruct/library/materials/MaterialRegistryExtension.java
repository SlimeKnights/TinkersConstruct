package slimeknights.tconstruct.library.materials;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.fixture.MaterialRegistryFixture;
import slimeknights.tconstruct.fixture.MaterialStatsFixture;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;

/**
 * Makes all materials from the {@link MaterialFixture} available during tests.
 */
public class MaterialRegistryExtension implements BeforeEachCallback, AfterAllCallback, ParameterResolver {

  private IMaterialRegistry materialRegistry;

  @Override
  public void beforeEach(ExtensionContext context) {
    Map<IMaterial, List<IMaterialStats>> materialSetup = MaterialFixture.ALL_MATERIAL_FIXTURES;

    Map<MaterialId, IMaterial> materials = materialSetup.keySet().stream()
      .collect(Collectors.toMap(IMaterial::getIdentifier, Function.identity()));
    Map<MaterialId, Map<MaterialStatsId, IMaterialStats>> stats = materialSetup.entrySet().stream()
      .collect(Collectors.toMap(
        entry -> entry.getKey().getIdentifier(),
        entry -> entry.getValue().stream().collect(Collectors.toMap(
          IMaterialStats::getIdentifier,
          Function.identity()
        ))
      ));

    Map<MaterialStatsId, IMaterialStats> defaultStats = MaterialStatsFixture.TIC_DEFAULT_STATS.stream()
      .collect(Collectors.toMap(IMaterialStats::getIdentifier, Function.identity()));

    // empty map as nothing using the extension uses traits
    materialRegistry = new MaterialRegistryFixture(materials, stats, defaultStats, Collections.emptyMap());
    MaterialRegistry.INSTANCE = new MaterialRegistry(materialRegistry);
  }

  @Override
  public void afterAll(ExtensionContext context) {
    // cleanup
    MaterialRegistry.INSTANCE = new MaterialRegistry(mock(IMaterialRegistry.class));
    MaterialRegistry.fullyLoaded = true;
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return MaterialRegistryImpl.class.isAssignableFrom(parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return materialRegistry;
  }
}
