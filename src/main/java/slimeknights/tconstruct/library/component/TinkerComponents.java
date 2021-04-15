package slimeknights.tconstruct.library.component;

import net.minecraft.util.Identifier;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import slimeknights.tconstruct.library.component.piggyback.TinkerPiggybackComponent;

public class TinkerComponents implements EntityComponentInitializer {
	public static final ComponentKey<TinkerPiggybackComponent> TINKER_PIGGYBACK_COMPONENT_KEY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("dimdoors:counter"), TinkerPiggybackComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(TINKER_PIGGYBACK_COMPONENT_KEY, TinkerPiggybackComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
	}
}
