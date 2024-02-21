package slimeknights.tconstruct.common;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.client.EffectRenderer;

import java.util.function.Consumer;

/** Effect extension with a few helpers */
public class TinkerEffect extends MobEffect {
  /** If true, effect is visible, false for hidden */
  private final boolean show;
  public TinkerEffect(MobEffectCategory typeIn, boolean show) {
    this(typeIn, 0xffffff, show);
  }

  public TinkerEffect(MobEffectCategory typeIn, int color, boolean show) {
    super(typeIn, color);
    this.show = show;
  }

  // override to change return type
  @Override
  public TinkerEffect addAttributeModifier(Attribute pAttribute, String pUuid, double pAmount, Operation pOperation) {
    super.addAttributeModifier(pAttribute, pUuid, pAmount, pOperation);
    return this;
  }

  /* Visibility */

  @Override
  public void initializeClient(Consumer<EffectRenderer> consumer) {
    consumer.accept(new EffectRenderer() {
      @Override
      public void renderInventoryEffect(MobEffectInstance effect, EffectRenderingInventoryScreen<?> gui, PoseStack mStack, int x, int y, float z) {}

      @Override
      public void renderHUDEffect(MobEffectInstance effect, GuiComponent gui, PoseStack mStack, int x, int y, float z, float alpha) {}

      @Override
      public boolean shouldRenderInvText(MobEffectInstance effect) {
        return show;
      }

      @Override
      public boolean shouldRender(MobEffectInstance effect) {
        return show;
      }

      @Override
      public boolean shouldRenderHUD(MobEffectInstance effect) {
        return show;
      }
    });
  }

  /* Helpers */

  /**
   * Applies this potion to an entity
   * @param entity    Entity
   * @param duration  Duration
   * @return  Applied instance
   */
  public MobEffectInstance apply(LivingEntity entity, int duration) {
    return this.apply(entity, duration, 0);
  }

  /**
   * Applies this potion to an entity
   * @param entity    Entity
   * @param duration  Duration
   * @param level     Effect level
   * @return  Applied instance
   */
  public MobEffectInstance apply(LivingEntity entity, int duration, int level) {
    return this.apply(entity, duration, level, false);
  }

  /**
   * Applies this potion to an entity
   * @param entity    Entity
   * @param duration  Duration
   * @param level     Effect level
   * @param showIcon  If true, shows an icon in the HUD
   * @return  Applied instance
   */
  public MobEffectInstance apply(LivingEntity entity, int duration, int level, boolean showIcon) {
    MobEffectInstance effect = new MobEffectInstance(this, duration, level, false, false, showIcon);
    entity.addEffect(effect);
    return effect;
  }

  /**
   * Gets the level of the effect on the entity, or -1 if not active
   * @param entity  Entity to check
   * @return  Level, or -1 if inactive
   */
  public int getLevel(LivingEntity entity) {
    MobEffectInstance effect = entity.getEffect(this);
    if (effect != null) {
      return effect.getAmplifier();
    }
    return -1;
  }

}
