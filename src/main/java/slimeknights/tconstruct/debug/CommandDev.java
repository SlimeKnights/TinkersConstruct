package slimeknights.tconstruct.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*****
 * Useful commands for development
 * NOTE: NONE of this will work on a multiplayer server, due to the lack of a client-side command handler
 * This could be remedied by intercepting chat input, checking if a message starts with '/', and parsing it independently.
 * The caveat is that the command would then not show up in the tab completion list or help command output,
 * wouldn't have an output for '/help CommandName', and wouldn't have tab completion for subcommands and accepted values.
 *****/

@SuppressWarnings({"CodeBlock2Expr", "UnnecessaryInterfaceModifier", "RedundantSuppression"})
public class CommandDev implements ICommandBase {
  //TODO: Implement datapack subcommand(s) to
  //      List Tags in a datapack/namespace
  //      Find which namespace(s) contain a particular Tag name
  //      ??Localized feedback strings??
  private static final Logger logger = LogManager.getLogger(CommandDev.class);

  private static final SuggestionProvider<CommandSource> SUGGEST_ANY_PACK = (srcContext, suggestionBuilder) -> {
    return ISuggestionProvider.suggest(
      packNames(getResourcepacks(srcContext).getAllPacks()), suggestionBuilder
    );
  };
  private static final SuggestionProvider<CommandSource> SUGGEST_AVAILABLE_PACK = (srcContext, suggestionBuilder) -> {
    return ISuggestionProvider.suggest(
      packNames(getResourcepacks(srcContext).getAvailablePacks()), suggestionBuilder
    );
  };
  private static final SuggestionProvider<CommandSource> SUGGEST_ENABLED_PACK = (srcContext, suggestionBuilder) -> {
    return ISuggestionProvider.suggest(
      packNames(getResourcepacks(srcContext).getEnabledPacks()), suggestionBuilder
    );
  };

  private static ResourcePackList<ResourcePackInfo> getResourcepacks(CommandContext<CommandSource> ctx) {
    return ctx.getSource().getServer().getResourcePacks();
  }

  private static Stream<String> packNames(Collection<ResourcePackInfo> packs) {
    return packs.stream().map(ResourcePackInfo::getName).map(StringArgumentType::escapeIfRequired);
  }

  @Nonnull
  private static <T> Collection<String> tryFindThingWithTag(Collection<Tag<T>> matchingTags) {
    // If no tags matched, return an empty list
    if (matchingTags.isEmpty()) {
      return Collections.emptyList();
    }
    else {
      //Return a comma-separated list of all things tagged with whatever TagName
      return matchingTags.stream()
        .flatMap(itemTag -> itemTag.getAllElements().stream())
        .map(T::toString)
        .collect(Collectors.toList());
// I prefer how this looks, but I dont really want to count the number of commas to determine the number of items found
//			                  .collect(Collectors.joining(", "));
    }
  }

  private static <T extends Tag<?>> T getTagFromResource(
    Function<ResourceLocation, T> creator, ResourceLocation tag
  ) {
    return creator.apply(tag);
  }

  /**
   * Registers the command, and calls
   * {@link #registerAliasesStatic(CommandDispatcher, LiteralCommandNode, List) registerAliases}
   * to add alternative command names.
   *
   * @param dispatcher The command dispatcher
   */
  public static void register(CommandDispatcher<CommandSource> dispatcher) {
    //Permission levels are confusing >.<
    // OpPermissions=4;
    // According to DefaultPermissionLevel, ALL=0, OP=1, NONE=2
    // But the CommandGenerate checks for a permission level of 4, and DebugCommand looks for 3,
    //      so who the hell knows?
    // According to ServerProperties, DedicatedServers set Op permission level at 4,
    //      and something called function-permission-level at 2
    //      While IntegratedServer says that Op permission level is 2, and lacks a function-permission-level
    // Based on the fact that DeOpCommand and KickCommand require a permission level of 3 though, it seems safe to assume
    //      that 3 or higher is at least Operator level
    // That seems somewhat reasonable, until you learn from PlayerEntity that any creative mode player with
    //		permission level >= 2 can use command blocks, which you certainly dont want non-Ops using.
    // And from the fact that commands like GameRuleCommand, which definitely requires elevated permissions,
    //      only looks for a permission level of 2, it sure as hell doesn't look like 2=NONE
    // At the same time, if 2=NONE, then it would make sense for something like SayCommand to set its permission
    //      requirement at 2

    //TODO ADD SHORTCUT COMMAND
    LiteralCommandNode<CommandSource> commandLiteral = dispatcher.register(
      Commands.literal("zhdevhelp")
//			        .requires(commandSource -> commandSource.hasPermissionLevel(2))
        .then(T1CommandItemTags.tier1ItemTagsCommand)
        .then(T1CommandBlockTags.tier1BlockTagsCommand)
        .then(T1CommandSetupTestWorld.tier1SetupTestWorld)
        .executes((command) -> {
          respond(command.getSource(), "No operation provided.");
          return -1;
        })
    );
    //FIXME: completion doesnt work for the /zhd alias
    ICommandBase.registerAliasesStatic(
      dispatcher, commandLiteral,
      Collections.singletonList("zhd")
    );
  }

  private static <T extends TextComponent> void respond(CommandSource source, T component) {
    source.sendFeedback(component, true);
  }

  private static <T extends TextComponent> void respond(PlayerEntity player, T component) {
    player.sendMessage(component);
  }

  private static void respond(CommandSource source, String component) {
    respond(source, new StringTextComponent(component));
  }

  private static void respond(PlayerEntity player, String component) {
    respond(player, new StringTextComponent(component));
  }

  private static class T1CommandBlockTags {
    private static final ArgumentBuilder<CommandSource, ?> listKnownBlockTags = listKnownBlockTags();
    private static final SuggestionProvider<CommandSource> SUGGEST_BLOCK_TAGS = (srcContext, suggestionBuilder) -> {
      return ISuggestionProvider.suggest(
        BlockTags.getCollection().getTagMap().values().stream().map(tag -> '"' + tag.getId().toString() + '"'),
        suggestionBuilder
      );
    };
    private static final ArgumentBuilder<CommandSource, ?> showBlocksWithTag = showBlocksWithTag();
    public static final ArgumentBuilder<CommandSource, ?> tier1BlockTagsCommand = cmdTier1BlockTags();

    @Nonnull
    private static Collection<String> tryFindWithBlockTag(String blockTagName) {
      // We don't care about tags that don't match
      Collection<Tag<Block>> matchingTags =
        BlockTags.getCollection().getTagMap().values().parallelStream().filter(tag -> {
          // If blockTagName lacks a namespace, assume it is part of the "minecraft" namespace
          return blockTagName.contains(":") ?
            tag.getId().toString().equals(blockTagName) :
            tag.getId().toString().equals("minecraft:" + blockTagName);
        }).collect(Collectors.toList());

      return tryFindThingWithTag(matchingTags);
    }

    private static ArgumentBuilder<CommandSource, ?> showBlocksWithTag() {
      return Commands.literal("withTag")
        .then(argBlockTagName())
        .executes(ctx -> {
          respond(ctx.getSource(), "No BlockTag name provided");
          return -1;
        });
    }

    private static ArgumentBuilder<CommandSource, ?> argBlockTagName() {
      return Commands.argument("BlockTagName", StringArgumentType.string())
        .suggests(SUGGEST_BLOCK_TAGS)
        .executes(ctx -> {
          String argument = StringArgumentType.getString(ctx, "BlockTagName");
          Collection<String> results = tryFindWithBlockTag(argument);
          String reply = results.isEmpty() ?
            "Unable to find Blocks with Tag \"" + argument + "\"" :
            "Found " + results.size() + " Blocks tagged with \"" + argument + "\"  =  " + results;
          respond(ctx.getSource(), reply);
          return results.size();
        });
    }

    private static ArgumentBuilder<CommandSource, ?> listKnownBlockTags() {
      return Commands.literal("listAll").executes(ctx -> {
        StringTextComponent response = new StringTextComponent(
          BlockTags.getCollection().getRegisteredTags().stream()
            .map(ResourceLocation::toString)
            .collect(Collectors.joining(", "))
        );
        respond(ctx.getSource(), response);
        return 0;
      });
    }

    private static ArgumentBuilder<CommandSource, ?> cmdTier1BlockTags() {
      return Commands.literal("blockTags")
        .then(showBlocksWithTag)
        .then(listKnownBlockTags);
    }
  }

  private static class T1CommandItemTags {
    private static final ArgumentBuilder<CommandSource, ?> listKnownItemTags = listKnownItemTags();
    private static final ArgumentBuilder<CommandSource, ?> showTagsOnHeldItem = showTagsOnHeldItem();
    private static final SuggestionProvider<CommandSource> SUGGEST_ITEM_TAGS = (srcContext, suggestionBuilder) -> {
      return ISuggestionProvider.suggest(
        ItemTags.getCollection().getTagMap().values().stream().map(tag -> '"' + tag.getId().toString() + '"'),
        suggestionBuilder
      );
    };
    private static final ArgumentBuilder<CommandSource, ?> showItemsWithTag = showItemsWithTag();
    public static final ArgumentBuilder<CommandSource, ?> tier1ItemTagsCommand = cmdTier1ItemTags();

    @Nonnull
    private static Collection<String> tryFindWithItemTag(String itemTagName) {
      // We don't care about tags that don't match
      Collection<Tag<Item>> matchingTags = ItemTags.getCollection().getTagMap().values().parallelStream().filter(
        tag -> {
          // If itemTagName lacks a namespace, assume it is part of the "minecraft" namespace
          return itemTagName.contains(":") ?
            tag.getId().toString().equals(itemTagName) :
            tag.getId().toString().equals("minecraft:" + itemTagName);
        }).collect(Collectors.toList());
      return tryFindThingWithTag(matchingTags);
    }

    private static ArgumentBuilder<CommandSource, ?> showTagsOnHeldItem() {
      return Commands.literal("onHeld").executes(ctx -> {
        CommandSource source = ctx.getSource();
        PlayerEntity caller = source.asPlayer();    //yes, asPlayer returns a ServerPlayerEntity, but remember that singleplayer is internally a server these days
        Item heldItem = caller.getHeldItemMainhand().getItem();
        Set tags = heldItem.getTags();

        if (tags.isEmpty()) {
          respond(source, heldItem + " has 0 DataTags");
        }
        else {
          respond(source, heldItem + " has " + tags.size() + " DataTags:  " + tags);
        }
        return 0;
      });
    }

    private static ArgumentBuilder<CommandSource, ?> showItemsWithTag() {
      return Commands.literal("withTag")
        .then(argItemTagName())
        .executes(ctx -> {
          respond(ctx.getSource(), "No ItemTag name provided");
          return -1;
        });
    }

    private static ArgumentBuilder<CommandSource, ?> argItemTagName() {
      return Commands.argument("ItemTagName", StringArgumentType.string())
        .suggests(SUGGEST_ITEM_TAGS)
        .executes(ctx -> {
          String argument = StringArgumentType.getString(ctx, "ItemTagName");
          Collection<String> results = tryFindWithItemTag(argument);
          String reply = results.isEmpty() ?
            "Unable to find Items with Tag \"" + argument + "\"" :
            "Found " + results.size() + " Items tagged with \"" + argument + "\"  =  " + results;
          respond(ctx.getSource(), reply);
          return results.size();
        });
    }

    private static ArgumentBuilder<CommandSource, ?> listKnownItemTags() {
      return Commands.literal("listAll").executes(ctx -> {
        StringTextComponent response = new StringTextComponent(
          ItemTags.getCollection().getRegisteredTags().stream()
            .map(ResourceLocation::toString)
            .collect(Collectors.joining(", "))
        );
        respond(ctx.getSource(), response);
        return 0;
      });
    }

    private static ArgumentBuilder<CommandSource, ?> cmdTier1ItemTags() {
      return Commands.literal("itemTags")
        .then(showTagsOnHeldItem)
        .then(showItemsWithTag)
        .then(listKnownItemTags);
    }
  }

  private static class T1CommandSetupTestWorld {
    private static final String[] firstCommandsInTestWorld = {
      "/gamerule doDaylightCycle false", "/gamerule doFireTick false", "/gamerule doLimitedCrafting false",
      "/gamerule doWeatherCycle false", "/gamerule keepInventory true", "/gamerule mobGriefing false"
    };
    public static final ArgumentBuilder<CommandSource, ?> tier1SetupTestWorld = cmdTier1RulesForTestWorld();

    private static ArgumentBuilder<CommandSource, ?> cmdTier1RulesForTestWorld() {
      return Commands.literal("worldRules")
        .requires(src -> src.hasPermissionLevel(2))
        .executes(ctx -> {
          for (String cmd : firstCommandsInTestWorld) {
            Minecraft.getInstance().player.sendChatMessage(cmd);
          }
          return 0;
        });
    }
  }
}
