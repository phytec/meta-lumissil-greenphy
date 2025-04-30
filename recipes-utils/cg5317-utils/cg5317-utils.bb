DESCRIPTION = "Layer includes the services to initialize CG5317 in host loading mode"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = " \
	file://cg5317-bringup.service \
	file://cg5317-host.service \
	file://COPYING.MIT \
	file://examples \
	file://FW.bin \
	file://spi_evse_config.bin \
	file://spi_ev_config.bin \
"

inherit systemd

RDEPENDS:${PN}:append = "libgpiod (<= 2.0.0)"

do_install() {
	# Install systemd services
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${WORKDIR}/cg5317-bringup.service ${D}${systemd_system_unitdir}
	install -m 0644 ${WORKDIR}/cg5317-host.service ${D}${systemd_system_unitdir}

	# Install examples directory
	install -d ${D}/root/examples
	cp -r ${WORKDIR}/examples/* ${D}/root/examples/
	find ${D}/root/examples -type f -exec chmod +x {} +

	# Install firmware files
	install -d ${D}${nonarch_base_libdir}/firmware
	install -m 0644 ${WORKDIR}/spi_evse_config.bin ${D}${nonarch_base_libdir}/firmware
	install -m 0644 ${WORKDIR}/spi_ev_config.bin ${D}${nonarch_base_libdir}/firmware
	install -m 0644 ${WORKDIR}/FW.bin ${D}${nonarch_base_libdir}/firmware
}

FILES:${PN} += "${nonarch_base_libdir}/firmware/*"
FILES:${PN} += "/root/examples"
FILES:${PN} += "${systemd_unitdir}/**"

SYSTEMD_SERVICE:${PN} = "cg5317-host.service cg5317-bringup.service"
