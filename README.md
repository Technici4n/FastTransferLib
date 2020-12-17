# Fast Transfer Lib
A library for fast item and fluid transfer for the Fabric ecosystem, based on
[fabric-provider-api-v1](https://github.com/FabLabsMC/fabric/tree/api-provider/fabric-provider-api-v1).
Hopefully it will be merged in fabric api, but more testing has to be done before this can happen.

## Goals of the project
* The first goal of the project is testing the `fabric-provider-api-v1` in a production environment for correctness and performance.
* The second goal of the project is trying to design a simple yet very efficient design for an item and fluid transfer api for potential inclusion in Fabric API in the future.
  In particular, that includes experimenting with fixed base 81000 denominators for the fluid api.
* For this api to be a drop-in replacement in the current 1.16 ecosystem, it will be bundled with `Inventory`/`SidedInventory` and LBA interop, but that may be removed in the future if it is not necessary anymore.
  
## Users of the API
* This API will be tested in a real-world scenario as a replacement for LBA in Modern Industrialization.
* Authors of other tech mods have expressed interest in an alternative to LBA as well.

## Installation
```groovy
repositories {
    maven {
        name = "Technici4n"
        url = "https://raw.githubusercontent.com/Technici4n/Technici4n-maven/master/"
        content {
            includeGroup "net.fabricmc.fabric-api" // FTL needs this too
            includeGroup "dev.technici4n"
        }
    }
    maven {
        name = "BuildCraft"
        url = "https://mod-buildcraft.com/maven"
    }
}

dependencies {
    modApi include("dev.technici4n:FastTransferLib:${project.ftl_version}")
}
```
In `gradle.properties`:
```properties
# put latest version here, check the commits!
ftl_version=0.1.4
```

## Usage
### Item API
Item transfer is handled by a single interface: `ItemIo`. It supports reading inventory contents, and optionnaly inserting and extracting items.
`ItemMovement` provides useful functions for moving items between two `ItemIo`'s.

Note that `ItemStack`s are never transferred directly with this API. Instead, immutable count-less `ItemStack`s called `ItemKey`s are used, and the
counts must be passed separately. This prevents unneeded allocation, and can make comparison between stacks a lot faster.

Querying an instance of the API is dead simple:
```java
ItemIo io = ItemApi.SIDED.get(world, blockPos, direction);
if (io != null) {
    // use the view
}
```

Registering your block to use the API is also very simple:
```java
ItemApi.SIDED.registerForBlocks((world, pos, blockState, direction) -> {
    // return an ItemIo for your block, or null if there is none
}, BLOCK_INSTANCE, ANOTHER_BLOCK_INSTANCE); // register as many blocks as you want
```

If your block is a BlockEntity, it is much more efficient to use `registerForBlockEntities` instead. Ideally, you also want to
store the API in a field of your block entity, as there can be a lot of queries per tick.
```java
ItemApi.SIDED.registerForBlockEntities((blockEntity, direction) -> {
    if (blockEntity instanceof YourBlockEntity) {
        // return your ItemIo, ideally a field in the block entity, or null if there is none.
    }
    return null;
}, BLOCK_ENTITY_TYPE_1, BLOCK_ENTITY_TYPE_2);
```

### Fluid API
The fluid transfer API is basically the same as the item api, with the following differences:
* Fluids are identified by a `Fluid` parameter instead of an `ItemKey` parameter. `FluidKey`s may be considered in the future, but they don't seem
  very useful for now.
* The amounts are specified in `long`s instead of `int`s.
* All fluid operations are done in _millidroplets_, 1/81000th of a bucket.

### Unsided APIs
TODO

### Item-provided APIs
TODO

## Why use fabric-provider-api-v1
`fabric-provider-api-v1` is much more flexible than `InventoryProvider` or block entity `instanceof` checks as it allows registering compatibility layers.
It also comes with a caching system to massively improve performance for blocks that need to do queries every tick.

## Item API prior art
### Vanilla Inventory/SidedInventory
The standard Minecraft interfaces for handling items.
They require a lot of boilerplate for the implementor and for the user, but they are very simple to understand.
Despite requiring a lot of helper functions to be usable, they work reasonably well with small and simple inventories.

However, the main issue with `Inventory`/`SidedInventory` is that they leak too many implementation details:
* Inventory contents can be mutated at will by anyone, and the caller is responsible for calling `markDirty` when that happens.
* The caller is responsible for fitting inserted items in the target inventory.
* It is not possible to reliably insert in slots that can hold more than `maxCount` items.
* It is not possible to optimize insertion or extracting for large inventories.
* And so on...

### IItemHandler
The standard Forge capability for handling items. It is clearly an improvement over Vanilla's interfaces, but:
* Although forbidden by the API contract, the caller can still modify stored `ItemStack`s directly.
* Iterating over all pairs of slots is still in the API contract, although workarounds exist.
* There is no way to know if a large inventory has changed without rescanning its entire contents. This is particularly
  annoying for mods such as Applied Energistics 2 which have to rescan the entire network when an item enters or exits it.
  Some way to listen to inventory changes or at least have a way to skip unchanged inventories would be nice.

### LibBlockAttributes (LBA)
LBA is an item and fluid transfer library written for use in BuildCraft. It has a ton of API surface and default implementations.
For example, the main classes for item transfer are `ItemInsertable`, `ItemExtractable`, `FixedItemInvView`, `FixItemInv`, `GroupedItemInvView` and `GroupedItemInv`.
These classes have a lot of default methods, some inherit others, and sometimes you get one by querying another, which can be surprising.
The author feels that it should be possible to remove 80% of the code of LBA while still retaining the most important functionality.

Another concern is that `ItemExtractable`s in particular work with a completely opaque `ItemStack extract(int maxCount, ItemFilter filter)`,
where `ItemFilter` is basically a count-independent `Predicate<ItemStack>`.
One of the issues with that approach is that it's very hard to optimize the extraction, unless the `ItemFilter` implements `ReadableItemFilter` and one of
`ConstantItemFilter`, `ExactItemFilter`, `ExactItemSetFilter`, `ExactItemStackFilter`, `ItemClassFilter`, `AggregateItemFilter`, `ResolvableItemFilter`.
The author feels that this is a lot of complexity for little benefit.

Also, moving a lot of items between an `ItemExtractable` and an `ItemInsertable`, each with `N` slots, is in `O(N^3)` for a naive implementation,
whereas it is only `O(N^2)` with the vanilla interfaces or the Forge capabilities.

Finally, without defensive copies, it is still possible to modify `ItemStack`s directly.

In spite of that, `ItemExtractable` and `ItemInsertable` are great abstractions to work with, and LBA is clearly a big improvement over the vanilla interfaces.

### Fluidity
The author is not very familiar with Fluidity, but a few observations can be made:
* `fabric-provider-api-v1` is derived from the component and device subsystem of Fluidity.
* Fluidity always distinguishes between a resource and its amount. `ItemStack`s are split into a count and an immutable `Article`.
  In fact, an `Article` in Fluidity can be an item with nbt, a fluid, or even something else. Fluidity uses the same functions for handling all
  types of `Article`s, which makes it very generic and powerful. However, it is the author's opinion that this increases the cognitive load for the user.
* Like LBA, Fluidity has a lot of API surface, which can make it hard to understand.
* On top of simulations, Fluidity provides a transaction system, allowing arbitrarily complex transfer operations to be simulated and reverted if necessary.
  This allows some patterns that are simply not possible with APIs that only allow simulating one operation at the time like IItemHandler and LBA.

A lot of other things can probably be said about Fluidity. The author hasn't studied it very closely, but he strongly recommends giving it a try if the
reader is looking for a batteries-included generic resource management library.

## Item API design
### ItemKey
`ItemKey`s solve the problem of `ItemStack` mutation and allocation. Not only do they prevent users from modifying stacks through a read-only view,
but they also ensure that we don't have to worry about which stacks are safe or not safe to pass to insert/extract functions.

### The API
This is the entire item api:
```java
public interface ItemIo { // item inventory
	int getItemSlotCount(); // number of slots
	ItemKey getItemKey(int slot); // ItemKey in slot
	int getItemCount(int slot); // count in slot
	default int getVersion() { /* ... */ } // inventory version, must change if the inventory changes
	// INSERTION FUNCTIONS
	default boolean supportsItemInsertion() { return false; } // false if insert always rejects
	default int insert(ItemKey key, int count, Simulation simulation) { /* does nothing */ } // insert, and return leftover
	// EXTRACTION FUNCTIONS
	default boolean supportsItemExtraction() { return false; } // false if extract always rejects
	default int extract(int slot, ItemKey key, int maxCount, Simulation simulation) { return 0; } // extract
	default int extract(ItemKey key, int maxCount, Simulation simulation) { /* ... */ } // slotless variant, with default impl
}
```

### `ItemIo`
The first three functions of `ItemIo` are as straightforward as one can get, they allow reading the content of an inventory,
but without the same restriction as a vanilla `Inventory` regarding the slots. In particular, there is no max stack size, and
the slots in the inventory need not match physical slots. A barrel would have a single slot, and a large chest would be free
to merge stacks with the same content if it wants. Note also that it is not possible to modify the inventory in any way.

The `getVersion` is an optional function provided by the API. The _version_ of an inventory **must** change when the inventory
changes, but the value may also change even if the inventory itself hasn't changed. The idea is that pipes that maintain a cache
of the contents of an inventory can skip rescanning an inventory whose version hasn't changed. This is very important for AE2,
which must rescan all the Storage Busses every time an item enters or exits the network as it may have been moved to another storage bus.
This feature is trivial to support for most implementations, yet it can make a huge performance difference for large bases.

### Insertion functions
A few important points regarding these two functions:
* It is optional, which means it's trivial to tell if an inventory supports insertion or not. This is very useful for pipe connections.
* It's very easy to implement: just two functions!
* It leaves distribution entirely to the implementor.
* The implementor can easily optimize the insertion if they want to.

### Extraction functions
* Again, it's optional.
* For most simple inventories, the slot-based function is enough.
* Inventories are able to optimize extraction through the slotless function if the `ItemKey` to be extracted is known in advance.

But why have a slot-based function when the slotless function gives more freedom to the implementor? The point is that if you are
iterating a big inventory using `getItemSlotCount` and `getItemKey` to find out which items can be extracted, you know in which
slot the item is already, so you can prevent a lot of work by telling the `ItemExtractable` where to extract from.

### Final words
The API provides a few functions to move items between two `ItemIo`'s because it is a frequent operation, and it
can be a bit tricky to get right. The API also provides (or will provide) implementations for simple inventories that could be used
for chests and similar "simple" containers.

Overall, the author feels that this API hits a sweet spot between flexibility and performance, solving most issues with vanilla `(Sided)Inventory`,
`IItemHandler` and LBA while keeping the API surface minimal.

## Fluid API design
The design of the fluid api is almost identical to that of the item api, with a few differences.

It uses `Fluid` instances instead of `FluidKey` because fluids look sufficient for now.
If for some reason you need `FluidKey`s, please open an issue so it can be discussed. For now, the author hasn't found a convincing use case for it.

Items are discrete: most players don't expect their diamond to be splittable into two "half-diamonds". Players however expect fluids to be transferable
in small amounts. Forge has been using millibuckets for the smallest amount of fluid that can be transferred, and just uses integers to store the number
of millibuckets. Millibuckets have the advantage of being very easy to read for the player, but a bucket (1000 units) can't be divided into bottles, ingots or nuggets.
To solve this, FTL uses droplets (1/81000 of a bucket). Buckets can be divided into bottles, ingots or nuggets with no issues, and the fluid amounts
can still be displayed in a player-friendly fashion as `x + y/81 mb`, or just `x mb` if the amount is a multiple of `81`. FTL includes helpers for displaying
that as unicode.

The fluid API uses `long`s instead of `int`s because `int`s could be too small when transferring large amounts of buckets, although this probably
wouldn't matter for most mods anyway.

## Why not support transactions?
FTL only allows simulating a single operation, which prevents some patterns from existing. A way to solve this would be to introduce transactions,
which would allow simulating and then rolling back arbitrary transfer operations. Unfortunately, this requires every inventory to support transactions,
which is non-trivial to do efficiently for even a big chest, and most existing mods don't have a use for it anyway.

Ultimately, the author believes that forcing transaction support for every inventory is not reasonable, for both FTL and Fabric API.
If this assumption is wrong, it will be possible to deprecate the previous simulation-based API and replace it with transactions in the future,
but that should not stop simulation from being used _now_.

## License
This library is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
