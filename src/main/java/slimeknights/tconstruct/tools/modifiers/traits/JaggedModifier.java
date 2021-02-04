package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.UUID;
import java.util.function.BiConsumer;

public class JaggedModifier extends Modifier {
  private static final UUID ATTRIBUTE_UUID = UUID.fromString("e9df53f0-65e0-11eb-ae93-0242ac130002");
  public JaggedModifier() {
    super(0x696969);
  }

  @Override
  public void addAttributes(IModifierToolStack tool, int level, BiConsumer<Attribute,AttributeModifier> consumer) {

  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event) {

  }
}
