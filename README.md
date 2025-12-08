# PHYTEC Lumissil SPI Integration

This repository provides the tools and drivers necessary to enable SPI communication between PHYTEC development kits and Lumissil IS31CG5317/IS32CG5327 powerline communication (PLC) devices.

## Table of Contents
1. [Overview](#overview)
2. [Components](#components)
3. [Building Images](#building-images)
4. [Verification](#verification)
5. [Host Services](#host-services)
6. [Device Communication](#device-communication)
7. [Troubleshooting](#troubleshooting)

## Overview

This meta-layer integrates Lumissil's powerline communication devices with PHYTEC's development platforms, enabling ethernet-over-powerline communication through SPI interface. The integration includes kernel drivers, firmware loading services, and management tools.

**Supported Devices:**
- Lumissil IS31CG5317
- Lumissil IS32CG5327

**Supported Platforms:**
- PHYTEC development kits
- TI TISDK platforms

## Components

### Core Components

- **Lumissil lms-eth2spi SPI Driver**: Kernel driver integrated into the BSP that handles SPI communication with the PLC device
- **Host Load Services**: Firmware loading services that automatically configure the device during system startup when in host-load mode
- **Management Tools**: Command-line utilities for device configuration and status monitoring

### Pre-built Images

Two ready-to-use images are available that include all necessary software for device bringup and testing:

**PHYTEC Yocto BSP:**
```bash
bitbake phytec-lumissil-greenphy-image
```

**TISDK Yocto BSP:**
```bash
bitbake tisdk-lumissil-greenphy-image
```

## Building Images

Follow the standard PHYTEC or TI build procedures, then build one of the provided images above. The images will automatically include all required drivers and services.

## Verification

### 1. Check Device Connection

Verify that the PLC device has been detected and the driver loaded successfully:

```bash
dmesg | grep spi
```

**Expected Output:**
```bash
root@phyboard-lyra-am62xx-3:~# dmesg | grep spi
[    1.378411] spi-nor spi0.0: mt35xu512aba (65536 Kbytes)
[    1.384400] 5 fixed-partitions partitions found on MTD device fc40000.spi.0
[    1.391388] Creating 5 MTD partitions on "fc40000.spi.0":
[    1.396785] 0x000000000000-0x000000080000 : "ospi.tiboot3"
[    1.403725] 0x000000080000-0x000000280000 : "ospi.tispl"
[    1.410297] 0x000000280000-0x000000680000 : "ospi.u-boot"
[    1.416887] 0x000000680000-0x0000006c0000 : "ospi.env"
[    1.423172] 0x0000006c0000-0x000000700000 : "ospi.env.backup"
[    4.200768] Initialising lms_eth2spi version 0.0.8
[    5.748718] lms_eth2spi spi1.0: SPI controller min possible speed  : 1525Hz
[    5.756107] lms_eth2spi spi1.0: SPI controller max possible speed   : 50000000Hz
[    5.770392] lms_eth2spi spi1.0: SPI lms driver configured max speed : 1000000Hz
[    5.780238] lms_eth2spi spi1.0: Translation to 'LSB first' happens in software
[    5.801912] lms_eth2spi spi1.0: Status 0 sanity check passed
[    5.808133] lms_eth2spi spi1.0: Using random MAC address: 00:16:e8:b9:c8:2a
[    5.817810] lms_eth2spi spi1.0: Registered net device: seth0
```

**Key Indicators:**
- `lms_eth2spi` driver initialization
- Successful sanity check
- Network device `seth0` registration

### 2. Check Network Interface

Verify the ethernet-over-SPI interface is available:

```bash
ip link show seth0
```

At this point, the hardware is configured and the PLC device is ready for application software communication.

## Host Services

The system includes a systemd services and a systemd network configuration that
handle device initialization:

### Service Status Check

```bash
systemctl networkctl status seth0
systemctl status cg5317-host@0
```

### seth0.network Config

Brings up the `seth0` network interface during system startup.

**Expected Status:**
```bash
root@phyboard-lyra-am62xx-3:~# networkctl status seth0
* 4: seth0
                   Link File: /usr/lib/systemd/network/99-default.link
                Network File: /usr/lib/systemd/network/25-seth0.network
                       State: degraded (configured)
                Online state: online                                               
                        Type: ether
                        Path: platform-20100000.spi-cs-0
                      Driver: lms_eth2spi
            Hardware Address: 00:16:E8:00:00:02
                         MTU: 1500 (min: 46, max: 1500)
                       QDisc: pfifo_fast
IPv6 Address Generation Mode: none
    Number of Queues (Tx/Rx): 1/1
                     Address: 169.254.187.93
           Activation Policy: always-up
         Required For Online: yes
```

### cg5317-host@ Service

Handles firmware loading and device initialization, instantiable, and
configurable via ```/etc/lumissil/cg5317_0.cfg```. Runs continuously to monitor device
status.

**Expected Status:**
```bash
root@phyboard-lyra-am62xx-3:~# systemctl status cg5317-host@0
* cg5317-host@0.service - CG5317_0 Host FW Loading
     Loaded: loaded (/usr/lib/systemd/system/cg5317-host@.service; enabled; preset: disabled)
     Active: active (running) since Thu 2025-12-04 12:15:22 UTC; 50s ago
    Process: 251 ExecStart=/root/lumissil_examples/host_loading_service/host_loading_service ${RMII_FLAG} -f ${FW_PATH} -c ${CFG_PATH} -s ${SPI_IFNAME} -e ${ETH_IFNAME} -g ${CG_RESET_GPIO_CHIP} -o ${CG_RESET_GPIO_OFFSET} (code=exited, status=0/SUCCESS)
   Main PID: 253 (host_loading_se)
      Tasks: 2 (limit: 2086)
     Memory: 988.0K (peak: 1.4M)
        CPU: 252ms
```

**Key Log Messages:**
- `FW LOAD API: Com channel opened`
- `FW LOAD API: Device queried`
- `FW LOAD API: finished sending fw loading commands`
- `FW LOAD API: Query reply received and FW is running`

## Device Communication

### Management Tool

The included management tool allows communication with connected PLC devices.
Cross-compiled binaries are located in `/lumissil_examples/`.

### Device Information Query

To retrieve device information, use the management tool with the device's MAC address:

```bash
./lumissil_examples/management_tool/management_tool -a `cat /sys/class/net/seth0/address` -c "device_info 3"
```

**Successful Response Example:**
```bash
Destination mac address not provided (-d)   -> using broadcast (FF:FF:FF:FF:FF:FF)
device_info:
        cco mode                      = 1 (never)
        host iface                    = 0 (SPI)
        Terminal Equipment Identifier = 0
        MAC address                   = 00:16:E8:00:00:02
        Manufacturer HFID             = Lumissil
        User HFID                     = userHFID
        AVLN HFID                     = AVLNHFID
        Network Membership Key        = 2D7552AB56297611142B534A090D983E
        Network Identifier            = 4969E63564A20F
        security level                = 0 (Simple connect)
        SNID                          = 0x03
        Max Receiver sensitivity      = 43 dB
        PLC Frequency selection       = 0 (50 Hz)

command: 'device_info 3' finished successfully
```

## Troubleshooting

### Common Issues

**Device not detected:**
- Check SPI connections
- Verify device power supply
- Check `dmesg` for SPI errors

**Service failures:**
- Restart services: `systemctl restart cg5317-host@0`
- Check service logs: `journalctl -u cg5317-host@0 -f`

**Communication timeouts:**
- Verify correct MAC address
- Check network interface status: `ip link show seth0`

### Additional Resources

For device-specific documentation and support, contact Lumissil:
https://www.lumissil.com/applications/communication/electric-vehicles-charging/vehicle-charging/is32cg5317

### SDK Information

Binaries were cross-compiled using the [Phytec SDK](https://download.phytec.de/Software/Linux/BSP-Yocto-AM62x/BSP-Yocto-Ampliphy-AM62x-PD24.1.2/sdk/ampliphy/).
