# tiles_XXX 地形纹理包逐格说明

适用素材：`core/src/main/assets/environment/tiles_sewers.png`、`tiles_caves.png`、`tiles_caves_crystal.png`、`tiles_caves_gnoll.png`、`tiles_halls.png`。
参考代码：[DungeonTileSheet.java](../core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/tiles/DungeonTileSheet.java)、[DungeonTerrainTilemap.java](../core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/tiles/DungeonTerrainTilemap.java)、[DungeonWallsTilemap.java](../core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/tiles/DungeonWallsTilemap.java)、[RaisedTerrainTilemap.java](../core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/tiles/RaisedTerrainTilemap.java)。

---

## 1. 总体规格

- 每张图 **256×256 px = 16×16 格，每格 16×16 px**（5 张图尺寸完全一致）。
- 游戏内一格也是 16 px（`DungeonTilemap.SIZE = 16`），所以**格子与地块一一对应，不缩放**。
- 帧号（TextureFilm 的 index）= `(col-1) + 16*(row-1)`。`DungeonTileSheet.xy(x, y)` 里的 x/y 就是这张图上的 **1-based 列/行**。

```
xy(1,1) = 0     xy(16,1)  = 15
xy(1,2) = 16    xy(16,2)  = 31
xy(9,8) = 120   xy(1,16)  = 240
```

整张图按功能分成 11 个区块（行号均指图上 1-based 行）：

| 区块 | 起始 | 范围 | 用途 |
|---|---|---|---|
| GROUND | `xy(1,1)` | 行1–行2前半 | 地面 / 楼梯 / 井 / 基座 |
| CHASM | `xy(9,2)` | 8 格 | 深渊及拼接边 |
| WATER | `xy(1,3)` | 整行3，16 格 | 水面拼接边 |
| FLAT_WALLS | `xy(1,4)` | 行4 | 俯视（平面）模式的墙/门/出口 |
| FLAT_OTHER | `xy(1,5)` | 行5 | 俯视模式的物件 |
| RAISED_WALLS | `xy(1,6)` | 行6–行7，32 格 | 立体墙体底部 |
| RAISED_DOORS | `xy(1,8)` | 行8 前 8 格 | 立体门 |
| RAISED_OTHER | `xy(9,8)` | 行8后半+行9，24 格 | 立体物件（雕像/草/路障…） |
| WALLS_INTERNAL | `xy(1,10)` | 行10–行12，48 格 | 墙体内部/顶部 |
| WALLS_OVERHANG | `xy(1,13)` | 行13–行14，32 格 | 墙的向上延伸 + 侧向门 |
| DOOR_OVERHANG | `xy(1,15)` | 行15 前 8 格 | 门/出口的向上延伸 |
| OTHER_OVERHANG | `xy(9,15)` | 行15后半+行16，24 格 | 物件的向上延伸 / 前景草 |

> 全图 5 个区块中都有**故意留空的格子**（0% 不透明），它们不是漏画，是给后续扩展或该区域用不到的素材留的位。

---

## 2. 渲染分层（谁在哪一层画）

`GameScene` 里的加入顺序 = 绘制顺序（后面的盖前面的）：

1. `water`：`SkinnedBlock`，用 `water0/2/4.png` 做流动动画
2. **`DungeonTerrainTilemap`**（地面层）：地面、深渊、水面拼接边、以及所有 `RAISED_*` 立体物件的**本体**
3. `customTiles` / `visualGrid` / `terrainFeatures`（草叶、陷阱、植物，来自 `terrain_features.png`，**不在这张图里**）
4. `entities` / `levelVisuals` / `heaps` / `mobs` / 英雄
5. **`RaisedTerrainTilemap`**：`*_UNDERHANG`，高草的**前景**部分
6. **`DungeonWallsTilemap`**：`WALL_INTERNAL*`、`*_OVERHANG`，墙和物件的**上半部分**
7. `customWalls` / `wallBlocking` / `fog`

### 关键几何约定

- **`RAISED_*`（行6–行9）**画在**物件自己那一格**，在地面层，是立体物件的"下半身 / 正面"。
- **`*_OVERHANG`（行13–行16）**画在**物件正上方那一格**，不透明区域只有**下半格（y=8..15，约 8px）**，让物件朝画面上方"长高"半格。
- 墙的完整视觉 = 自己格的正面 + 上面那格下半格的 `WALL_OVERHANG`；一整片墙中间的格子则铺满 `WALL_INTERNAL` 砖面。
- **`*_UNDERHANG`（行16 第 11–15 格）**画在**草自己那一格**，但在实体层之上 —— 角色站在高草里时草叶会挡住脚。
- **`skipCells`**：boss 层等通过 `DungeonWallsTilemap.skipCells` 屏蔽掉某些格的墙层，`RaisedTerrainTilemap` 也会跟着跳过。

---

## 3. 逐格对照

### 3.1 GROUND（行1–行2前半）

| # | 格 | 常量 | 显示 / 含义 |
|---|---|---|---|
| 0 | (1,1) | `FLOOR` | 空心砖地面，最普通的地板底色 |
| 1 | (2,1) | `FLOOR_DECO` | 带碎石/裂纹的地面（`Terrain.EMPTY_DECO`） |
| 2 | (3,1) | `GRASS` | 草地底色（草叶另外由 `terrain_features.png` 叠加） |
| 3 | (4,1) | `EMBERS` | 焦土/余烬 |
| 4 | (5,1) | `FLOOR_SP` | 特殊地面（雕像/特殊房间的底座花纹） |
| 5 | (6,1) | — | 空 |
| 6 | (7,1) | `FLOOR_ALT_1` | `FLOOR` 的常见变体（50%） |
| 7 | (8,1) | `FLOOR_DECO_ALT` | `FLOOR_DECO` 常见变体 |
| 8 | (9,1) | `GRASS_ALT` | `GRASS` 常见变体 |
| 9 | (10,1) | `EMBERS_ALT` | `EMBERS` 常见变体 |
| 10 | (11,1) | `FLOOR_SP_ALT` | `FLOOR_SP` 常见变体 |
| 11 | (12,1) | — | 空 |
| 12 | (13,1) | `FLOOR_ALT_2` | `FLOOR` 的**稀有**变体（5%） |
| 13–15 | (14,1)–(16,1) | — | 空 |
| 16 | (1,2) | `ENTRANCE` | 下行楼梯（`Terrain.ENTRANCE`） |
| 17 | (2,2) | `EXIT` | 上行楼梯（`Terrain.EXIT`） |
| 18 | (3,2) | `WELL` | 有水的水井 |
| 19 | (4,2) | `EMPTY_WELL` | 干涸的井 |
| 20 | (5,2) | `PEDESTAL` | 基座（boss 层摆物件的台子） |
| 21 | (6,2) | — | 空 |
| 22 | (7,2) | `ENTRANCE_SP` | 特殊入口（`Terrain.ENTRANCE_SP`，任务/特殊楼梯） |
| 23 | (8,2) | — | 空 |

### 3.2 CHASM（行2 第 9–16 格）

深渊本体是**整格不透明**的黑色，边缘变体根据**上方那一格**是什么地形，画出"塌陷断面"。

| # | 格 | 常量 | 显示 / 含义 |
|---|---|---|---|
| 24 | (9,2) | `CHASM` | 深渊本体（默认） |
| 25 | (10,2) | `CHASM_FLOOR` | 上方是普通地面（EMPTY/GRASS/EMBERS/井/雕像/陷阱/书架/路障…）时的断面 |
| 26 | (11,2) | `CHASM_FLOOR_SP` | 上方是特殊地面（`EMPTY_SP`/`STATUE_SP`） |
| 27 | (12,2) | `CHASM_WALL` | 上方是墙/门（`WALL`/`DOOR`/`LOCKED_DOOR`/`WALL_DECO`…） |
| 28 | (13,2) | `CHASM_WATER` | 上方是水（`WATER`） |
| 29–31 | (14,2)–(16,2) | — | 空 |

> `stitchChasmTile()` 里有个特例：上方是 `REGION_DECO_ALT` 时按楼层深度挑变体（≤5 用 `CHASM_FLOOR_SP`，≤10 用 `CHASM`，≤20 用 `CHASM_FLOOR_SP`，否则 `CHASM_FLOOR`）。

### 3.3 WATER（整行3）

**第 1 格是全透明的** —— 开阔水面本身不在这张图上，由 `GameScene` 的 `water` SkinnedBlock 用 `water0/2/4.png` 画动画；`DungeonTerrainTilemap.needsRender()` 也会跳过 `WATER`。

第 2–16 格是**岸线拼接**，位掩码含义是「**该方向是陆地**」：

`idx = WATER + (上?1:0) + (右?2:0) + (下?4:0) + (左?8:0)`

| # | 格 | 掩码 | 陆地在上/右/下/左 |
|---|---|---|---|
| 32 | (1,3) | 0 | 四面都是水（透明，不画） |
| 33 | (2,3) | 1 | ↑ |
| 34 | (3,3) | 2 | → |
| 35 | (4,3) | 3 | ↑→ |
| 36 | (5,3) | 4 | ↓ |
| 37 | (6,3) | 5 | ↑↓ |
| 38 | (7,3) | 6 | →↓ |
| 39 | (8,3) | 7 | ↑→↓ |
| 40 | (9,3) | 8 | ← |
| 41 | (10,3) | 9 | ↑← |
| 42 | (11,3) | 10 | →← |
| 43 | (12,3) | 11 | ↑→← |
| 44 | (13,3) | 12 | ↓← |
| 45 | (14,3) | 13 | ↑↓← |
| 46 | (15,3) | 14 | →↓← |
| 47 | (16,3) | 15 | 四面都是陆地（一格孤岛水） |

能和水拼接的地形白名单见 `DungeonTileSheet.waterStitcheable`（地面、草、井、楼梯、陷阱、门、雕像、炼金锅…；`REGION_DECO_ALT` 只在 21 层以下算）。

### 3.4 FLAT_WALLS / FLAT_OTHER（行4、行5）

这一组是**俯视（flat）模式**专用：`getTileVisual(..., flat=true)` 时走 `directFlatVisuals`，用于小地图、物品栏/弹窗的缩略图（例如 `Chasm.java` 弹窗里用 `new Image(TileTex, 176, 16, 16, 16)` 取帧）。正常 3D 视图里不会出现。

| # | 格 | 常量 | 显示 |
|---|---|---|---|
| 48 | (1,4) | `FLAT_WALL` | 整格实心墙 |
| 49 | (2,4) | `FLAT_WALL_DECO` | 带装饰的墙 |
| 50 | (3,4) | `FLAT_BOOKSHELF` | 书架 |
| 51 | (4,4) | — | 空 |
| 52 | (5,4) | `FLAT_WALL_ALT` | `FLAT_WALL` 变体 |
| 53 | (6,4) | `FLAT_WALL_DECO_ALT` | 变体 |
| 54 | (7,4) | `FLAT_BOOKSHELF_ALT` | 变体 |
| 55 | (8,4) | — | 空 |
| 56 | (9,4) | `FLAT_DOOR` | 关闭的门 |
| 57 | (10,4) | `FLAT_DOOR_OPEN` | 打开的门 |
| 58 | (11,4) | `FLAT_DOOR_LOCKED` | 上锁的门（`HERO_LKD_DR` 也用它） |
| 59 | (12,4) | `FLAT_DOOR_CRYSTAL` | 水晶门 |
| 60 | (13,4) | `UNLOCKED_EXIT` | 已解锁的出口 |
| 61 | (14,4) | `LOCKED_EXIT` | 未解锁的出口 |
| 62–63 | (15,4)–(16,4) | — | 空 |
| 64 | (1,5) | `FLAT_ALCHEMY_POT` | 炼金锅（87% 覆盖，边缘透明） |
| 65 | (2,5) | `FLAT_BARRICADE` | 路障 |
| 66 | (3,5) | `FLAT_HIGH_GRASS` | 高草 |
| 67 | (4,5) | `FLAT_FURROWED_GRASS` | 犁过的草地 |
| 68 | (5,5) | — | 空 |
| 69 | (6,5) | `FLAT_HIGH_GRASS_ALT` | 变体 |
| 70 | (7,5) | `FLAT_FURROWED_ALT` | 变体 |
| 71 | (8,5) | — | 空 |
| 72 | (9,5) | `FLAT_STATUE` | 雕像 |
| 73 | (10,5) | `FLAT_STATUE_SP` | 特殊雕像 |
| 74 | (11,5) | `FLAT_REGION_DECO` | 区域装饰（如洞穴的蛛网石堆） |
| 75 | (12,5) | `FLAT_REGION_DECO_ALT` | 区域装饰变体 |
| 76 | (13,5) | `FLAT_MINE_CRYSTAL` / `FLAT_MINE_BOULDER` | 矿区水晶 / 巨石（**同一格，两个地形共用**） |
| 77 | (14,5) | `..._ALT` | 变体 |
| 78 | (15,5) | `..._ALT_2` | 稀有变体 |
| 79 | (16,5) | — | 空 |

### 3.5 RAISED_WALLS（行6–行7）

**每格都是整格不透明的砖面**。每组 4 格，按 2 个方向位拼接：

`+1` = **右边不是墙**（墙体到此收边）　`+2` = **左边不是墙**

| # | 格 | 常量 | 何时出现 |
|---|---|---|---|
| 80–83 | (1,6)–(4,6) | `RAISED_WALL` | 墙格，且**下方不是墙**（露出墙的正面）；变体 = 右/左/两侧开口 |
| 84–87 | (5,6)–(8,6) | `RAISED_WALL_DECO` | 同上，`WALL_DECO` |
| 88–91 | (9,6)–(12,6) | `RAISED_WALL_DOOR` | 墙格，且**下方是门**（门洞上方那段墙） |
| 92–95 | (13,6)–(16,6) | `RAISED_WALL_BOOKSHELF` | 同上，`BOOKSHELF` |
| 96–99 | (1,7)–(4,7) | `RAISED_WALL_ALT` | `RAISED_WALL` 的常见变体（50%） |
| 100–103 | (5,7)–(8,7) | `RAISED_WALL_DECO_ALT` | 变体 |
| 104–107 | (9,7)–(12,7) | — | 空 |
| 108–111 | (13,7)–(16,7) | `RAISED_WALL_BOOKSHELF_ALT` | 变体 |

### 3.6 RAISED_DOORS（行8 第 1–8 格）

| # | 格 | 常量 | 显示 |
|---|---|---|---|
| 112 | (1,8) | `RAISED_DOOR` | 关着的门（`Terrain.DOOR`） |
| 113 | (2,8) | `RAISED_DOOR_OPEN` | 打开的门（`OPEN_DOOR`） |
| 114 | (3,8) | `RAISED_DOOR_LOCKED` | 上锁的门（`LOCKED_DOOR` / `HERO_LKD_DR`） |
| 115 | (4,8) | `RAISED_DOOR_CRYSTAL` | 水晶门 |
| 116 | (5,8) | `RAISED_DOOR_SIDEWAYS` | 门格，但**下方是墙**（上下方向的门口，画成可通行地面感） |
| 117–119 | (6,8)–(8,8) | — | 空 |

### 3.7 RAISED_OTHER（行8 后半 + 行9）

立体物件的"下半身"，画在自己格上。

| # | 格 | 常量 | 显示 |
|---|---|---|---|
| 120 | (9,8) | `RAISED_ALCHEMY_POT` | 炼金锅（87% 覆盖） |
| 121 | (10,8) | `RAISED_BARRICADE` | 路障（木板） |
| 122 | (11,8) | `RAISED_HIGH_GRASS` | 高草 |
| 123 | (12,8) | `RAISED_FURROWED_GRASS` | 犁过的草地 |
| 124 | (13,8) | — | 空 |
| 125 | (14,8) | `RAISED_HIGH_GRASS_ALT` | 变体 |
| 126 | (15,8) | `RAISED_FURROWED_ALT` | 变体 |
| 127 | (16,8) | — | 空 |
| 128 | (1,9) | `RAISED_STATUE` | 雕像 |
| 129 | (2,9) | `RAISED_STATUE_SP` | 特殊雕像 |
| 130 | (3,9) | `RAISED_REGION_DECO` | 区域装饰 |
| 131 | (4,9) | `RAISED_REGION_DECO_ALT` | 区域装饰变体 |
| 132 | (5,9) | `RAISED_MINE_CRYSTAL` / `RAISED_MINE_BOULDER` | 矿区水晶 / 巨石（共用一格） |
| 133 | (6,9) | `..._ALT` | 变体（50%） |
| 134 | (7,9) | `..._ALT_2` | 稀有变体（5%） |
| 135–143 | (8,9)–(16,9) | — | 空 |

### 3.8 WALLS_INTERNAL（行10–行12）

画在 `DungeonWallsTilemap`（实体层之上），**每格整格不透明**。挑选规则：**自己下方是墙**（`stitchInternalWallTile`）。四方向位：

`+1` 右边不是墙　`+2` 右下不是墙　`+4` 左下不是墙　`+8` 左边不是墙

| # | 格 | 常量 | 显示 |
|---|---|---|---|
| 144–159 | (1,10)–(16,10) | `WALL_INTERNAL` | 墙的**内部/顶部砖面**的 16 种拼接变体 |
| 160–175 | (1,11)–(16,11) | `WALL_INTERNAL_DECO` | 同上，`WALL_DECO` 且**当前在分支 1（矿区）** |
| 176–191 | (1,12)–(16,12) | `WALL_INTERNAL_WOODEN` | 同上，格子是书架或**下方是书架**时用（木质书架的顶） |

> 因为整格铺满，一整片墙在画面上是连续的砖体；只有最下面一行墙用 `RAISED_WALL` 露出正面。

### 3.9 WALLS_OVERHANG（行13–行14）

**只有下半格不透明（约 8px）**，画在物件**正上方那一格**，让墙/门向上"长高"半格。方向位：

`+1` 右下不是墙　`+2` 左下不是墙

| # | 格 | 常量 | 显示 |
|---|---|---|---|
| 192–195 | (1,13)–(4,13) | `WALL_OVERHANG` | 墙顶边缘：自己下方是墙、自己不是墙 |
| 196–199 | (5,13)–(8,13) | `WALL_OVERHANG_DECO` | 下方是 `WALL_DECO`（矿区分支） |
| 200–203 | (9,13)–(12,13) | `WALL_OVERHANG_WOODEN` | 下方是书架 |
| 204–207 | (13,13)–(16,13) | — | 空 |
| 208–211 | (1,14)–(4,14) | `DOOR_SIDEWAYS_OVERHANG` | 下方是**打开的门**时的墙顶 |
| 212–215 | (5,14)–(8,14) | `DOOR_SIDEWAYS_OVERHANG_CLOSED` | 下方是关着的门（整格不透明） |
| 216–219 | (9,14)–(12,14) | `DOOR_SIDEWAIS_OVERHANG_LOCKED` | 下方是上锁的门（整格不透明） |
| 220–223 | (13,14)–(16,14) | `DOOR_SIDEWAYS_OVERHANG_CRYSTAL` | 下方是水晶门（整格不透明） |

### 3.10 DOOR_OVERHANG（行15 第 1–8 格）

门/出口的「上半部分」，画在自己**下方是门**的那一格。这一组多数只有底部一小条不透明。

| # | 格 | 常量 | 覆盖 | 显示 |
|---|---|---|---|---|
| 224 | (1,15) | `DOOR_OVERHANG` | 25%（底部 4px） | 关着的门 / 上锁的门 / 英雄钥匙门的门楣 |
| 225 | (2,15) | `DOOR_OVERHANG_OPEN` | 6%（底部 1px） | 打开的门（几乎只剩门框线） |
| 226 | (3,15) | `DOOR_OVERHANG_CRYSTAL` | 25% | 水晶门门楣 |
| 227 | (4,15) | `DOOR_SIDEWAYS` | 21%（从 y=7 起） | 下方是 `DOOR` 的侧向门 |
| 228 | (5,15) | `DOOR_SIDEWAYS_LOCKED` | 29% | 下方是 `LOCKED_DOOR`/`HERO_LKD_DR` |
| 229 | (6,15) | `DOOR_SIDEWAYS_CRYSTAL` | 29% | 下方是 `CRYSTAL_DOOR` |
| 230 | (7,15) | `EXIT_UNDERHANG` | 13%（底部 1px） | 出口。注释说明：出口视觉是**平铺**的，所以它其实算"下挂" |
| 231 | (8,15) | — | — | 空 |

### 3.11 OTHER_OVERHANG（行15 后半 + 行16）

物件的「上半部分」，画在物件正上方那一格；最后一组是**高草的前景**。

| # | 格 | 常量 | 覆盖 | 显示 |
|---|---|---|---|---|
| 232 | (9,15) | `ALCHEMY_POT_OVERHANG` | 13%（从 y=12） | 炼金锅上半 |
| 233 | (10,15) | `BARRICADE_OVERHANG` | 64%（从 y=5） | 路障上半 |
| 234 | (11,15) | `HIGH_GRASS_OVERHANG` | 38%（从 y=8） | 高草上半（伸进上方格的下半） |
| 235 | (12,15) | `FURROWED_OVERHANG` | 38% | 犁地草上半 |
| 236 | (13,15) | — | — | 空 |
| 237 | (14,15) | `HIGH_GRASS_OVERHANG_ALT` | 34% | 变体 |
| 238 | (15,15) | `FURROWED_OVERHANG_ALT` | 34% | 变体 |
| 239 | (16,15) | — | — | 空 |
| 240 | (1,16) | `STATUE_OVERHANG` | 6%（从 y=13） | 雕像上半（头肩） |
| 241 | (2,16) | `STATUE_SP_OVERHANG` | 6% | 特殊雕像上半 |
| 242 | (3,16) | `REGION_DECO_OVERHANG` | 9%（从 y=13） | 区域装饰上半 |
| 243 | (4,16) | `REGION_DECO_ALT_OVERHANG` | 9% | 变体 |
| 244 | (5,16) | `MINE_CRYSTAL_OVERHANG` / `MINE_BOULDER_OVERHANG` | — | 矿区水晶 / 巨石上半 |
| 245 | (6,16) | `..._ALT` | — | 变体 |
| 246 | (7,16) | `..._ALT_2` | — | 稀有变体 |
| 247–249 | (8,16)–(10,16) | — | — | 空 |
| 250 | (11,16) | `HIGH_GRASS_UNDERHANG` | 41%（y=10..15） | **高草前景**，画在草自己格、实体之上 |
| 251 | (12,16) | `FURROWED_UNDERHANG` | 41% | 犁地草前景 |
| 252 | (13,16) | — | — | 空 |
| 253 | (14,16) | `HIGH_GRASS_UNDERHANG_ALT` | 39% | 变体 |
| 254 | (15,16) | `FURROWED_UNDERHANG_ALT` | 39% | 变体 |
| 255 | (16,16) | — | — | 空 |

---

## 4. 总览 ASCII 图

```
     c1   c2   c3   c4   c5   c6   c7   c8   c9   c10  c11  c12  c13  c14  c15  c16
r1  FLOOR DECO GRASS EMBER FLRSP  --  ALT1 DALT GALT EALT SALT  --  ALT2  --   --   --
r2  ENTR EXIT WELL EWEL PEDES  --  ENTSP  --  CHASM CHF  CHFS CHW  CHWA  --   --   --
r3  WATER +1   +2   +3   +4   +5   +6   +7   +8   +9   +10  +11  +12  +13  +14  +15
r4  FWALL FWDEC FBOOK  --  FWALT FDALT FBALT  --  FDOOR FDOPN FDLOC FDCRY UEXIT LEXIT  --   --
r5  FPOT  FBAR  FHGRS FFURR  --  FHALT FFALT  --  FSTAT FSPSP FRDEC FRDCA FMC   FMC1  FMC2   --
r6  RWALL RW+1  RW+2  RW+3  RWDEC RWD+1 RWD+2 RWD+3 RWDR  RWDR1 RWDR2 RWDR3 RWBS  RWBS1 RWBS2 RWBS3
r7  RWALT RWA+1 RWA+2 RWA+3 RWDAL RWD1  RWD2  RWD3   --   --   --   --  RWBAL RWB1  RWB2  RWB3
r8  RDOR  RDOPN RDLOC RDCRY RDSID  --   --   --  RDPOT RDBAR RDHGR RDFUR  --  RDHAL RDFAL  --
r9  RDSTA RDSPS RRDEC RRDCA RDMIN RDM+1 RDM+2  --   --   --   --   --   --   --   --   --
r10 ----- 16 个 WALL_INTERNAL（+1 右 / +2 右下 / +4 左下 / +8 左）-----
r11 ----- 16 个 WALL_INTERNAL_DECO -------------------------------
r12 ----- 16 个 WALL_INTERNAL_WOODEN -----------------------------
r13 WOVER WO+1  WO+2  WO+3  WODEC WOD+1 WOD+2 WOD+3 WOWOD WOW+1 WOW+2 WOW+3  --   --   --   --
r14 DSOVR DSO+1 DSO+2 DSO+3 DSOCL DSC+1 DSC+2 DSC+3 DSOLK DSL+1 DSL+2 DSL+3 DSOCR DSCR1 DSCR2 DSCR3
r15 DOVHD DOPHD DCRHD DSIDE DSLKD DSCRY EXTUN  --  POTHD BARHD HGRHD FURHD  --  HGAHD FGAHD  --
r16 STTHD SPSHD RDEHD RDAHD MINHD MINH1 MINH2  --   --   --  HGRUN FURUN  --  HGAUN FGAUN  --
```

---

## 5. 变体（alt）规则

`tileVariance[pos]` 是按楼层种子为每格生成的 0–99 随机数，`getVisualWithAlts()` 据此选帧：

| 条件 | 结果 |
|---|---|
| `v >= 95` 且该帧有 rare alt | 用 **rare alt**（5%） |
| `v >= 50` 且该帧有 common alt | 用 **common alt**（45%；若无 rare alt 则 50%） |
| 其他 | 用基础帧 |

- **common alt**：`FLOOR / GRASS / EMBERS / FLOOR_DECO / FLOOR_SP / FLAT_WALL(_DECO) / FLAT_BOOKSHELF / FLAT_HIGH_GRASS / FLAT_FURROWED_GRASS / FLAT_MINE_* / RAISED_WALL(_DECO/_BOOKSHELF) / RAISED_HIGH_GRASS / RAISED_FURROWED_GRASS / RAISED_MINE_* / HIGH_GRASS_OVERHANG / FURROWED_OVERHANG / HIGH_GRASS_UNDERHANG / FURROWED_UNDERHANG / MINE_*_OVERHANG`
- **rare alt**：`FLOOR → FLOOR_ALT_2`、`FLAT_MINE_* → _ALT_2`、`RAISED_MINE_* → _ALT_2`、`MINE_*_OVERHANG → _ALT_2`

> 注意：**墙的拼接方向位是在选 alt 之前/之后叠加的**。`getRaisedWallTile()` 是「先 `getVisualWithAlts()` 再 `+1/+2`」，而 `stitchInternalWallTile()` 是「先定 base 再 `+1/+2/+4/+8`」且**不**走 alt —— 所以 `WALL_INTERNAL` 没有变体。

---

## 6. 五张图的差异

用 `node` 扫了一遍 5 张图每格的 alpha 覆盖率，结构 100% 一致，只有下面几格不同：

| 格 | sewers / halls | caves | caves_crystal | caves_gnoll |
|---|---|---|---|---|
| (13,5)(14,5)(15,5) `FLAT_MINE_*` | 空 | **空** | 有 | 有 |
| (5,9)(6,9)(7,9) `RAISED_MINE_*` | 空 | **空** | 有 | 有 |
| (5,16)(6,16)(7,16) `MINE_*_OVERHANG` | 空 | **空** | 有 | 有 |

其余差异只是美术规格（halls 的雕像更小、`WALL_OVERHANG` 覆盖 45% 而不是 50% 等）。

`tiles_XXX` 的选取只有三处：

- `SewerLevel.tilesTex()` → `TILES_SEWERS`
- `MiningLevel.tilesTex()` → 按 `Blacksmith.Quest.Type()` 取 `TILES_CAVES`（default）/ `TILES_CAVES_CRYSTAL` / `TILES_CAVES_GNOLL`
- `LastLevel.tilesTex()` → `TILES_HALLS`

> ⚠️ 值得留意：`tiles_caves.png` **缺**矿区那 9 格贴图，但 `MiningLevel` 的 default 分支（非 CRYSTAL/GNOLL）仍然返回它，而 `MineEntrance` / `MineGiantRoom` / `MineLargeRoom` 是无条件加进房间表的。真跑到这个组合时 `MINE_CRYSTAL` / `MINE_BOULDER` 会画不出东西（raised 帧是空的；flat 帧 `get()` 落回 0 = `FLOOR`）。目前没验证 default 是否可达。

---

## 7. 不在 `tiles_XXX` 里的东西

别在这张图里找它们：

| 内容 | 实际来源 |
|---|---|
| 流动的水面 | `water0.png` / `water2.png` / `water4.png`（`SkinnedBlock`） |
| 草叶、陷阱、植物 | `terrain_features.png`（`TerrainFeaturesTilemap`），按楼层取 stage |
| 网格辅助线 | `visual_grid.png`（`GridTileMap`） |
| 墙体"不可见判定"辅助图 | `wall_blocking.png`（`WallBlockingTilemap`） |
| 特殊层的自定义地形 | `environment/custom_tiles/*.png`（`CustomTilemap`） |
