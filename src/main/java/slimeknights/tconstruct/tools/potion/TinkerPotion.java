package slimeknights.tconstruct.tools.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class TinkerPotion extends Potion {
  private final boolean show;

  public TinkerPotion(ResourceLocation location, boolean badEffect, boolean showInInventory) {
    super(location, badEffect, 0xffffff);
    setPotionName("potion." + location.getResourcePath());

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
    PotionEffect effect = new PotionEffect(this.id, duration, level, false, false);
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
    return false;
  }
}
