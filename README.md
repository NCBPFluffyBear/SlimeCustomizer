# SlimeCustomizer
This Slimefun4 addon allows any server owner to add their own custom Slimefun machines!

## How to create a machine
Provided in [machines.yml](https://github.com/NCBPFluffyBear/SlimeCustomizer/blob/master/src/main/resources/machines.yml) is an example of how machines should be added.

#### Step-by-step instructions
##### Installing the plugin:
1. Download SlimeCustomizer via the [Slimefun repo server](https://thebusybiscuit.github.io/builds/NCBPFluffyBear/SlimeCustomizer/master/)
2. Move the downloaded jar (SlimeCustomizer - DEV ... .jar) to your plugins folder, located at `\<YOUR_SERVER_LOCATION>\plugins`
3. Start the server to generate the proper configuration files
4. Stop the server

##### Adding your machine:
1. Open the `machines.yml` file, located at `\<YOUR_SERVER_LOCATION>\plugins\SlimeCustomizer`

Each comment below explains what the line should contain.
```yaml
   EXAMPLE_MACHINE: #The ID of the machine, in all caps with with _ instead of spaces. THIS IS THE ONLY KEY YOU SHOULD EDIT!
     machine-name: "&bExample Machine" #The name of the machine. Color codes accepted.
     machine-lore: "&7This is an example machine &7I make cool things!" #The lore of the machine. Color codes accepted.
     block-type: FURNACE #The vanilla material ID or skull hash that the machine will use.
     progress-bar-item: FLINT_AND_STEEL #The vanilla material ID that the machine's progress bar will use.
     stats:
       energy-consumption: 16 #The amount of electricity in joules the machine consumes per Slimefun tick.
       energy-buffer: 64 #The amount of electricity in joules the machine can store.
     crafting-recipe:
       1:
         type: VANILLA #The type of material. Can be VANILLA, SLIMEFUN, or NONE
         id: IRON_BLOCK #The material ID. Use the respective id for the type of material. Can be left blank if type is NONE.
       2:
         type: NONE #The numbers 1-9 are the crafting recipe slots, read left to right, row by row.
         id: N/A
       3:
         type: VANILLA
         id: IRON_BLOCK
       4:
         type: VANILLA
         id: IRON_BLOCK
       5:
         type: VANILLA
         id: IRON_BLOCK
       6:
         type: SLIMEFUN
         id: SMALL_CAPACITOR
       7:
         type: VANILLA
         id: IRON_BLOCK
       8:
         type: VANILLA
         id: IRON_BLOCK
       9:
         type: VANILLA
         id: IRON_BLOCK
     recipes:
       1:
         speed-in-seconds: 5 #The seconds it takes to craft the recipe. May not seem to align due to modified Slimefun tick speeds.
         input:
           type: VANILLA #The type of material. Can be VANILLA or SLIMEFUN
           id: IRON_INGOT #The material ID. Use the respective id for the type of material.
           amount: 64 #The amount of the material. Must be within stack size limits.
         output:
           type: SLIMEFUN #Output item, follows save formats as the input
           id: SYNTHETIC_DIAMOND
           amount: 64
       2:
         speed-in-seconds: 5 #If you need to add more recipes, simply increment the recipe number.
         input:
           type: VANILLA
           id: IRON_BLOCK
           amount: 12
         output:
           type: SLIMEFUN
           id: SYNTHETIC_DIAMOND
           amount: 1
```

##### Adding your generator:
1. Open the `generators.yml` file, located at `\<YOUR_SERVER_LOCATION>\plugins\SlimeCustomizer`

Each comment below explains what the line should contain.
```yaml
   EXAMPLE_GENERATOR: #The ID of the generator, in all caps with with _ instead of spaces. THIS IS THE ONLY KEY YOU SHOULD EDIT!
      generator-name: "&bExample Generator" #The name of the machine. Color codes accepted.
      generator-lore: "&7This is an example generator!" #The lore of the machine. Color codes accepted.
      block-type: SKULLe707c7f6c3a056a377d4120028405fdd09acfcd5ae804bfde0f653be866afe39 #The vanilla material ID or skull hash that the generator will use. 
      progress-bar-item: FLINT_AND_STEEL #The vanilla material ID that the machine's progress bar will use.
      stats:
        energy-production: 16 #The amount of electricity in joules the generator produces per Slimefun tick.
        energy-buffer: 64 #The amount of electricity in joules the generator can store.
      crafting-recipe:
        1:
          type: VANILLA #The type of material. Can be VANILLA, SLIMEFUN, or NONE
          id: IRON_BLOCK #The material ID. Use the respective id for the type of material. Can be left blank if type is NONE.
        2:
          type: NONE #The numbers 1-9 are the crafting recipe slots, read left to right, row by row.
          id: N/A
        3:
          type: VANILLA
          id: IRON_BLOCK
        4:
          type: VANILLA
          id: IRON_BLOCK
        5:
          type: VANILLA
          id: IRON_BLOCK
        6:
          type: SLIMEFUN
          id: COAL_GENERATOR
        7:
          type: VANILLA
          id: IRON_BLOCK
        8:
          type: VANILLA
          id: IRON_BLOCK
        9:
          type: VANILLA
          id: IRON_BLOCK
     recipes:
       1:
         time-in-seconds: 5 #The time the fuel will last. This number may not match in game depending on custom tick delays.
         input:
           type: VANILLA
           id: COAL
           amount: 9
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

Want to use a skull texture instead of a block? Replace `block-type` with `HEAD<hash>`. Example provided in the generators config.
How to create a skull hash: https://bukkit.org/threads/create-your-own-custom-head-texture.424286/

Have any questions? Join the Slimefun discord at https://discord.gg/slimefun/