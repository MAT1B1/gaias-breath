# 🌿 Gaia’s Breath

A Fabric mod that makes the **natural world evolve on its own**.

Grass spreads, flowers sprout, trees regenerate, metal weathers, wood ages — Minecraft’s ecology becomes **alive, gradual, and configurable**.

---

## 🧭 Overview

> The world breathes, grows, and heals over time.  
> Without player input, nature **reclaims** barren land and balances overgrowth,  
> creating a living landscape that changes as you explore.

### Main Features
- **Vegetation propagation**: short grass spreads, may become **tall grass**, and **wild flowers** can appear.  
- **Tree renewal**: **saplings** can pop near trees, letting forests naturally expand.  
- **Natural path formation**: player footsteps gradually **wear down grass** into **coarse dirt** and **dirt paths**, which can slowly recover over time.  
- **Charcoal from burned wood**: when wood blocks are **burned by fire**, they can **drop charcoal** instead of disappearing completely.  
- **Fully configurable** via an in-game screen (tick budgets, growth chances, moss spread, path erosion, and more).  
- **Server-friendly**: all updates are **tick-limited** and distributed across chunks to prevent lag spikes.


---

## 🌱 Natural Growth

### Vegetation Propagation
- Grass blocks may receive **short grass** over time.  
- Short grass can convert to **tall grass** based on probability.  
- **Flowers** occasionally generate in suitable spots (low density, natural feel).

### Tree Regeneration
- **Saplings** can appear around established trees (rare, distance-limited).  

---

## ⚙️ Configuration (ModMenu)

Open the **Gaia’s Breath** configuration screen from **ModMenu** to fine-tune every natural mechanic.  
All values are stored in `config/gaiasbreath.json` and can be safely edited in-game or manually.
<details>
<summary>Click to expand</summary>

### 🌾 World Growth
Control how vegetation spreads and evolves each tick.

| Setting | Default | Description |
|----------|----------|-------------|
| **GROWTH_MAX_CHUNK_PER_TICK** | `20` | Maximum number of chunks processed per tick. |
| **GROWTH_BLOCKS_PER_CHUNK** | `100` | Maximum number of block updates per chunk. |
| **SHORT_GRASS_GROWTH_CHANCE** | `0.005` | Chance for grass blocks to spawn **short grass**. |
| **SHORT_TO_TALL_CHANCE** | `0.005` | Chance for short grass to grow into **tall grass**. |
| **FLOWER_SPREAD_CHANCE** | `0.005` | Chance for **flowers** to appear nearby. |
| **SAPLING_SPREAD_CHANCE** | `0.002` | Chance for **saplings** to sprout near trees. |
| **MUSHROOM_SPREAD_CHANCE** | `0.01` | Chance for **mushrooms** to spread underground or in dark areas. |
| **BUSH_SPREAD_CHANCE** | `0.01` | Chance for **berry bushes** or similar foliage to propagate. |
| **RAIN_GROWTH_CHANCE** | `0.25` | Global growth multiplier during **rain** (boosts propagation speed). |

### 🪨 Moss Generation
Regulates how mossy blocks appear and spread on stone.

| Setting | Default | Description |
|----------|----------|-------------|
| **STONE_TO_MOSSY_CHANCE** | `0.02` | Chance for stone to become **mossy**. |
| **MOSSY_SPREAD_CHANCE** | `0.01` | Chance for mossy blocks to spread their texture. |
| **MOSSY_TO_MOSS_CHANCE** | `0.01` | Chance for mossy blocks to fully convert into **moss blocks**. |
| **MOSS_MAX_CHUNK_PER_TICK** | `10` | Maximum chunks processed for moss generation each tick. |
| **MOSS_BLOCKS_PER_CHUNK** | `10` | Maximum moss updates per chunk. |
| **Y_RANGE** | `20` | Vertical search range for nearby stone/moss blocks. |

### 🪵 Path & Erosion System
Tracks player movement and generates **paths** over time.

| Setting | Default | Description |
|----------|----------|-------------|
| **STEP_TO_COARSE** | `30` | Steps needed to turn grass into **coarse dirt**. |
| **STEP_TO_PATH** | `80` | Steps needed to form a **dirt path**. |
| **RECOVERY_RATE** | `1` | Rate at which paths **regrow** into grass per tick. |
| **DECAY_INTERVAL** | `1200` (1 minute) | Time between **path decay checks** (in ticks). |

### 🔥 Miscellaneous

| Setting | Default | Description |
|----------|----------|-------------|
| **CHARCOAL_DROP_CHANCE** | `0.5` | Chance for **burned wood** to drop **charcoal** instead of ash. |

> 🧩 All settings can be changed live through the config screen or by editing the JSON file.  
> **Gaia’s Breath** is designed to remain stable even with extreme or custom values — tweak it freely!
</details>

---

## 🚀 Performance

- **Tick budgeted**: hard caps per tick and per chunk to keep servers smooth.  
- **Distributed work**: processing spreads across the world to avoid spikes.  
- **Compatible with existing worlds**: no world reset required.

---

## 🧩 Compatibility

- **Fabric** (MC 1.21.9).  
- Works in **single-player and servers**.  
- Plays nicely with biome/terrain mods — Gaia’s Breath only performs **light, probabilistic edits** over time.

---

## 🗺️ Design Goals

- Keep the world feeling **alive**, not chaotic.  
- Prioritize **readability**: changes should be noticeable when you return, not jarring the moment you arrive.  
- Offer **control** to players and server owners via clear settings.

---

## Got feedback or want different defaults?

- Email: **matibi.mods@gmail.com**  
