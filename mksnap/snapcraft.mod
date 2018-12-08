name: dbtarzan
version: 'VERSION.0' 
summary: Database browser
description: Explore the tables of a relational database following the relations defined in their foreign keys.
grade: stable
icon: monkey-face-cartoon.png
confinement: strict

apps:
  dbtarzan:
    command: java -jar $SNAP/dbtarzan-assembly-VERSION.jar --configPath=$SNAP_USER_COMMON -Djdk.gtk.version=2
    desktop: dbtarzan.desktop
    environment:        
        # If it does not find the fonts configuration it gives an error.
        XDG_DATA_HOME: $SNAP/usr/share
        FONTCONFIG_PATH: $SNAP/etc/fonts/config.d
        FONTCONFIG_FILE: $SNAP/etc/fonts/fonts.conf    
        # Standard libraries for Java
        JAVA_HOME: $SNAP/usr/lib/jvm/java-8-openjdk-amd64
        PATH: $SNAP/usr/lib/jvm/java-8-openjdk-amd64/bin:$SNAP/usr/lib/jvm/java-8-openjdk-amd64/jre/bin:$PATH
        LD_LIBRARY_PATH: $LD_LIBRARY_PATH:$SNAP/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/amd64/
    plugs: [home, unity7, x11, wayland, network]

parts:
  libopenjfxjava:
    plugin: dump 
    source-type: deb
    source: http://no.archive.ubuntu.com/ubuntu/pool/universe/o/openjfx/libopenjfx-java_8u161-b12-1ubuntu2_all.deb

  libopenjfxjni:
    plugin: dump 
    source-type: deb
    source: http://no.archive.ubuntu.com/ubuntu/pool/universe/o/openjfx/libopenjfx-jni_8u161-b12-1ubuntu2_amd64.deb
    stage-packages: [libasound2, libavcodec57, libavformat57, libc6, libcairo2, libfreetype6, libgcc1, libgdk-pixbuf2.0-0, libgl1, libglib2.0-0, libgtk2.0-0, libjpeg8, libpango-1.0-0, libpangoft2-1.0-0, libstdc++6, libx11-6, libxml2, libxslt1.1, libxtst6]
  
  openjfx:
    plugin: dump 
    source-type: deb
    source: http://no.archive.ubuntu.com/ubuntu/pool/universe/o/openjfx/openjfx_8u161-b12-1ubuntu2_amd64.deb
  
  # one part for the java libraries and dependencies, one for the jar itself
  java:
    plugin: dump 
    # without libcamberra... it complains that canberra-gtk is not available. 
    stage-packages: [libc6, openjdk-8-jre, zlib1g, fonts-ubuntu, libcanberra-gtk-module, libcanberra-gtk3-module]
  dbtarzan:
    source: source/
    plugin: dump


