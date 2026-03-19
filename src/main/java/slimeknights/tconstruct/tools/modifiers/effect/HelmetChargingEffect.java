package slimeknights.tconstruct.tools.modifiers.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Effect for rendering the charge up when you start using a helmet */
public class HelmetChargingEffect extends MobEffect {
  public HelmetChargingEffect() {
    super(MobEffectCategory.NEUTRAL, -1);
  }

  @Override
  public List<ItemStack> getCurativeItems() {
    return new ArrayList<>();
  }

  @Override
  public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
    consumer.accept(new IClientMobEffectExtensions() {
      private static final ResourceLocation BAR_KEY = TConstruct.getResource("helmet_charging_bar");
      private final Minecraft mc = Minecraft.getInstance();

      @Override
      public boolean isVisibleInInventory(MobEffectInstance instance) {
        return false;
      }

      @Override
      public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics graphics, int x, int y, float z, float alpha) {
        // start by drawing the original texture, skip alpha
        MobEffectTextureManager textures = mc.getMobEffectTextures();
        TextureAtlasSprite sprite = textures.get(instance.getEffect());
        graphics.blit(x + 3, y + 3, 0, 18, 18, sprite);

        // if the helmet has a drawtime, render the extra bar
        if (mc.player != null) {
          ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
          if (!helmet.isEmpty()) {
            // if we have a drawtime, draw the bar overlaying the icon
            int duration = instance.getDuration();
            int drawtime = ModifierUtil.getPersistentInt(helmet, GeneralInteractionModifierHook.KEY_DRAWTIME, 0);
            int dd = drawtime + 20;
            if (drawtime > 0 && duration < dd) {
              sprite = textures.getSprite(BAR_KEY);
              int height;
              if (duration < 20) {
                height = 18;
              } else {
                height = (dd - duration) * 18 / drawtime;
              }
              float v0 = sprite.getV0(), v1 = sprite.getV1();
              int yOffset = (18 - height);
              graphics.innerBlit(sprite.atlasLocation(), x + 3, x + 21, y + 3 + yOffset, y + 21, 0, sprite.getU0(), sprite.getU1(), v0 + (v1 - v0) * yOffset / 18f, v1);
            }
          }
        }
        return true;
      }
    });
  }


  /* Helpers */

  /** Starts using the helmet with the charge time rendering */
  public static int startUsingHelmet(IToolStackView tool, LivingEntity living, float speedFactor) {
    int time = GeneralInteractionModifierHook.startDrawing(tool, living, speedFactor);
    living.addEffect(new MobEffectInstance(TinkerModifiers.helmetCharging.get(), time + 20, 0, true, false, true));
    return time;
  }
}
