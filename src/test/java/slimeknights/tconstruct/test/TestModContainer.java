package slimeknights.tconstruct.test;

import net.neoforged.bus.api.BusBuilder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforgespi.language.IModInfo;

public class TestModContainer extends ModContainer {
  private final IEventBus eventBus = BusBuilder.builder().build();

  public TestModContainer(IModInfo info) {
    super(info);
  }

  @Override
  public IEventBus getEventBus() {
    return eventBus;
  }
}
