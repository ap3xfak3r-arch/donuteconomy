# DonutEconomy

A Donut SMP style economy plugin for **Paper 1.21.x**, built with `/sell`, `/shop`, and `/auction`,
plus a live sidebar scoreboard showing each player's money and playtime under their username.

## Building

You'll need Java 21 and Maven installed.

```
cd DonutEconomy
mvn clean package
```

The compiled plugin will be at `target/DonutEconomy.jar`. Drop it into your server's `plugins/` folder
and restart (or `/reload`, though a restart is safer).

## Commands

| Command | Description |
|---|---|
| `/balance [player]` | Check your balance, or someone else's |
| `/pay <player> <amount>` | Send money to another player |
| `/sell hand` | Sell the item in your hand (must be a sellable shop item) |
| `/sell all` | Sell every sellable item in your inventory at once |
| `/shop` | Open the buy/sell chest GUI |
| `/shopadmin` | Open the shop **editor** (admin only, see below) |
| `/auction` or `/ah` | Browse the auction house |
| `/auction sell <price>` | List the item in your hand on the auction house |

## Setting up the shop (chest GUI editor)

Run `/shopadmin` (requires the `donuteconomy.admin` permission — OPs have it by default). This opens
a 54-slot editor:

- **Empty slots** show a green "Empty Slot" pane. Hold the item you want to sell in your hand and click
  the slot — it grabs whatever's in your hand (including the stack size) as that slot's item.
- You'll then be asked to type a price in chat: `<buyPrice> <sellPrice>` — e.g. `100 40` means players
  buy that stack for $100 and sell it back for $40. Use `0` for either side to disable buying or selling
  that item. Type `cancel` to back out.
- **Filled slots**: **left-click removes the item** (clears the slot so you can add something else),
  **right-click lets you re-price it** without removing it.

Regular players use `/shop` to see the same layout — left-click an item to buy it, right-click to sell
that item type back (if a sell price is set and they have enough of it in their inventory).

## Auction house

- `/auction sell <price>` lists whatever's in your hand.
- `/auction` (or `/ah`) opens a paginated browser (45 listings per page + nav arrows). Left-click to buy,
  right-click your own listing to cancel and get the item back.
- Listings currently don't expire automatically — that'd be a good next feature to add if you want it.

## Data storage

Everything is stored in flat YAML files inside the plugin's data folder (`plugins/DonutEconomy/`):
`economy.yml` (balances), `playtime.yml`, `shop.yml`, and `auctions.yml`. No database setup needed.

## Notes / things you may want to tweak

- Starting balance is set in `config.yml` (`starting-balance: 500.0`).
- The sidebar scoreboard updates once per second and overwrites the player's current scoreboard — if you
  use another scoreboard plugin, they'll conflict.
- This was written and reviewed by hand but **not compiled/tested in a live server** here (no network
  access in this environment to pull the Paper API and run Maven). Build it locally first and test on a
  dev server before using it on anything live — Minecraft/Paper API details do shift between minor
  versions, so double check against 1.21.x if you hit a compile error.
