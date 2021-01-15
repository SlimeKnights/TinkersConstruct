package slimeknights.tconstruct.plugin.jei.entitymelting;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.RequiredArgsConstructor;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renderer for entity type ingredients
 */
@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
public class EntityIngredientRenderer implements IIngredientRenderer<EntityType> {
  private final int size;

  /** Cache of entities for each entity type */
  private static final Map<EntityType<?>,Entity> ENTITY_MAP = new HashMap<>();

  @Override
  public void render(MatrixStack matrixStack, int x, int y, @Nullable EntityType type) {
    if (type != null) {
      World world = Minecraft.getInstance().world;
      if (world != null) {
        Entity entity = ENTITY_MAP.computeIfAbsent(type, t -> t.create(world));
        if (entity instanceof LivingEntity) {
          LivingEntity livingEntity = (LivingEntity) entity;
          int scale = size / 2;
          float height = entity.getHeight();
          if (height > 2) {
            scale = (int)(size / height);
          }
          InventoryScreen.drawEntityOnScreen(x + size / 2, y + size, scale, 0, 10, livingEntity);
          return;
        }
      }

      Minecraft minecraft = Minecraft.getInstance();
      minecraft.getTextureManager().bindTexture(EntityMeltingRecipeCategory.BACKGROUND_LOC);
      int offset = (size - 16) / 2;
      Screen.blit(matrixStack, x + offset, y + offset, 149f, 58f, 16, 16, 256, 256);
    }
  }

  @Override
  public List<ITextComponent> getTooltip(EntityType type, ITooltipFlag flag) {
    List<ITextComponent> tooltip = new ArrayList<>();
    tooltip.add(type.getName());
    return tooltip;
  }
}
