package slimeknights.tconstruct.library.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

/** Client extensions for modifiable items. Used in non-armor items to adjust animations. */
public class ModifiableItemClientExtension implements IClientItemExtensions {
  private static final float PI = (float)Math.PI;

  public static final ModifiableItemClientExtension INSTANCE = new ModifiableItemClientExtension();

  protected ModifiableItemClientExtension() {}

  /** Static copy of {@link net.minecraft.client.renderer.ItemInHandRenderer#applyItemArmTransform(PoseStack, HumanoidArm, float)} */
  private static void applyItemArmTransform(PoseStack poseStack, float equippedProgress, int sideOffset) {
    poseStack.translate(sideOffset * 0.56f, -0.52f + equippedProgress * -0.6f, -0.72f);
  }

  @Override
  public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTicks, float equipProgress, float swingProgress) {
    // forge: why don't you give me the arm that is used on the next line???
    InteractionHand hand = arm == player.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    // this code is copied from ItemInHandRenderer#renderArmWithItem with changes made for Tinker tools
    // to avoid redundant operations, we copied even methods that are unmodified, changes are noted below
    int sideOffset = arm == HumanoidArm.RIGHT ? 1 : -1;
    if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == hand) {
      switch (stack.getUseAnimation()) {
        // merged BLOCK and NONE - same code
        case NONE, BLOCK:
          applyItemArmTransform(poseStack, equipProgress, sideOffset);
          break;

        case EAT:
        case DRINK:
          // start: applyEatTransform
          float timeLeft = player.getUseItemRemainingTicks() - partialTicks + 1;
          float percentage = timeLeft / stack.getUseDuration();
          if (percentage < 0.8f) {
            poseStack.translate(0, Mth.abs(Mth.cos(timeLeft / 4f * PI) * 0.1f), 0);
          }

          float speed = 1 - (float)Math.pow(percentage, 27);
          int sideOffset1 = arm == HumanoidArm.RIGHT ? 1 : -1;
          poseStack.translate(speed * 0.6F * (float) sideOffset1, speed * -0.5f, speed * 0);
          poseStack.mulPose(Axis.YP.rotationDegrees(sideOffset1 * speed * 90));
          poseStack.mulPose(Axis.XP.rotationDegrees(speed * 10));
          poseStack.mulPose(Axis.ZP.rotationDegrees(sideOffset1 * speed * 30));
          // end: applyEatTransform
          applyItemArmTransform(poseStack, equipProgress, sideOffset);
          break;

        case BOW:
          applyItemArmTransform(poseStack, equipProgress, sideOffset);
          poseStack.translate(sideOffset * -0.2785682f, 0.18344387f, 0.15731531f);
          poseStack.mulPose(Axis.XP.rotationDegrees(-13.935f));
          poseStack.mulPose(Axis.YP.rotationDegrees(sideOffset * 35.3f));
          poseStack.mulPose(Axis.ZP.rotationDegrees(sideOffset * -9.785f));
          float remainingTime = (float) stack.getUseDuration() - ((float) player.getUseItemRemainingTicks() - partialTicks + 1);
          // change: scale charge by drawtime instead of a flat 20f
          float charge = remainingTime / ModifierUtil.getPersistentInt(stack, GeneralInteractionModifierHook.KEY_DRAWTIME, 20);
          charge = (charge * charge + charge * 2) / 3;
          if (charge > 1) {
            charge = 1;
          }

          if (charge > 0.1f) {
            // change: inlined expressions, replaced (value * 0) with 0
            poseStack.translate(0, Mth.sin((remainingTime - 0.1f) * 1.3f) * (charge - 0.1f) * 0.004f, 0);
          }
          // change: replaced (value * 0) with 0
          poseStack.translate(0, 0, charge * 0.04f);
          poseStack.scale(1, 1, 1 + charge * 0.2f);
          poseStack.mulPose(Axis.YN.rotationDegrees(sideOffset * 45));
          break;

        case SPEAR:
          applyItemArmTransform(poseStack, equipProgress, sideOffset);
          poseStack.translate((float) sideOffset * -0.5f, 0.7f, 0.1f);
          // change: added 45 degrees to take into account tools being diagonal
          poseStack.mulPose(Axis.XP.rotationDegrees(-90));
          poseStack.mulPose(Axis.YP.rotationDegrees(sideOffset * 35.3f));
          poseStack.mulPose(Axis.ZP.rotationDegrees(sideOffset * -9.785f));
          float sRemainingTime = stack.getUseDuration() - (player.getUseItemRemainingTicks() - partialTicks + 1);
          // change: scale charge by drawtime instead of a flat 10f, tridents have 2.0 drawspeed I decided
          float sCharge = sRemainingTime / ModifierUtil.getPersistentInt(stack, GeneralInteractionModifierHook.KEY_DRAWTIME, 20);
          if (sCharge > 1) {
            sCharge = 1;
          }

          if (sCharge > 0.1F) {
            // change: inlined expressions, replaced (value * 0) with 0
            poseStack.translate(0, Mth.sin((sRemainingTime - 0.1F) * 1.3F) * (sCharge - 0.1F) * 0.004F, 0);
          }
          poseStack.translate(0, 0, sCharge * 0.2f);
          poseStack.scale(1, 1, 1 + sCharge * 0.2f);
          poseStack.mulPose(Axis.YN.rotationDegrees(sideOffset * 45));
          break;

        case BRUSH:
          // start: applyBrushTransform
          applyItemArmTransform(poseStack, equipProgress, sideOffset);
          float xRot = -15 + 75 * Mth.cos((1 - ((float) (player.getUseItemRemainingTicks() % 10) - partialTicks + 1) / 10) * 2 * PI);
          if (arm != HumanoidArm.RIGHT) {
            poseStack.translate(0.1, 0.83, 0.35);
            poseStack.mulPose(Axis.XP.rotationDegrees(-80));
            poseStack.mulPose(Axis.YP.rotationDegrees(-90));
            poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
            poseStack.translate(-0.3, 0.22, 0.35);
          } else {
            poseStack.translate(-0.25, 0.22, 0.35);
            poseStack.mulPose(Axis.XP.rotationDegrees(-80));
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(0));
            poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
          }
          // end: applyBrushTransform
      }
    } else if (player.isAutoSpinAttack()) {
      applyItemArmTransform(poseStack, equipProgress, sideOffset);
      poseStack.translate(sideOffset * -0.4f, 0.8f, 0.3f);
      poseStack.mulPose(Axis.YP.rotationDegrees(sideOffset * 105));
      poseStack.mulPose(Axis.ZP.rotationDegrees(sideOffset * -85));
    } else {
      poseStack.translate(
        sideOffset * -0.4f * Mth.sin(Mth.sqrt(swingProgress) * PI),
        0.2f * Mth.sin(Mth.sqrt(swingProgress) * PI * 2),
        -0.2f * Mth.sin(swingProgress * PI));
      applyItemArmTransform(poseStack, equipProgress, sideOffset);
      // begin: applyItemArmAttackTransform
      poseStack.mulPose(Axis.YP.rotationDegrees(sideOffset * (45 + Mth.sin(swingProgress * swingProgress * (float)Math.PI) * -20)));
      float rotation = Mth.sin(Mth.sqrt(swingProgress) * PI);
      poseStack.mulPose(Axis.ZP.rotationDegrees(sideOffset * rotation * -20));
      poseStack.mulPose(Axis.XP.rotationDegrees(rotation * -80));
      poseStack.mulPose(Axis.YP.rotationDegrees(sideOffset * -45));
      // end: applyItemArmAttackTransform
    }
    return true;
  }
}
