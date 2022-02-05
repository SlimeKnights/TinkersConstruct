package slimeknights.tconstruct.test;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.network.NetworkRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class BaseMcTest {

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @BeforeAll
  static void setUpRegistries() {
    SharedConstants.setVersion(TestWorldVersion.INSTANCE);
    Bootstrap.bootStrap();
    ModLoadingContext.get().setActiveContainer(new TestModContainer(mock(IModInfo.class)));

    // ensure during static initialization, we don't load channel stuff that we lack access to
    try (MockedStatic<NetworkRegistry> mockNetwork = Mockito.mockStatic(NetworkRegistry.class)) {
      mockNetwork.when(() -> NetworkRegistry.newSimpleChannel(any(), any(), any(), any())).thenReturn(null);
      TierSortingRegistry.getSortedTiers();
    }
  }

  /** No need to set it up multiple times */
  private static boolean setupTiers = false;

  /** Sets up the forge tier sorting registry */
  public static void setupTierSorting() {
    if (setupTiers) {
      return;
    }
    setupTiers = true;
    try {
      Method method = TierSortingRegistry.class.getDeclaredMethod("recalculateItemTiers");
      method.setAccessible(true);
      method.invoke(TierSortingRegistry.class);
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }
}
