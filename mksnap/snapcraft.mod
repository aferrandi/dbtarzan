name: dbtarzan
base: core18
version: 'VERSION.0' 
summary: Database browser
description: Explore the tables of a relational database following the relations defined in their foreign keys.
grade: stable
icon: monkey-face-cartoon.png
confinement: strict

apps:
  dbtarzan:
    command: java -jar $SNAP/dbtarzan-assembly-VERSION.jar --configPath=$SNAP_USER_COMMON
    desktop: dbtarzan.desktop
    environment:        
        # If it does not find the fonts configuration it gives an error.
        XDG_DATA_HOME: $SNAP/usr/share
        FONTCONFIG_PATH: "$SNAP/etc/fonts"
        # Standard libraries for Java
        JAVA_HOME: $SNAP/usr/lib/jvm/java-11-openjdk-amd64
        JAVA_TOOL_OPTIONS: "-Duser.home=$SNAP_USER_COMMON -Djdk.gtk.version=2"        
        PATH: $SNAP/usr/lib/jvm/java-11-openjdk-amd64/bin:$SNAP/usr/lib/jvm/java-11-openjdk-amd64/jre/bin:$PATH
        LD_LIBRARY_PATH: $SNAP/usr/lib/$SNAPCRAFT_ARCH_TRIPLET:$LD_LIBRARY_PATH:$SNAP/usr/lib/jvm/java-11-openjdk-amd64/jre/lib/amd64/
    plugs: [desktop, home, x11, wayland, network, network-bind]

parts:
  # one part for the java libraries and dependencies, one for the jar itself
  java:
    plugin: dump
    source: .
    # without libcamberra... it complains that canberra-gtk is not available.
    build-packages: [ca-certificates, ca-certificates-java, openjdk-11-jre]
    stage-packages: [openjdk-11-jre, zlib1g, libcanberra-gtk-module, libcanberra-gtk3-module]
  dbtarzan:
    source: source/
    plugin: dump


