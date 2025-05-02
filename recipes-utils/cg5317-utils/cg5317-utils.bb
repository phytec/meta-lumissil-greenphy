DESCRIPTION = "Layer includes the services to initialize CG5317 in host loading mode"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = " \
	file://25-seth0.network \
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
	install -m 0644 ${WORKDIR}/cg5317-host.service ${D}${systemd_system_unitdir}
	install -d ${D}${systemd_unitdir}/network
	install -m 0644 ${WORKDIR}/25-seth0.network  ${D}${systemd_unitdir}/network/

	# Install examples directory
	install -d ${D}${ROOT_HOME}/lumissil_examples
	cp -r ${WORKDIR}/examples/* ${D}${ROOT_HOME}/lumissil_examples/
	find ${D}${ROOT_HOME}/lumissil_examples -type f -exec chmod +x {} +

	# Install firmware files
	install -d ${D}${nonarch_base_libdir}/firmware/lumissil
	install -m 0644 ${WORKDIR}/spi_evse_config.bin ${D}${nonarch_base_libdir}/firmware/lumissil/
	install -m 0644 ${WORKDIR}/spi_ev_config.bin ${D}${nonarch_base_libdir}/firmware/lumissil/
	install -m 0644 ${WORKDIR}/FW.bin ${D}${nonarch_base_libdir}/firmware/lumissil/
}

FILES:${PN} += "${nonarch_base_libdir}/firmware/lumissil/*"
FILES:${PN} += "${ROOT_HOME}/lumissil_examples/**"
FILES:${PN} += "${systemd_unitdir}/**"

SYSTEMD_SERVICE:${PN} = "cg5317-host.service"
