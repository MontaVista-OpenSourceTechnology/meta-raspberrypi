SUMMARY = "Linux kernel firmware files from Raspbian distribution"
DESCRIPTION = "Updated firmware files for RaspberryPi hardware. \
RPi-Distro obtains these directly from Cypress; they are not submitted \
to linux-firmware for general use."
HOMEPAGE = "https://github.com/RPi-Distro/firmware-nonfree"
SECTION = "kernel"

LICENSE = "GPL-2.0-only & binary-redist-Cypress-rpidistro & Synaptics-rpidistro"
LIC_FILES_CHKSUM = "\
    file://debian/copyright;md5=291ee5385b4cf74b10c5fb5a46a7bbc6 \
"
# Where these are no common licenses, set NO_GENERIC_LICENSE so that the
# license files will be copied from the fetched source.
NO_GENERIC_LICENSE[binary-redist-Cypress-rpidistro] = "debian/copyright"
NO_GENERIC_LICENSE[Synaptics-rpidistro] = "debian/copyright"
LICENSE_FLAGS = "synaptics-killswitch"

SRC_URI = "git://github.com/RPi-Distro/firmware-nonfree;branch=bookworm;protocol=https \
    file://0001-Default-43455-firmware-to-standard-variant.patch \
"
SRCREV = "4b356e134e8333d073bd3802d767a825adec3807"
PV = "20230625-2+rpt3"

inherit allarch

do_configure[noexec] = "1"
do_compile[noexec] = "1"

# The minimal firmware doesn't work with Raspberry Pi 5, so default to the
# standard firmware
CYFMAC43455_SDIO_FIRMWARE ??= "minimal"
CYFMAC43455_SDIO_FIRMWARE:raspberrypi5 ??= "standard"

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/brcm ${D}${nonarch_base_libdir}/firmware/cypress

    cp debian/copyright ${D}${nonarch_base_libdir}/firmware/copyright.firmware-nonfree-rpidistro

    for fw in \
            brcmfmac43430-sdio \
            brcmfmac43430b0-sdio \
            brcmfmac43436-sdio \
            brcmfmac43436s-sdio \
            brcmfmac43455-sdio \
            brcmfmac43456-sdio; do
        cp -R --no-dereference --preserve=mode,links -v debian/config/brcm80211/brcm/${fw}.* ${D}${nonarch_base_libdir}/firmware/brcm/
    done

    cp -R --no-dereference --preserve=mode,links -v debian/config/brcm80211/cypress/* ${D}${nonarch_base_libdir}/firmware/cypress/
    ln -s cyfmac43455-sdio-${CYFMAC43455_SDIO_FIRMWARE}.bin ${D}${nonarch_base_libdir}/firmware/cypress/cyfmac43455-sdio.bin

    rm ${D}${nonarch_base_libdir}/firmware/cypress/README.txt
}

PACKAGES = "\
    ${PN}-bcm43430 \
    ${PN}-bcm43436 \
    ${PN}-bcm43436s \
    ${PN}-bcm43439 \
    ${PN}-bcm43455 \
    ${PN}-bcm43456 \
    ${PN}-license \
"

LICENSE:${PN}-bcm43430 = "binary-redist-Cypress-rpidistro"
LICENSE:${PN}-bcm43436 = "Synaptics-rpidistro"
LICENSE:${PN}-bcm43436s = "Synaptics-rpidistro"
LICENSE:${PN}-bcm43439 = "Synaptics-rpidistro"
LICENSE:${PN}-bcm43455 = "binary-redist-Cypress-rpidistro"
LICENSE:${PN}-bcm43456 = "Synaptics-rpidistro"
LICENSE:${PN}-license = "GPL-2.0-only"

FILES:${PN}-bcm43430 = " \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac43430* \
    ${nonarch_base_libdir}/firmware/cypress/cyfmac43430-sdio.bin \
    ${nonarch_base_libdir}/firmware/cypress/cyfmac43430-sdio.clm_blob \
"
FILES:${PN}-bcm43436 = " \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac43436-* \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac43430b0-* \
"
FILES:${PN}-bcm43436s = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43436s*"
FILES:${PN}-bcm43439 = " \
    ${nonarch_base_libdir}/firmware/cypress/43439A0-7.95.49.00.combined \
    ${nonarch_base_libdir}/firmware/cypress/cyfmac43439-sdio* \
"
FILES:${PN}-bcm43455 = " \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac43455* \
    ${nonarch_base_libdir}/firmware/cypress/cyfmac43455-sdio* \
"
FILES:${PN}-bcm43456 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43456*"
FILES:${PN}-license = "${nonarch_base_libdir}/firmware/copyright.firmware-nonfree-rpidistro"

RDEPENDS:${PN}-bcm43430 += "${PN}-license"
RDEPENDS:${PN}-bcm43436 += "${PN}-license"
RDEPENDS:${PN}-bcm43436s += "${PN}-license"
RDEPENDS:${PN}-bcm43439 += "${PN}-license"
RDEPENDS:${PN}-bcm43455 += "${PN}-license"
RDEPENDS:${PN}-bcm43456 += "${PN}-license"

RCONFLICTS:${PN}-bcm43430 = "linux-firmware-raspbian-bcm43430"
RCONFLICTS:${PN}-bcm43436 = "linux-firmware-bcm43436"
RCONFLICTS:${PN}-bcm43436s = "linux-firmware-bcm43436s"
RCONFLICTS:${PN}-bcm43439 = "linux-firmware-bcm43439"
RCONFLICTS:${PN}-bcm43455 = "linux-firmware-bcm43455"
RCONFLICTS:${PN}-bcm43456 = "linux-firmware-bcm43456"

RREPLACES:${PN}-bcm43430 = "linux-firmware-bcm43430"
RREPLACES:${PN}-bcm43436 = "linux-firmware-bcm43436"
RREPLACES:${PN}-bcm43436s = "linux-firmware-bcm43436s"
RREPLACES:${PN}-bcm43439 = "linux-firmware-bcm43439"
RREPLACES:${PN}-bcm43455 = "linux-firmware-bcm43455"
RREPLACES:${PN}-bcm43456 = "linux-firmware-bcm43456"

# Firmware files are generally not run on the CPU, so they can be
# allarch despite being architecture specific
INSANE_SKIP = "arch"
