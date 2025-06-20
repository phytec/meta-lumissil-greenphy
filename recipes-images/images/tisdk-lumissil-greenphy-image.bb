include recipes-core/images/tisdk-default-image.bb

SUMMARY = "This image is designed to install all software required for \
	   the Lumissil Green PHY demo."

LICENSE = "MIT"

IMAGE_INSTALL += "\
    cg5317-utils \
    open-plc-utils \
    lms-eth2spi \
    kernel-module-lms-eth2spi \
"
