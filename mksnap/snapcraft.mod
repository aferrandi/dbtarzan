name: dbtarzan
base: core22
version: 'VERSION.0' 
summary: Database browser
description: Explore the tables of a relational database following the relations defined in their foreign keys.
grade: stable
icon: monkey-face-cartoon.png
confinement: strict

apps:
  dbtarzan:
    command: java.sh VERSION
    desktop: dbtarzan.desktop
    extensions: [gnome]
    environment:
        # If it does not find the fonts configuration it gives an error.
        XDG_DATA_HOME: $SNAP/usr/share
        FONTCONFIG_PATH: "$SNAP/etc/fonts"
        # Standard libraries for Java
        JAVA_HOME: $SNAP/usr/lib/jvm/java-21-openjdk-amd64
        JAVA_TOOL_OPTIONS: "-Duser.home=$SNAP_USER_COMMON"
        PATH: $SNAP/usr/lib/jvm/java-17-openjdk-amd64/bin:$SNAP/usr/lib/jvm/java-17-openjdk-amd64/jre/bin:$PATH
        LD_LIBRARY_PATH: $SNAP/usr/lib/$CRAFT_ARCH_TRIPLET_BUILD_FOR:$LD_LIBRARY_PATH:$SNAP/usr/lib/jvm/java-17-openjdk-amd64/jre/lib/amd64/
        LIBGL_DRIVERS_PATH: $SNAP/usr/lib/${CRAFT_ARCH_TRIPLET_BUILD_FOR}/dri
    plugs: [desktop, home, x11, wayland, network, network-bind]

parts:
  # one part for the java libraries and dependencies, one for the jar itself
  dbtarzan:
    plugin: dump
    source: source/
    build-packages: [ca-certificates, ca-certificates-java, openjdk-17-jre]
    stage-packages: [openjdk-17-jre, zlib1g]
    override-prime: |
        snapcraftctl prime
        rm -vf usr/lib/jvm/java-21-openjdk-*/lib/security/blacklisted.certs
