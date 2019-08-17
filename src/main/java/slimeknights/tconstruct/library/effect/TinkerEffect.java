package slimeknights.tconstruct.library.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

public class TinkerEffect extends Effect {

  private final boolean show;

  public TinkerEffect(ResourceLocation location, EffectType typeIn, boolean showInInventory) {
    this(location, typeIn, showInInventory, 0xffffff);
  }

  public TinkerEffect(ResourceLocation location, EffectType typeIn, boolean showInInventory, int color) {
    super(typeIn, color);

    this.setRegistryName(location);

    this.show = showInInventory;
  }

  @Override
  public boolean shouldRenderInvText(EffectInstance effect) {
    return this.show;
  }

  public EffectInstance apply(LivingEntity entity, int duration) {
    return this.apply(entity, duration, 0);
  }

  public EffectInstance apply(LivingEntity entity, int duration, int level) {
    EffectInstance effect = new EffectInstance(this, duration, level, false, false);
    entity.addPotionEffect(effect);
    return effect;
  }

  public int getLevel(LivingEntity entity) {
    EffectInstance effect = entity.getActivePotionEffect(this);
    if (effect != null) {
      return effect.getAmplifier();
    }
    return 0;
  }

  @Override
  public boolean shouldRender(EffectInstance effect) {
    return this.show;
  }

}
