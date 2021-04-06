package slimeknights.tconstruct.plugin.jei.entity;

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
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Renderer for entity type ingredients
 */
@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
public class EntityIngredientRenderer implements IIngredientRenderer<EntityType> {
  /** Entity types that will not render, as they either errored or are the wrong type */
  private static final Set<EntityType<?>> IGNORED_ENTITIES = new HashSet<>();

  private final int size;

  /** Cache of entities for each entity type */
  private final Map<EntityType<?>,Entity> ENTITY_MAP = new HashMap<>();

  @Override
  public void render(MatrixStack matrixStack, int x, int y, @Nullable EntityType type) {
    if (type != null) {
      World world = Minecraft.getInstance().world;
      if (world != null && !IGNORED_ENTITIES.contains(type)) {
        Entity entity;
        // players cannot be created using the type, but we can use the client player
        // side effect is it renders armor/items
        if (type == EntityType.PLAYER) {
          entity = Minecraft.getInstance().player;
        } else {
          // entity is created with the client world, but the entity map is thrown away when JEI restarts so they should be okay I think
          entity = ENTITY_MAP.computeIfAbsent(type, t -> t.create(world));
        }
        // only can draw living entities, plus non-living ones don't get recipes anyways
        if (entity instanceof LivingEntity) {
          // scale down large mobs, but don't scale up small ones
          LivingEntity livingEntity = (LivingEntity) entity;
          int scale = size / 2;
          float height = entity.getHeight();
          float width = entity.getWidth();
          if (height > 2 || width > 2) {
            scale = (int)(size / Math.max(height, width));
          }
          // catch exceptions drawing the entity to be safe, any caught exceptions blacklist the entity
          try {
            InventoryScreen.drawEntityOnScreen(x + size / 2, y + size, scale, 0, 10, livingEntity);
            return;
          } catch (Exception e) {
            TConstruct.log.error("Error drawing entity " + type.getRegistryName(), e);
            IGNORED_ENTITIES.add(type);
            ENTITY_MAP.remove(type);
          }
        } else {
          // not living, so might as well skip next time
          IGNORED_ENTITIES.add(type);
          ENTITY_MAP.remove(type);
        }
      }

      // fallback, draw a pink and black "spawn egg"
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
