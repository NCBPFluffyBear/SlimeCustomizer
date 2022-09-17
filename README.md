# SlimeCustomizer
This Slimefun4 addon allows any server owner to add their own custom Slimefun machines!
<p align="center">
  <a href="https://thebusybiscuit.github.io/builds/NCBPFluffyBear/SlimeCustomizer/master/">
    <img src="https://thebusybiscuit.github.io/builds/NCBPFluffyBear/SlimeCustomizer/master/badge.svg" alt="Build Server"/>
  </a>
</p>

### Showcase
*Want to see what SlimeCustomizer can do? Visit `play.royale-mc.com` and take a look at their custom items! All showcase image credits go to Azakaturan.*

![Resources](https://user-images.githubusercontent.com/31554056/110004177-c124dd00-7cdc-11eb-8031-3c1feeec228e.png)\
![Category](https://user-images.githubusercontent.com/31554056/110005314-f2ea7380-7cdd-11eb-9090-36017111cbf1.png)
![Electric Ingot Factory IV](https://user-images.githubusercontent.com/31554056/110005311-f251dd00-7cdd-11eb-905a-55d4a86ff5d0.png)
![Heated Redstone](https://user-images.githubusercontent.com/31554056/110005901-9cca0000-7cde-11eb-846c-2a1bd5b2c900.png)
![Charcoal Kiln](https://user-images.githubusercontent.com/31554056/110005316-f2ea7380-7cdd-11eb-8bd3-d95d50b25be6.png)
![Heated Carbon Press](https://user-images.githubusercontent.com/31554056/110005318-f3830a00-7cdd-11eb-871b-6b9fa733231a.png)

#### Reporting bugs
Please report all bugs in the [issue tracker](https://github.com/NCBPFluffyBear/SlimeCustomizer/issues).

## How to use SlimeCustomizer

##### Installing the plugin
1. Download SlimeCustomizer via the [Slimefun repo server](https://thebusybiscuit.github.io/builds/NCBPFluffyBear/SlimeCustomizer/master/)
2. Move the downloaded jar (SlimeCustomizer - DEV ... .jar) to your plugins folder, located at `\<YOUR_SERVER_LOCATION>\plugins`
3. Start the server to generate the proper configuration files
4. Stop the server

##### Adding your category
1. Open the `categories.yml` file, located at `\<YOUR_SERVER_LOCATION>\plugins\SlimeCustomizer`
The table below explains what each key does.

```yaml
slime_customizer:
  type: normal
  category-name: "&cSlimeCustomizer"
  category-item: REDSTONE_LAMP
nested_group:
  type: nested
  category-name: "&cSlimeCustomizer - Nested"
  category-item: BEDROCK
sub_group:
  type: sub
  category-name: "&cSlimeCustomizer - Sub"
  category-item: DIRT
  parent: nested_group
seasonal_group:
  type: seasonal
  category-name: "&cSlimeCustomizer - Seasonal"
  category-item: DIAMOND
  month: 9
locked_group:
  type: locked
  category-name: "&cSlimeCustomizer - Locked"
  category-item: DIAMOND
  parents:
    - slimefun:basic_machines

```

| Key | Description |
| -------- | -------- |
| slime_customizer | The ID of the category. You can change this key! |
| type | The type of the category. |
| category-name | The name of the category that shows in the Slimefun guide. |
| category-item | The vanilla material ID or skull hash of the item that this category will use in the Slimefun guide. |
| tier | A number that indicates the position of this category in Slimefun Guide. Default: 3 |


The types of category:
- `normal`: the category type that all current categories belong to. Can contain items. **This is the default value.**
- `nested`: a nested category can contain several sub categories, CANNOT contain items.
- `sub`: a sub category is belong to a nested category, can contain items. **Required fields:**
  - `parent`: the id of a nested category (from SlimeCustomizer)
- `seasonal`: a seasonal category will only appear in Slimefun Guide in a specified month. **Required fields:**
  - `month`: the numerical month. 1 = Jan, 2 = Feb, and so on...
- `locked`: a locked category cannot be opened until all parent categories are fully unlocked. **Required fields:**
  - `parents`: the list of NamespacedKeys of parent categories


##### Adding your item
1. Open the `items.yml` file, located at `\<YOUR_SERVER_LOCATION>\plugins\SlimeCustomizer`
The table below explains what each key does.


```yaml
EXAMPLE_ITEM:
  category: slime_customizer
  item-type: CUSTOM
  item-name: "&bExample Item"
  item-lore:
  - "&7This is an example item!"
  - "&cSlimeCustomizer now supports multiline lore!"
  item-id: STICK
  item-amount: 1
  placeable: false
  crafting-recipe-type: ENHANCED_CRAFTING_TABLE
  crafting-recipe:
    1:
      type: VANILLA
      id: STICK
      amount: 1
    2:
      type: NONE
      id: N/A
      amount: 1
    3:
      type: NONE
      id: N/A
      amount: 1
    4:
      type: VANILLA
      id: STICK
      amount: 1
    5:
      type: NONE
      id: N/A
      amount: 1
    6:
      type: NONE
      id: N/A
      amount: 1
    7:
      type: NONE
      id: N/A
      amount: 1
    8:
      type: NONE
      id: N/A
      amount: 1
    9:
      type: NONE
      id: N/A
      amount: 1
```

| Key | Description | Acceptable Inputs |
| --- | ----------- | ----------------- |
| EXAMPLE_ITEM | The ID of the item. You can change this key! |
| category | The key of the category that this item will be under in the Slimefun guide.
| item-type | The type of item that you are registering. | CUSTOM (You define the name, lore, and type), SAVEDITEM (Load key from saveditems folder) |
| item-name | The name of the item. |
| item-lore | The lore of the item. |
| item-id | The vanilla ID or skull hash of the material this item will use. |
| item-amount | The amount of this item crafted at once. |
| placeable | If the item is placeable or not. DO NOT MAKE TOOLS PLACEABLE! |
| crafting-recipe-type | The multiblock machine that this item will be crafted in. | ENHANCED_CRAFTING_TABLE, MAGIC_WORKBENCH, ARMOR_FORGE, COMPRESSOR, PRESSURE_CHAMBER, SMELTERY, ORE_CRUSHER, GRIND_STONE, NONE (Can not be crafted with multiblocks) |
| crafting-recipe.#.type | The type of item. | NONE (Empty spot, all other fields will be ignored), SLIMEFUN, SAVEDITEM |
| crafting-recipe.#.id | The id of the item. |
| crafting-recipe.#.amount | The amount of the item. |

*These items CAN be used in your custom machines/generators as well!*
*All registered CUSTOM items can be referred to with their id under the type SLIMEFUN. If you want to use an item inside items.yml as a crafting component for another item, it the crafting component must be registered beforehand.*

*Slimefun items are tagged with a key in their metadata so that they are recognized by Slimefun. To make sure that your SAVEDITEMs do not have conflicts if they need to be recognized by other plugins, this tag is removed. CUSTOM items still have this tag. Because of this, using `/sf give` on a SAVEDITEM may interfere with other plugins since it will be tagged. To get the untagged version of this item, use `/sc give` instead.*

##### Adding your machine
1. Open the `machines.yml` file, located at `\<YOUR_SERVER_LOCATION>\plugins\SlimeCustomizer`
The table below explains what each key does.

```yaml
EXAMPLE_MACHINE:
  category: slime_customizer
  machine-name: "&bExample Machine"
  machine-lore:
  - "&7This is an example machine!"
  block-type: FURNACE
  progress-bar-item: FLINT_AND_STEEL
  stats:
    energy-consumption: 16
    energy-buffer: 64
  crafting-recipe-type: ENHANCED_CRAFTING_TABLE
  crafting-recipe:
    1:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    2:
      type: NONE
      id: N/A
      amount: 1
    3:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    4:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    5:
      type: SLIMEFUN
      id: SMALL_CAPACITOR
      amount: 1
    6:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    7:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    8:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    9:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
  recipes:
    1:
      speed-in-seconds: 5
      input:
        1:
          type: VANILLA
          id: IRON_INGOT
          amount: 9
        2:
          type: NONE
          id: N/A
          amount: 1
      output:
        1:
          type: VANILLA
          id: IRON_BLOCK
          amount: 1
        2:
          type: NONE
          id: N/A
          amount: 1
    2:
      speed-in-seconds: 5
      input:
        1:
          type: SLIMEFUN
          id: GOLD_24K
          amount: 9
        2:
          type: NONE
          id: N/A
          amount: 1
      output:
        1:
          type: SLIMEFUN
          id: GOLD_24K_BLOCK
          amount: 1
        2:
          type: NONE
          id: N/A
          amount: 1
```
| Key | Description | Acceptable Inputs |
| --- | ----------- | ----------------- |
| EXAMPLE_MACHINE | The ID of the machine. You can change this key! |
| category | The key of the category that this item will be under in the Slimefun guide.
| machine-name | The name of the machine. |
| machine-lore | The lore of the machine. |
| block-type | The vanilla ID or skull hash of the material this item will use. | 
| progress-bar-item | The vanilla ID of the progress bar item. |
| stats.energy-consumption | The amount of energy consumed by this machine per Slimefun tick. |
| stats.energy-buffer | The amount of energy that can be stored in this machine. |
| crafting-recipe-type | The multiblock machine that this item will be crafted in. | ENHANCED_CRAFTING_TABLE, MAGIC_WORKBENCH, ARMOR_FORGE, COMPRESSOR, PRESSURE_CHAMBER, SMELTERY, ORE_CRUSHER, GRIND_STONE, NONE (Can not be crafted with multiblocks) |
| crafting-recipe.#.type | The type of item. | NONE (Empty spot, all other fields will be ignored), VANILLA, SLIMEFUN, SAVEDITEM |
| crafting-recipe.#.id | The id of the item based on the type. |
| crafting-recipe.#.amount | The amount of the item to use in the recipe. Enhanced Crafting Table only accepts 1. |
| recipes.#.speed-in-seconds | The time it takes for the recipe to complete. |
| recipes.#.input/output.#.type | The type of item. | NONE (Empty spot, all other fields will be ignored), VANILLA, SLIMEFUN, SAVEDITEM |
| recipes.#.input/output.#.id | The id of the item based on the type. |
| recipes.#.input/output.#.amount | The amount of items. |

*There can as many recipes as you want, but only be 2 inputs and 2 outputs for each recipe*

##### Adding your generator
1. Open the `generators.yml` file, located at `\<YOUR_SERVER_LOCATION>\plugins\SlimeCustomizer`
The table below explains what each key does.

```yaml
EXAMPLE_GENERATOR:
  category: slime_customizer
  generator-name: "&bExample Generator"
  generator-lore:
  - "&7This is an example generator!"
  block-type: SKULLe707c7f6c3a056a377d4120028405fdd09acfcd5ae804bfde0f653be866afe39
  progress-bar-item: FLINT_AND_STEEL
  stats:
    energy-production: 16
    energy-buffer: 64
  crafting-recipe-type: ENHANCED_CRAFTING_TABLE
  crafting-recipe:
    1:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    2:
      type: NONE
      id: N/A
      amount: 1
    3:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    4:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    5:
      type: SLIMEFUN
      id: COAL_GENERATOR
      amount: 1
    6:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    7:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    8:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    9:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
  recipes:
    1:
      time-in-seconds: 5
      input:
        type: VANILLA
        id: SPRUCE_SIGN
        amount: 1
      output:
        type: NONE
        id: N/A
        amount: N/A
    2:
      time-in-seconds: 10
      input:
        type: VANILLA
        id: BEDROCK
        amount: 1
      output:
        type: VANILLA
        id: BIRCH_PLANKS
        amount: 1
```
| Key | Description | Acceptable Inputs |
| --- | ----------- | ----------------- |
| EXAMPLE_GENERATOR | The ID of the generator. You can change this key! |
| category | The key of the category that this item will be under in the Slimefun guide.
| generator-name | The name of the generator. |
| generator-lore | The lore of the generator. |
| block-type | The vanilla ID or skull hash of the material this item will use. | 
| progress-bar-item | The vanilla ID of the progress bar item. |
| stats.energy-production | The amount of energy produced by this generator per Slimefun tick. |
| stats.energy-buffer | The amount of energy that can be stored in this machine. |
| crafting-recipe-type | The multiblock machine that this item will be crafted in. | ENHANCED_CRAFTING_TABLE, MAGIC_WORKBENCH, ARMOR_FORGE, COMPRESSOR, PRESSURE_CHAMBER, SMELTERY, ORE_CRUSHER, GRIND_STONE, NONE (Can not be crafted with multiblocks) |
| crafting-recipe.#.type | The type of item. | NONE (Empty spot, all other fields will be ignored), VANILLA, SLIMEFUN, SAVEDITEM |
| crafting-recipe.#.id | The id of the item based on the type. |
| crafting-recipe.#.amount | The amount of the item to use in the recipe. Enhanced Crafting Table only accepts 1. |
| recipes.#.time-in-seconds | The time it takes for the recipe to complete. |
| recipes.#.input/output.type | The type of item. | NONE (Empty spot, all other fields will be ignored), VANILLA, SLIMEFUN, SAVEDITEM |
| recipes.#.input/output.id | The id of the item based on the type. |
| recipes.#.input/output.amount | The amount of items. |

##### Adding your solar generator
1. Open the `solar-generators.yml` file, located at `\<YOUR_SERVER_LOCATION>\plugins\SlimeCustomizer`
The table below explains what each key does.

```yaml
EXAMPLE_SOLAR_GENERATOR:
  category: slime_customizer
  generator-name: "&bExample Solar Generator"
  generator-lore:
  - "&7This is an example solar generator!"
  block-type: DAYLIGHT_DETECTOR
  stats:
    energy-production:
      day: 256
      night: 128
  crafting-recipe-type: ENHANCED_CRAFTING_TABLE
  crafting-recipe:
    1:
      type: VANILLA
      id: BEDROCK
      amount: 1
    2:
      type: NONE
      id: N/A
      amount: 1
    3:
      type: VANILLA
      id: BEDROCK
      amount: 1
    4:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    5:
      type: SLIMEFUN
      id: COAL_GENERATOR
      amount: 1
    6:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    7:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    8:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
    9:
      type: VANILLA
      id: IRON_BLOCK
      amount: 1
```
| Key | Description | Acceptable Inputs |
| --- | ----------- | ----------------- |
| EXAMPLE_SOLAR_GENERATOR | The ID of the generator. You can change this key! |
| category | The key of the category that this item will be under in the Slimefun guide.
| generator-name | The name of the generator. |
| generator-lore | The lore of the generator. |
| block-type | The vanilla ID or skull hash of the material this item will use. | 
| stats.energy-production.day | The amount of energy produced by this generator per Slimefun tick during daytime. |
| stats.energy-production.day | The amount of energy produced by this generator per Slimefun tick during nighttime. |
| crafting-recipe-type | The multiblock machine that this item will be crafted in. | ENHANCED_CRAFTING_TABLE, MAGIC_WORKBENCH, ARMOR_FORGE, COMPRESSOR, PRESSURE_CHAMBER, SMELTERY, ORE_CRUSHER, GRIND_STONE, NONE (Can not be crafted with multiblocks) |
| crafting-recipe.#.type | The type of item. | NONE (Empty spot, all other fields will be ignored), VANILLA, SLIMEFUN, SAVEDITEM |
| crafting-recipe.#.id | The id of the item based on the type. |
| crafting-recipe.#.amount | The amount of the item to use in the recipe. Enhanced Crafting Table only accepts 1. |

##### Adding your mob drops
1. Open the `mob-drops.yml` file, located at `\<YOUR_SERVER_LOCATION>\plugins\SlimeCustomizer`
   The table below explains what each key does.

```yaml
#READ THE WIKI BEFORE CREATING AN ITEM! https://github.com/NCBPFluffyBear/SlimeCustomizer/blob/master/README.md
EXAMPLE_DROP:
  category: slime_customizer
  item-type: CUSTOM
  item-name: "&bExample Drop"
  item-lore:
    - "&7This is an example mob-drop!"
    - "&cExample drops are not obtainable"
  item-id: STICK
  item-amount: 1
  mob: GHAST
  chance: 0
  recipe-display-item: GHAST_SPAWN_EGG
```
| Key | Description | Acceptable Inputs |
| --- | ----------- | ----------------- |
| EXAMPLE_DROP | The ID of the mob drop. You can change this key! |
| category | The key of the category that this drop will appear under in the Slimefun guide.
| item-type | The type of item that you are registering. | CUSTOM (You define the name, lore, and type), SAVEDITEM (Load key from saveditems folder) |
| item-name | The name of the item. (Custom item types only) |
| item-lore | The lore of the item. (Custom item types only) |
| item-id | The vanilla ID or skull hash of the material this item will use. |
| item-amount | The amount of this item dropped. |
| mob | The type of mob that drops this item |
| chance | The chance that the specified mob drops the item (0 - 100) |
| recipe-display-item | The item that appears in the Slimefun guide's instructions on how to obtain this drop |

#### Using skull textures
Want to use a skull texture instead of a block? Replace `block-type` with `SKULL<hash>`. Example provided in the generators config.
How to create a skull hash: https://bukkit.org/threads/create-your-own-custom-head-texture.424286/

#### Using custom items
SlimeCustomizer supports custom items! These can be from other plugins or even renamed/relored items!

###### I will be "setting up" an example item along the way.
##### Saving an item
1. Hold the item you want to use in your hand
2. Type `/sc saveitem` (You must have the proper permissions to use this command)

The location of where your item is saved will appear in chat. You can rename this file to anything WITHOUT SPACES. This name will be used in your configs.

###### Example: Hold dirt, type /sc saveitem. Navigate to `\plugins\SlimeCustomizer\saveditems` and rename `0.yml` to `DIRT.yml` 
##### Using your saved item
Your saved item can be used in crafting recipes, machine inputs/outputs, and generator inputs/outputs.
For `type`, use `SAVEDITEM` and for `id`, use the file name.

###### Example:
```yaml
1:
  type: SAVEDITEM
  id: DIRT    
```
#### Important notes
- Shaped multiblock machines (ENHANCED_CRAFTING_TABLE, MAGIC_WORKBENCH, ARMOR_FORGE, PRESSURE_CHAMBER) will only accept recipe inputs with a stack size of 1.
- The speed/time and energy production/consumption that you configure may not line up exactly in game depending in your Slimefun tick delay. The lore is adaptive to show you to correct values according to real time.
- When the `type` for the crafting recipes or machine inputs/outputs is set to NONE, all of the fields below it can be omitted.
- If you are updating SlimeCustomizer and new keys have been added, they may appear in different spots than in the examples provided above. Feel free to move them around.
- Names and lore can be colored using [standard Minecraft chat color codes](https://htmlcolorcodes.com/minecraft-color-codes/). Instead of using `§`, use `&` before the color code. Ex: `&4&lSlimeCustomizer` will display "SlimeCustomizer" in red (`&4`) and bold (`&l`).
- Hex color codes are also supported. Ex: To make the word "text" appear in the color `#123456`, use `&x&1&2&3&4&5&6text`, where the combination starts with `&x` and each follwing hex value has a `&` in front of it.

## Permissions
| **Permission** | **Description** |
| --------------------- | ---------------------------------------- |
| slimecustomizer.admin | Access to SlimeCustomizer admin commands |

## Commands
| **Command** | **Permission** | **Parameters** | **Description** |
| ----------- | -------------- | -------------- | --------------- |
| saveitem | slimecustomizer.admin | | Saves the item in your hand to a yml file. Read #saving-an-item for more info. |
| give | slimecustomizer.admin | \<player_name\> \<item_id\> \<amount\> | Used to give an item to a player. |
| getsaveditem | slimecustomizer.admin | gui / \<item_id\> \<player_name\> \<amount\> | Used to get/give a saveditem. |

## Compatability with other Slimefun addons
To be compatible with items from other addons, SlimeCustomizer softdepends the following:
  - ChestTerminal
  - ColoredEnderChests
  - DyedBackpacks
  - EcoPower
  - ElectricSpawners
  - ExoticGarden
  - ExtraGear
  - ExtraHeads
  - HotbarPets
  - luckyblocks-sf
  - PrivateStorage
  - SlimefunOreChunks
  - SlimyTreeTaps
  - SoulJars
  - CommandOverride
  - CS-CoreLib
  - EmeraldEnchants2
  - QuickMarket
  - QuickSell
  - RankPrefixPlus
  - LiteXpansion
  - MobCapturer
  - SoundMuffler
  - ExtraTools
  - TranscEndence
  - Liquid
  - SFCalc
  - SlimefunWarfare
  - Slimy-Power-Suits
  - FluffyMachines
  - SlimyRepair
  - InfinityExpansion
  - FoxyMachines
  - GlobalWarming
  - DynaTech
  - GeneticChickengineering
  - HeadLimiter
  - SlimeXpansion
  - Barrels
  - ClayTech
  - FNAmplifications
  - SMG
  - EMC2
  - Simple-Storage
  - AlchimiaVitae
  - SlimeTinker
  - PotionExpansion
  - FlowerPower
  - Galactifun
  - Element-Manipulation
  - CrystamaeHistoria
  - DankTech2
  - Networks
  - VillagerUtil
  - MissileWarfare
  - SensibleToolbox

It is highly unlikely that new addons will be added to this list. If you are making a new addon or own a private addon and wish to it in SlimeCustomizer, add the following to your `plugin.yml`
```yaml
loadbefore:
    - SlimeCustomizer
```

## Changelog
- DEV 1:
    - Release of SlimeCustomizer!
    - Build your own [custom machines](#adding-your-machine) and [custom generators](#adding-your-generator)
- DEV 2:
    - Updated to no longer require CS-CoreLib
- DEV 3:
    - Added multi-line lore support
    - Added support for other multiblocks (If you have a preexisting config, this key will be smashed onto the bottom. Feel free to reorganize!)
    - Added support for machines to have 2 inputs and/or outputs
    - Added [custom items](#adding-your-item)
    - Added [custom categories](#adding-your-category)
    - Added new commands
- DEV 4:
    - Machine bug fix
- DEV 5:
    - Added [solar generators](#adding-your-solar-generator)

Have any questions? Join the Slimefun discord at https://discord.gg/slimefun/
