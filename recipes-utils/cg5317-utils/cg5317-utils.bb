DESCRIPTION = "Layer includes the services to initialize CG5317 in host loading mode"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = " \
	file://cg5317-bringup.service \
	file://cg5317-host.service \
	file://config.bin \
	file://COPYING.MIT \
	file://evse.ini \
	file://examples \
	file://FW.bin \
	file://pev.ini \
	file://spi_evse_config.bin \
	file://spi_ev_config.bin \
"

RDEPENDS:${PN}:append = "libgpiod (<= 2.0.0)"

do_install() {
	# Install systemd services
	install -d ${D}${sysconfdir}/systemd/system/
	install -m 0644 ${WORKDIR}/cg5317-bringup.service ${D}${sysconfdir}/systemd/system
	install -m 0644 ${WORKDIR}/cg5317-host.service ${D}${sysconfdir}/systemd/system

	# Enable services for multi-user target
	install -d ${D}${sysconfdir}/systemd/system/multi-user.target.wants
	ln -sf ../cg5317-bringup.service ${D}${sysconfdir}/systemd/system/multi-user.target.wants/
	ln -sf ../cg5317-host.service ${D}${sysconfdir}/systemd/system/multi-user.target.wants/

	# Install configuration files
	install -m 0644 ${WORKDIR}/evse.ini ${D}${sysconfdir}/
	install -m 0644 ${WORKDIR}/pev.ini ${D}${sysconfdir}/

	# Install examples directory
	install -d ${D}/root/examples
	cp -r ${WORKDIR}/examples/* ${D}/root/examples/
	find ${D}/root/examples -type f -exec chmod +x {} +

	install -d ${D}/usr/lib/firmware
	install -m 0644 ${WORKDIR}/config.bin ${D}/usr/lib/firmware
	install -m 0644 ${WORKDIR}/spi_evse_config.bin ${D}/usr/lib/firmware
	install -m 0644 ${WORKDIR}/spi_ev_config.bin ${D}/usr/lib/firmware
	install -m 0644 ${WORKDIR}/FW.bin ${D}/usr/lib/firmware
}

FILES:${PN} += "${nonarch_base_libdir}/firmware/*"
FILES:${PN} += "/root/examples"
