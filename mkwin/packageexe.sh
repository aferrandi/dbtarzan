#!/bin/bash
ROOTDIR=$1
WINDIR=$ROOTDIR/mkwin
VERSION=$2
echo "ROOTDIR $ROOTDIR WINDIR $WINDIR VERSION $VERSION"
cp $WINDIR/launch4j_config.mod $WINDIR/launch4j_config.xml
JARFILE="$ROOTDIR/prjwin/target/scala-2.12/dbtarzan-assembly-$VERSION.jar"
OUTFILE="$WINDIR/dbtarzan_$VERSION.exe" 
ICONFILE="$WINDIR/monkey-face-cartoon.ico"
JARESCAPED="${JARFILE//\//\\\/}"
OUTESCAPED="${OUTFILE//\//\\\/}"
ICONESCAPED="${ICONFILE//\//\\\/}"
sed -i "s/JARFILE/$JARESCAPED/g" $WINDIR/launch4j_config.xml
sed -i "s/OUTFILE/$OUTESCAPED/g" $WINDIR/launch4j_config.xml
sed -i "s/ICONFILE/$ICONESCAPED/g" $WINDIR/launch4j_config.xml
$ROOTDIR/../../bin/launch4j/launch4j $WINDIR/launch4j_config.xml
rm -r jre11
wget -O jre11.zip "https://api.adoptopenjdk.net/v2/binary/nightly/openjdk11?openjdk_impl=hotspot&os=windows&arch=x64&release=latest&type=jre"
unzip jre11.zip
mv jdk* jre11
makensis -DVERSION=$VERSION $WINDIR/nsis.nsi
