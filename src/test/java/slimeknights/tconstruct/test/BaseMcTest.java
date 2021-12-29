package slimeknights.tconstruct.test;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import org.junit.jupiter.api.BeforeAll;

import static org.mockito.Mockito.mock;

public class BaseMcTest {

  @BeforeAll
  static void setUpRegistries() {
    SharedConstants.setVersion(TestWorldVersion.INSTANCE);
    Bootstrap.bootStrap();
    ModLoadingContext.get().setActiveContainer(new TestModContainer(mock(IModInfo.class)));
  }
}
