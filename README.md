# Spawning Overhaul

A Minecraft mod that creates a more immersive mob spawning system based on environmental factors like caves, forests, and structures.

## Features

### Environment-Based Spawning

Instead of mobs spawning uniformly everywhere, this mod makes spawning rates dependent on the spawn location's environment:

- **Base Multiplier**: Configure a global spawn rate that applies everywhere (default: 0.2x)
- **Cave Spawning**: Progressive scaling based on depth
  - Shallow caves (Y=60): Minimum multiplier (default: 1.0x = 20% acceptance)
  - Deep caves (Y=-64): Maximum multiplier (default: 5.0x = 100% acceptance)
  - The deeper you go, the more dangerous it gets!
- **Forest Spawning**: Progressive scaling based on tree density
  - Sparse areas: Minimum multiplier (default: 1.0x)
  - Dense forests/jungles: Maximum multiplier (default: 3.0x)
  - More trees = more danger
- **Structure Spawning**: Dangerous structures have increased spawn rates (default: 3.0x)
  - Strongholds, Nether Fortresses, Ocean Monuments, Woodland Mansions, Mineshafts, Dungeons

**Note:** Spawn multipliers use normalized probability acceptance based on the highest configured multiplier. With default settings (maxCaveMultiplier = 5.0), the reference is 5.0x:
- 0.2x base = 4% acceptance (0.2/5.0)
- 1.0x shallow caves = 20% acceptance (1.0/5.0)
- 5.0x deep caves = 100% acceptance (5.0/5.0)

This creates dramatic contrast: deep caves have 25x more spawns than the surface!

### Mob-Specific Rules

Configure special spawn restrictions for specific mobs:

- **Spider Only In Cave** (Enabled by default)
  - When enabled: Spiders and cave spiders only spawn in caves
  - When disabled: Spiders spawn normally everywhere

- **Disable Creeper Near Structure** (Disabled by default)
  - When enabled: Prevents creepers from spawning in dangerous structures (protects against griefing)
  - When disabled: Creepers spawn normally in structures

### In-Game Debug Command

Use `/spawndebug` to see what spawn rates would apply at your current position:
- Environment detection at your position (cave, forest density, structure)
- Active spawn multipliers and their ranges
- Which multiplier would be used (base, cave, or forest)
- Final calculated spawn rate if a mob were to spawn here

This shows you what the spawn system "sees" at your position.

Requires operator level 2 (use `/op <username>`)

## Installation

### Requirements

- **Minecraft 1.21.1**
- **Fabric Loader 0.16.9+** OR **NeoForge 21.1.217+**
- **YACL (Yet Another Config Lib) v3.8.0+** (required dependency)

### Steps

1. Download the appropriate version for your mod loader:
   - `spawningoverhaul-fabric-0.1.0.jar` for Fabric
   - `spawningoverhaul-neoforge-0.1.0.jar` for NeoForge
2. Download and install [YACL](https://modrinth.com/mod/yacl) if you haven't already
3. Place both JAR files in your `mods` folder
4. Launch Minecraft

## Configuration

The mod creates a config file at `config/spawningoverhaul.json5`

### Accessing the Config GUI

**Fabric:**
- Install [Mod Menu](https://modrinth.com/mod/modmenu)
- Click "Mods" in the main menu
- Find "Spawning Overhaul" and click the config button

**NeoForge:**
- Click "Mods" in the main menu
- Find "Spawning Overhaul" and click "Config"

### Configuration Options

#### Environmental Multipliers

| Option | Default | Description |
|--------|---------|-------------|
| Enable Immersive Spawning | ✅ Enabled | Master toggle for the entire system |
| Base Multiplier | 1.0x | Global spawn rate applied everywhere |
| Min Cave Multiplier | 1.0x | Spawn rate at shallowest cave depth (Y=60) |
| Max Cave Multiplier | 5.0x | Spawn rate at deepest depth (Y=-64) |
| Min Forest Multiplier | 1.0x | Spawn rate in sparse areas |
| Max Forest Multiplier | 3.0x | Spawn rate in dense forests |
| Dangerous Structure Multiplier | 3.0x | Additional multiplier in dangerous structures |
| Enable Structure Modifications | ✅ Enabled | Apply structure-based spawn changes |

#### Mob-Specific Rules

| Option | Default | Description |
|--------|---------|-------------|
| Spider Only In Cave | ✅ Enabled | Restricts spiders to caves only |
| Disable Creeper Near Structure | ❌ Disabled | Prevents creepers in structures (anti-griefing) |

#### Detection Settings

| Option | Default | Description |
|--------|---------|-------------|
| Dense Forest Log Threshold | 50 | Number of logs needed to count as dense forest |
| Dense Forest Scan Radius | 10 | Radius in blocks to scan for logs |

#### Mod Compatibility

| Option | Default | Description |
|--------|---------|-------------|
| Additional Dangerous Structures | `[]` | List of modded structure IDs to treat as dangerous |
| Additional Safe Structures | `[]` | List of modded structure IDs to treat as safe |

### Example: Custom Configuration

To reduce overall spawning and make caves extremely dangerous:

```json5
{
  "baseMultiplier": 0.3,           // 70% reduction everywhere
  "minCaveMultiplier": 2.0,        // Caves start at 2x
  "maxCaveMultiplier": 10.0,       // Deep caves reach 10x
  "maxForestMultiplier": 1.5,      // Forests slightly increased
  "spiderOnlyInCave": true,        // Keep spiders in caves
  "disableCreeperNearStructure": true  // Protect structures from creepers
}
```

## How It Works

### Spawn Calculation

For each mob spawn attempt, the mod:

1. **Detects the environment** at the spawn location (not the player's location):
   - Is the spawn position in a cave? What's the depth?
   - How dense is the forest at that position? (counts nearby log blocks)
   - Is the spawn position in a dangerous structure?

2. **Calculates multipliers**:
   - Starts with the base multiplier
   - Calculates cave multiplier (if in cave)
   - Calculates forest multiplier (if logs detected)
   - Selects the **highest** of: base, cave, or forest

3. **Applies additional modifiers**:
   - Multiplies by structure multiplier (if in dangerous structure)
   - Applies mob-specific rules (spiders, creepers)

4. **Makes the spawn decision**:
   - All multipliers use probabilistic acceptance normalized to the highest configured multiplier
   - The reference maximum is dynamically calculated from config (max of cave/forest/structure multipliers)
   - Higher multipliers = higher acceptance probability = more spawns
   - Examples (with default 5.0x reference from maxCaveMultiplier):
     - 0.2x → 4% acceptance (very few spawns)
     - 1.0x → 20% acceptance (reduced spawns)
     - 3.0x → 60% acceptance (structures, many spawns)
     - 5.0x → 100% acceptance (deep caves, maximum spawns)
   - Multiplier = 0.0: Always denies spawn

### Performance

The mod is designed to be lightweight:
- **Cave detection**: Simple Y-coordinate check + sky visibility
- **Forest detection**: Cylindrical scan limited to ±3 blocks vertically
- **Structure detection**: Cached with 10-second TTL
- **Target impact**: <2ms per spawn check on average

## Compatibility

### ✅ Compatible With

- **Modded mobs**: Automatically works with any mob from any mod
- **Modded structures**: Use config lists to mark modded structures as dangerous/safe
- **Modded biomes**: Works in all biomes (vanilla and modded)
- **Other spawn mods**: Applies after vanilla spawn checks, stacks with other mods

### ⚠️ Known Limitations

- Does not affect **mob spawners** (only natural spawns)
- Does not affect **event spawns** (raids, boss fights, etc.)
- Does not affect **command summons** (`/summon`)
- First spawn in each chunk may be slightly slower due to cache misses

### Adding Modded Structure Support

Edit the config file to add modded structures:

```json5
{
  "additionalDangerousStructures": [
    "twilightforest:lich_tower",
    "twilightforest:dark_tower",
    "alexscaves:underground_cabin"
  ],
  "additionalSafeStructures": [
    "minecolonies:colony"
  ]
}
```

## FAQ

**Q: Why am I seeing fewer mobs on the surface?**
A: By default, the base multiplier is 1.0x everywhere. If you've configured a lower base multiplier (e.g., 0.1x), surface spawns will be significantly reduced. Check your `baseMultiplier` setting.

**Q: Can I disable the mod temporarily without uninstalling?**
A: Yes! Set `enableImmersiveSpawning` to `false` in the config.

**Q: Why are there no spiders outside caves?**
A: The `spiderOnlyInCave` option is enabled by default. Disable it if you want spiders everywhere.

**Q: How do I make structures less dangerous?**
A: Reduce the `dangerousStructureMultiplier` value or set `enableStructureModifications` to false.

**Q: Can I make forests more dangerous than caves?**
A: Yes! Set `maxForestMultiplier` higher than `maxCaveMultiplier`.

**Q: Does this work on servers?**
A: Yes! Install the mod on the server (and optionally on clients for the config GUI).

**Q: How do I know if the mod is working?**
A: Use `/spawndebug` in-game to see real-time spawn calculations at your location.

## Development

### Project Structure

```
spawning-overhaul/
├── common/          # Platform-agnostic code
├── fabric/          # Fabric-specific code
└── neoforge/        # NeoForge-specific code
```

### Building

```bash
./gradlew build
```

Build artifacts will be in:
- `fabric/build/libs/spawningoverhaul-fabric-0.1.0.jar`
- `neoforge/build/libs/spawningoverhaul-neoforge-0.1.0.jar`

### Running Development Clients

```bash
# Fabric
./gradlew :fabric:runClient

# NeoForge
./gradlew :neoforge:runClient
```

## Support & Issues

Found a bug or have a suggestion? Please report it on our issue tracker:
- [GitHub Issues](https://github.com/yourusername/spawning-overhaul/issues)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Credits

- Configuration powered by [YACL](https://modrinth.com/mod/yacl)
- Compatible with Fabric and NeoForge

---
