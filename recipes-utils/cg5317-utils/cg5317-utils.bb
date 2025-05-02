DESCRIPTION = "Layer includes the services to initialize CG5317 in host loading mode"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

ROOT_HOME = "/root"

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

# libgpiod_2.x.x deploys libgpiod.so.3
RDEPENDS:${PN}:append = "libgpiod (<= 2.0.0)"

do_install() {
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${WORKDIR}/cg5317-host.service ${D}${systemd_system_unitdir}
	install -d ${D}${systemd_unitdir}/network
	install -m 0644 ${WORKDIR}/25-seth0.network  ${D}${systemd_unitdir}/network/

	install -d ${D}${ROOT_HOME}
	cp -r ${WORKDIR}/examples ${D}${ROOT_HOME}
	find ${D}${ROOT_HOME}/examples -type f -exec chmod +x {} +

	install -d ${D}${libdir}/firmware
	install -m 0644 ${WORKDIR}/spi_evse_config.bin ${D}${libdir}/firmware/
	install -m 0644 ${WORKDIR}/spi_ev_config.bin ${D}${libdir}/firmware/
	install -m 0644 ${WORKDIR}/FW.bin ${D}${libdir}/firmware/
}

FILES:${PN} += "${systemd_unitdir}/**"
FILES:${PN} += "${libdir}/firmware/*"
FILES:${PN} += "${ROOT_HOME}/**"

SYSTEMD_SERVICE:${PN} = "cg5317-host.service"
