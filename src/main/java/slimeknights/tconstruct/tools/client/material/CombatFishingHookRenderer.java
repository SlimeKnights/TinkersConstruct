package slimeknights.tconstruct.tools.client.material;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.TintedArmorTexture;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.utils.SimpleCache;
import slimeknights.tconstruct.tools.entity.CombatFishingHook;

import javax.annotation.Nullable;
import java.util.Optional;

/** Renderer for {@link CombatFishingHookRenderer}. Mostly a recreation of {@link net.minecraft.client.renderer.entity.FishingHookRenderer} */
public class CombatFishingHookRenderer extends EntityRenderer<CombatFishingHook> {
  /** Texture under the local folder */
  private static final ResourceLocation LOCAL = TConstruct.getResource("fishing_hook/material");
  /** Easier to append material variants without root */
  private static final ResourceLocation BASE = ArmorTextureSupplier.getTexturePath(LOCAL);

  /** Checks if the given texture is valid */
  @Nullable
  private static ResourceLocation tryTexture(String material) {
    ResourceLocation texture = LOCAL.withSuffix(material);
    if (ArmorTextureSupplier.TEXTURE_VALIDATOR.test(texture)) {
      return ArmorTextureSupplier.getTexturePath(texture);
    }
    return null;
  }

  /** Cache of texture and color for each material. */
  private static final SimpleCache<MaterialVariantId,MaterialTexture> TEXTURE_CACHE = new SimpleCache<>(material -> {
    if (!IMaterial.UNKNOWN_ID.equals(material)) {
      Optional<MaterialRenderInfo> infoOptional = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material);
      int color = -1;
      int luminosity = 0;
      if (infoOptional.isPresent()) {
        MaterialRenderInfo info = infoOptional.get();
        // first try untinted
        ResourceLocation untinted = info.texture();
        luminosity = info.luminosity();
        if (untinted != null) {
          ResourceLocation texture = tryTexture('_' + untinted.getNamespace() + '_' + untinted.getPath());
          if (texture != null) {
            return new MaterialTexture(texture, -1, luminosity);
          }
        }
        // fallback to tinted
        color = info.vertexColor();
        for (String fallback : info.fallbacks()) {
          ResourceLocation texture = tryTexture('_' + fallback);
          if (texture != null) {
            return new MaterialTexture(texture, color, luminosity);
          }
        }
      }
      // tint base texture
      return new MaterialTexture(ArmorTextureSupplier.getTexturePath(LOCAL), color, luminosity);
    }
    return MaterialTexture.EMPTY;
  });

  public CombatFishingHookRenderer(Context context) {
    super(context);
  }

  /** Clears any cache in the renderer. */
  public static void clearCache() {
    TEXTURE_CACHE.clear();
  }

  @Override
  public void render(CombatFishingHook hook, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
    Player player = hook.getPlayerOwner();
    if (player != null) {
      // setup rendering
      poseStack.pushPose();
      poseStack.pushPose();
      poseStack.scale(0.5F, 0.5F, 0.5F);
      poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
      poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

      // select material
      MaterialTexture texture = TEXTURE_CACHE.apply(hook.getMaterial());
      VertexConsumer consumer = buffer.getBuffer(texture.texture);
      int bobberLight = texture.applyLuminosity(packedLight);

      // render bobber
      PoseStack.Pose lastPose = poseStack.last();
      Matrix4f pose = lastPose.pose();
      Matrix3f normal = lastPose.normal();
      texture.vertex(consumer, pose, normal, bobberLight, 0f, 0, 0, 1);
      texture.vertex(consumer, pose, normal, bobberLight, 1f, 0, 1, 1);
      texture.vertex(consumer, pose, normal, bobberLight, 1f, 1, 1, 0);
      texture.vertex(consumer, pose, normal, bobberLight, 0f, 1, 0, 0);
      poseStack.popPose();

      // handle hand side
      int sideOffset = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
      ItemStack itemstack = player.getMainHandItem();
      if (!itemstack.canPerformAction(ToolActions.FISHING_ROD_CAST)) {
        sideOffset = -sideOffset;
      }

      // prepare string rendering
      double playerXo, playerYo, playerZo;
      float eyeHeight;
      if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
        double fov = 960.0 / this.entityRenderDispatcher.options.fov().get();
        Vec3 point = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane(sideOffset * 0.525f, -0.1f);
        point = point.scale(fov);
        float attackSin = Mth.sin(Mth.sqrt(player.getAttackAnim(partialTicks)) * (float)Math.PI);
        point = point.yRot(attackSin * 0.5f);
        point = point.xRot(-attackSin * 0.7f);
        playerXo = Mth.lerp(partialTicks, player.xo, player.getX()) + point.x;
        playerYo = Mth.lerp(partialTicks, player.yo, player.getY()) + point.y;
        playerZo = Mth.lerp(partialTicks, player.zo, player.getZ()) + point.z;
        eyeHeight = player.getEyeHeight();
      } else {
        float rotation = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot) * ((float)Math.PI / 180F);
        double rotationSin = Mth.sin(rotation);
        double rotationCos = Mth.cos(rotation);
        double scaledSide = sideOffset * 0.35D;
        playerXo = Mth.lerp(partialTicks, player.xo, player.getX()) - rotationCos * scaledSide - rotationSin * 0.8D;
        playerYo = player.yo + player.getEyeHeight() + (player.getY() - player.yo) * (double)partialTicks - 0.45D;
        playerZo = Mth.lerp(partialTicks, player.zo, player.getZ()) - rotationSin * scaledSide + rotationCos * 0.8D;
        eyeHeight = player.isCrouching() ? -0.1875f : 0f;
      }

      // render the string
      float hookXOff = (float)(playerXo - Mth.lerp(partialTicks, hook.xo, hook.getX()));
      float hookYOff = (float)(playerYo - Mth.lerp(partialTicks, hook.yo, hook.getY()) - 0.25D) + eyeHeight;
      float hookZOff = (float)(playerZo - Mth.lerp(partialTicks, hook.zo, hook.getZ()));
      consumer = buffer.getBuffer(RenderType.lineStrip());
      lastPose = poseStack.last();
      for (int i = 0; i <= 16; i++) {
        FishingHookRenderer.stringVertex(hookXOff, hookYOff, hookZOff, consumer, lastPose, i / 16f, (i + 1) / 16f);
      }

      poseStack.popPose();
      super.render(hook, yaw, partialTicks, poseStack, buffer, packedLight);
    }
  }

  @Override
  public ResourceLocation getTextureLocation(CombatFishingHook pEntity) {
    return BASE;
  }

  private record MaterialTexture(RenderType texture, int luminosity, int alpha, int red, int green, int blue) {
    public static final MaterialTexture EMPTY = new MaterialTexture(BASE, -1, 0);

    public MaterialTexture(ResourceLocation texture, int color, int luminosity) {
      this(RenderType.entityCutout(texture), luminosity,
        color >> 24 & 255,
        color >> 16 & 255,
        color >> 8 & 255,
        color & 255
      );
    }

    /** Applies luminosity to the given lightmap color */
    public int applyLuminosity(int packedLight) {
      if (luminosity > 0) {
        return TintedArmorTexture.applyLuminosity(packedLight, luminosity);
      }
      return packedLight;
    }

    /** Draws a vertex using this texture. */
    public void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int lightmap, float pX, int pY, int pU, int pV) {
      consumer.vertex(pose, pX - 0.5f, pY - 0.5f, 0f)
        .color(red, green, blue, alpha)
        .uv(pU, pV)
        .overlayCoords(OverlayTexture.NO_OVERLAY)
        .uv2(lightmap)
        .normal(normal, 0.0F, 1.0F, 0.0F)
        .endVertex();
    }
  }
}
