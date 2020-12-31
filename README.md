# Fast Transfer Lib
A fast item, fluid and energy api for the Fabric ecosystem, based on
[fabric-api-lookup-api-v1](https://github.com/FabricMC/fabric/pull/1234).

## Goals of the project
* The major goal of the project is trying to design a simple yet very efficient design for an item and fluid transfer api for potential inclusion in Fabric API in the future.
* For this api to be a drop-in replacement in the current 1.16 ecosystem, it will be bundled with `Inventory`/`SidedInventory` interop, and LBA if that proves to be possible, but that may be removed in the future if it is not necessary anymore.
* Another goal of this api is to replace the slow and annoying-to-use TR energy api by a much faster and better energy api, **not** for inclusion in Fabric API. To ease the transition, FTL comes with a compat layer that allows it to interact transparently with TR energy blocks. 
 
## Users of the API
* This API will be tested in a real-world scenario as a replacement for LBA in Modern Industrialization.
* Authors of other tech mods have expressed interest in an alternative to LBA as well.

## Installation
**FTL IS BEING REWORKED, THE ENERGY API IS STABLE BUT NOT THE REST. Please don't use it for item or fluid transfer for now.**
```groovy
repositories {
    maven {
        name = "Technici4n"
        url = "https://raw.githubusercontent.com/Technici4n/Technici4n-maven/master/"
        content {
            includeGroup "net.fabricmc.fabric-api" // until fabric-api-lookup-api-v1 is merged
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
ftl_version=0.2.1
```

## API overview and usage
TODO

## Why use fabric-api-lookup-api-v1
`fabric-api-lookup-api-v1` is much more flexible than `InventoryProvider` or block entity `instanceof` checks as it allows registering compatibility layers.
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
* It is not possible to optimize insertion or extraction for large inventories.
* And so on...

### IItemHandler
The standard Forge capability for handling items. It is clearly an improvement over Vanilla's interfaces, but:
* Although forbidden by the API contract, the caller can still modify storageView `ItemStack`s directly.
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
FTL is very much inspired by a subset of Fluidity, but Fluidity is by no means simple to understand. As its author Grondag puts it:
> It is much more extensive than the Fabric project would likely want to take on.

FTL is an attempt at reducing Fluidity to the bare minimum, keeping about 80% of the features with 20% of the complexity.
Let us review how FTL differs from Fluidity:
* Fluidity storages can accept any kind of resource (item, fluid, ...) because all of that is abstracted in an `Article`.
  This introduces `ArticleType` to handle serialization of the various resources,
  and forces storages to check the article type.
  It also introduces the need for an equivalent of a `boolean canInsert(ArticleType type)` function.
  
  In practice however, most storages would only accept some kind of resource anyway, so FTL uses a generic `T` parameter instead.
  In FTL, a `Storage<Fluid>` and a `Storage<ItemKey>` would be used for fluids and items respectively,
  but this genericity is solely there to prevent code duplication. Separate access methods must be used for fluid and item storages.
  This removes the need for `Article`, `ArticleType`, `ArticleTypeRegistry`, and a lot of other functions.
  FTL however introduces `ItemKey` for item management, which can be thought of as a specialization of `Article` for items.
* Fluidity has a change notification system for its storages, and with this change notification system comes the need to keep track of virtual slots,
  called handles in fluidity. This introduces a lot of complexity.
  FTL simply has a versioning system, which is obviously not as powerful or efficient as proper change notification,
  but removes the need for anything related to listeners, event streams, registration, unregistration, handle change events, and handles.
* Explicit simulation was removed in FTL, because it should be possible to achieve the same with an optimized transaction implementation.

With these changes, FTL is able to have only 3 core transfer api classes: `Storage<T>`, `StorageView<T>` and `StorageFunction<T>`.
The complexity comes from the base implementations, the `Inventory` compat wrapper, and the `Participant` and `Transaction` system,
which are unavoidable anyway.

Note also that `fabric-api-lookup-api-v1` is derived from the component and device subsystem of Fluidity.

## License
This library is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
