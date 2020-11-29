# Fast Transfer Lib
A library for fast item and fluid transfer for the Fabric ecosystem, based on
[fabric-provider-api-v1](https://github.com/FabLabsMC/fabric/tree/api-provider/fabric-provider-api-v1).
Hopefully it will be merged in fabric api, but more testing has to be done before this can happen.

## Goals of the project
* The first goal of the project is testing the `fabric-provider-api-v1` in a production environment for correctness and performance.
* The second goal of the project is trying to design a simple yet very efficient design for an item and fluid transfer api for potential inclusion in Fabric API in the future.
  In particular, that includes experimenting with a fixed-denominator base 81000 fluid api.
* For this api to be a drop-in replacement in the current 1.16 ecosystem, it will be bundled with `Inventory`/`SidedInventory` and LBA interop, but that may be removed in the future if it is not necessary anymore.
  
## Users of the API
* This API will be tested in a real-world scenario as a replacement for LBA in Modern Industrialization.
* Authors of other tech mods have expressed interest in an alternative to LBA as well.

## Installation
When the API is a bit more fleshed out, builds will be available on https://github.com/Technici4n/Technici4n-maven.

## Usage
TODO

## Why this API (api-provider)
TODO

## Why this API (Items)
TODO

## Prior art
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
TODO
The author is very familiar with LBA, and has designed FTL as an alternative to LBA specifically. Notable differences:
* FTL has a much smaller API surface;
* FTL uses millidroplets for its fluid api, LBA uses fractions;
* FTL has a single `ItemView`/`FluidView` interface that can be cast to `Insertable`/`Extractable` if necessary.

### Fluidity
TODO
The author is not familiar with Fluidity, and has no clue how its item and fluid interfaces work.
Note however that `fabric-provider-api-v1` is derived from a subset of Fluidity. Notable differences:
* FTL uses simulations, Fluidity uses transactions;
* FTL uses millidroplets for its fluid api, LBA uses fractions.

## License
This library is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
