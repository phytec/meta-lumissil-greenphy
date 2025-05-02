DESCRIPTION = "Layer includes the services to initialize CG5317 in host loading mode"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = " \
	file://25-seth0.network \
	file://cg5317-host.service \
	file://config.bin \
	file://COPYING.MIT \
	file://evse.ini \
	file://examples \
	file://FW.bin \
	file://pev.ini \
	file://spi_sta_config.bin \
	file://spi_cco_config.bin \
"

inherit systemd

RDEPENDS:${PN}:append = "libgpiod (<= 2.0.0)"

do_install() {
	# Install systemd services
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${WORKDIR}/cg5317-host.service ${D}${systemd_system_unitdir}
	install -d ${D}${systemd_unitdir}/network
	install -m 0644 ${WORKDIR}/25-seth0.network  ${D}${systemd_unitdir}/network/

	# Install configuration files
	install -m 0644 ${WORKDIR}/evse.ini ${D}${sysconfdir}/
	install -m 0644 ${WORKDIR}/pev.ini ${D}${sysconfdir}/

	# Install examples directory
	install -d ${D}/root/examples
	cp -r ${WORKDIR}/examples/* ${D}/root/examples/
	find ${D}/root/examples -type f -exec chmod +x {} +

	# Install firmware files
	install -d ${D}${nonarch_base_libdir}/firmware
	install -m 0644 ${WORKDIR}/config.bin ${D}${nonarch_base_libdir}/firmware
	install -m 0644 ${WORKDIR}/spi_sta_config.bin ${D}${nonarch_base_libdir}/firmware
	install -m 0644 ${WORKDIR}/spi_cco_config.bin ${D}${nonarch_base_libdir}/firmware
	install -m 0644 ${WORKDIR}/FW.bin ${D}${nonarch_base_libdir}/firmware
}

FILES:${PN} += "${nonarch_base_libdir}/firmware/*"
FILES:${PN} += "/root/examples"
FILES:${PN} += "${systemd_unitdir}/**"

SYSTEMD_SERVICE:${PN} = "cg5317-host.service"
