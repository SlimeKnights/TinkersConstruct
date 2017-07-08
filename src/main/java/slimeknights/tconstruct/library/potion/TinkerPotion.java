package slimeknights.tconstruct.library.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class TinkerPotion extends Potion {

  private final boolean show;

  public TinkerPotion(ResourceLocation location, boolean badEffect, boolean showInInventory) {
    this(location, badEffect, showInInventory, 0xffffff);
  }

  public TinkerPotion(ResourceLocation location, boolean badEffect, boolean showInInventory, int color) {
    super(badEffect, color);
    setPotionName("potion." + location.getResourcePath());

    this.setRegistryName(location);

    this.show = showInInventory;
  }

  @Override
  public boolean shouldRenderInvText(PotionEffect effect) {
    return show;
  }

  public PotionEffect apply(EntityLivingBase entity, int duration) {
    return apply(entity, duration, 0);
  }

  public PotionEffect apply(EntityLivingBase entity, int duration, int level) {
    PotionEffect effect = new PotionEffect(this, duration, level, false, false);
    entity.addPotionEffect(effect);
    return effect;
  }

  public int getLevel(EntityLivingBase entity) {
    PotionEffect effect = entity.getActivePotionEffect(this);
    if(effect != null) {
      return effect.getAmplifier();
    }
    return 0;
  }

  @Override
  public boolean shouldRender(PotionEffect effect) {
    return show;
  }
}
