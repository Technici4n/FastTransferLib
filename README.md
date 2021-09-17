# DEPRECATED, USE THE NEW TECHREBORN ENERGY INSTEAD
[TechReborn Energy](https://github.com/TechReborn/Energy) was reworked recently and now offers an interface similar to FTL's, but a little closer to the Fabric Transfer API and embracing its transaction system.
As such, FTL is deprecated in favor of TR Energy.

# Fast Transfer Lib
A clean, simple and fast energy api for the Fabric ecosystem, with **bidirectional** compatibility with Tech Reborn Energy,
based on the [Fabric API Lookup module](https://github.com/FabricMC/fabric/tree/1.16/fabric-api-lookup-api-v1).
Currently used by Industrial Revolution and Wireless Networks, with support from other mods as well.

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

## License
This library is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
