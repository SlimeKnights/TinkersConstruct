package slimeknights.tconstruct;

import net.minecraft.util.registry.Bootstrap;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import org.junit.jupiter.api.BeforeAll;

import static org.mockito.Mockito.mock;

public class BaseMcTest {

  @BeforeAll
  static void setUpRegistries() {
    Bootstrap.register();
    setupMockModLoadingContext();
  }

  private static void setupMockModLoadingContext() {
    ModContainer modContainer = mock(ModContainer.class);
    ModLoadingContext.get().setActiveContainer(modContainer, null);
  }
}
