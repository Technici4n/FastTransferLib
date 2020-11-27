# Fast Transfer Lib
A library for fast item and fluid transfer for the Fabric ecosystem, based on
[fabric-provider-api-v1](https://github.com/FabLabsMC/fabric/tree/api-provider/fabric-provider-api-v1).
Hopefully it will be merged in fabric api, but more testing has to be done before this can happen.

## Goals of the project
* The first goal of the project is testing the `fabric-provider-api-v1` in a production environment with the objective of
  testing its correctness and performance.
* The second goal of the project is trying to design a simple yet very efficient design for an item and fluid transfer api for potential inclusion in Fabric API in the future.
  In particular, that includes experimenting with a fixed-denominator base 81000 fluid api.
* For this api to be a drop-in replacement in the current 1.16 ecosystem, it will be bundled with `Inventory`/`SidedInventory` and LBA interop, but that may be removed in the future if it is not necessary anymore.
  
## Users of the API
* This API will be tested in a real-world scenario as a replacement for LBA in Modern Industrialization.
* Authors of other tech mods have expressed interest in an alternative to LBA as well.

## Prior art
### Vanilla Inventory/SidedInventory
The standard Minecraft interfaces for handling items.
They are very inconvenient to use, as shown by the number of alternative item apis. 

### IItemHandler/IFluidHandler
The standard Forge capabilities for handling items and fluids. The notable differences are that FTL:
* has an inventory serial number;
* has a completely opaque `insert` function;
* has an optional opaque `extract` function;
* uses millidroplets (1/81000) instead of millibuckets (1/1000) for its fluid api;
* insert/extract functionality is provided as subinterfaces of `ItemView`/`FluidView`.

### LibBlockAttributes (LBA)
The author is very familiar with LBA, and has designed FTL as an alternative to LBA specifically. Notable differences:
* FTL has a much smaller API surface;
* FTL uses millidroplets for its fluid api, LBA uses fractions;
* FTL has a single `ItemView`/`FluidView` interface that can be cast to `Insertable`/`Extractable` if necessary.

### Fluidity
The author is not familiar with Fluidity, and has no clue how its item and fluid interfaces work.
Note however that `fabric-provider-api-v1` is derived from a subset of Fluidity. Notable differences:
* FTL uses simulations, Fluidity uses transactions;
* FTL uses millidroplets for its fluid api, LBA uses fractions.

### Cursed Fluid API
Very similar fluid api, it might be merged into FTL or not. Interop is trivial anyway.

## Installation
When the API is a bit more fleshed out, builds will be available on https://github.com/Technici4n/Technici4n-maven. 

## Usage
TODO

## License

This library is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
