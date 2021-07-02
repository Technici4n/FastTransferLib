# Fast Transfer Lib
A clean, simple and fast energy api for the Fabric ecosystem, with **bidirectional** compatibility with Tech Reborn Energy.
Currently used by Industrial Revolution and Wireless Networks, with support from other mods as well.
 
FTL is based on the [Fabric API Lookup module](https://github.com/FabricMC/fabric/tree/1.16/fabric-api-lookup-api-v1),
with an experimental extension for item API support, currently [an open Fabric API PR](https://github.com/FabricMC/fabric/pull/1352).

## Installation
```groovy
repositories {
    maven {
        name = "Technici4n"
        url = "https://raw.githubusercontent.com/Technici4n/Technici4n-maven/master/"
        content {
            includeGroup "net.fabricmc.fabric-api" // until PR # is merged
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
ftl_version=/* ... */
```

## Usage
[`EnergyIo`](src/main/java/dev/technici4n/fasttransferlib/api/energy/EnergyIo.java) is the core interface of FTL.
Instances are queried and exposed through the `SIDED` and `ITEM` lookups in [`EnergyApi`](src/main/java/dev/technici4n/fasttransferlib/api/energy/EnergyApi.java).

To register simple energy-containing items with an energy capacity and maximum insertion/extraction rates,
[`SimpleItemEnergyIo#getProvider`](src/main/java/dev/technici4n/fasttransferlib/api/energy/base/SimpleItemEnergyIo.java) can be used.
Simple energy-containing block entities can store a [`SimpleEnergyIo`](src/main/java/dev/technici4n/fasttransferlib/api/energy/base/SimpleEnergyIo.java)
in a field and expose it via the `SIDED` lookup.

[`EnergyMovement`](src/main/java/dev/technici4n/fasttransferlib/api/energy/EnergyMovement.java) provides a helper function to transfer energy between two `EnergyIo`'s.
[`EnergyPreconditions`](src/main/java/dev/technici4n/fasttransferlib/api/energy/EnergyPreconditions.java) provides a few checks to fail-fast in case of bad API usage.

## Experimental Item and Fluid API
Starting from version 0.5, FTL includes the open **experimental** [Fabric Fluid API PR](https://github.com/FabricMC/fabric/pull/1356),
and provides **experimental** item and "fluid item" extensions.
**Features may be added, changed or removed at any time, use at your own risk.**
The objective is to gather production experience with the Fluid API, and with the design of the item and fluid item APIs,
that will also be PR'ed into Fabric.

### Item API
First check out how the Fluid API works. Then you shouldn't be surprised to learn that the Item API revolves around `Storage<ItemVariant>`.
`ItemStorage` is the entrypoint. `InventoryWrappers` provides a way to turn a vanilla `Inventory`, `SidedInventory` or `PlayerInventory` into a `Storage<ItemVariant>`.
`ItemPreconditions` provides a few helper preconditions.

### Item Fluid API
This is the API for items that contain fluids. Check out `ContainerItemContext` and `ItemFluidStorage`.
This one is more experimental than the Item API, as I'm still not satisfied by `ContainerItemContext` and will likely change it at some point.

## License
This library is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
