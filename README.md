# Fast Transfer Lib
A library for fast item and fluid transfer for the Fabric ecosystem, based on
[fabric-provider-api-v1](https://github.com/FabLabsMC/fabric/tree/api-provider/fabric-provider-api-v1).
Hopefully it will be merged in fabric api, but more testing has to be done before this can happen.

## Goals of the project
* The first goal of the project is testing the `fabric-provider-api-v1` in a production environment for correctness and performance.
* The second goal of the project is trying to design a simple yet very efficient design for an item and fluid transfer api for potential inclusion in Fabric API in the future.
  In particular, that includes experimenting with per-container denominators for the fluid api.
* For this api to be a drop-in replacement in the current 1.16 ecosystem, it will be bundled with `Inventory`/`SidedInventory` and LBA interop, but that may be removed in the future if it is not necessary anymore.
  
## Users of the API
* This API will be tested in a real-world scenario as a replacement for LBA in Modern Industrialization.
* Authors of other tech mods have expressed interest in an alternative to LBA as well.

## Installation
When the API is a bit more fleshed out, builds will be available on https://github.com/Technici4n/Technici4n-maven.

## Usage
### Item API
Item transfer is handled by three interfaces: `ItemView`, `ItemInsertable` and `ItemExtractable`.
* `ItemView` is a read-only view of an inventory.
* `ItemInsertable` is an `ItemView` that also supports inserting items.
* `ItemExtractable` is an `ItemView` that supports extracting items.
* Note that `ItemMovement` provides useful functions for moving items between an `ItemExtractable` and an `ItemInsertable`.

Note that `ItemStack`s are never transferred directly with this API. Instead, immutable count-less `ItemStack`s called `ItemKey`s are used, and the
counts must be passed separately. This prevents unneeded allocation, and can make comparison between stacks a lot faster.

Querying an instance of the API is dead simple:
```java
ItemView view = ItemApi.SIDED_VIEW.get(world, blockPos, direction);
if (view != null) {
    // use the view
}
if (view instanceof ItemInsertable) {
    // cast to ItemInsertable and use
}
if (view instanceof ItemExtractable) {
    // cast to ItemExtractable and use
}
```

Registering your block to use the API is also very simple:
```java
ItemApi.SIDED_VIEW.registerForBlocks((world, pos, blockState, direction) -> {
    // return an ItemView for your block, or null if there is none
}, BLOCK_INSTANCE, ANOTHER_BLOCK_INSTANCE); // register as many blocks as you want
```

If your block is a BlockEntity, it is much more efficient to use `registerForBlockEntities` instead. Ideally, you also want to
store the API in a field of your block entity, as there can be a lot of queries per tick.
```java
ItemApi.SIDED_VIEW.registerForBlockEntities((blockEntity, direction) -> {
    if (blockEntity instanceof YourBlockEntity) {
        // return your ItemView, ideally a field in the block entity, or null if there is none.
    }
    return null;
}, BLOCK_ENTITY_TYPE_1, BLOCK_ENTITY_TYPE_2);
```

### Fluid API
The fluid transfer API is basically the same as the item api, with the following differences:
* Fluids are identified by a `Fluid` parameter instead of an `ItemKey` parameter. `FluidKey`s may be considered in the future, but they don't seem
  very useful for now.
* The amounts are specified in `long`s instead of `int`s.
* `FluidView#getFluidUnit()` specifies the unit to use for all interactions with a given `FluidView`.

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
TODO explain which of the above problems FTL addresses.

## License
This library is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
