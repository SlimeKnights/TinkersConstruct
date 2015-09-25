package slimeknights.tconstruct.tools.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import slimeknights.tconstruct.library.Util;

public class TinkerPotion extends Potion {
  private static int idCounter = 32;

  // temp fix of potion array sizes
  static {
    Potion[] old = Potion.potionTypes;
    try {
      Field field = Potion.class.getDeclaredField("potionTypes");
      field.setAccessible(true);

      // remove final modifier from field
      Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

      Potion[] pots = Arrays.copyOf(old, 128);
      field.set(null, pots);
    } catch(NoSuchFieldException e) {
      e.printStackTrace();
    } catch(IllegalAccessException e) {
      e.printStackTrace();
    }
  }


  private final boolean show;

  public TinkerPotion(ResourceLocation location, boolean badEffect, boolean showInInventory) {
    super(idCounter++, location, badEffect, 0xffffff);
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
}
