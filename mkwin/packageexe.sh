#!/bin/bash

#example: mkwin/packageexe.sh $PWD 1.27
ROOTDIR=$1
WINDIR=$ROOTDIR/mkwin
VERSION=$2
SCALA_VERSION=2.13
JRE=jre11
echo "ROOTDIR $ROOTDIR"
echo "WINDIR $WINDIR"
echo "VERSION $VERSION"
cp $WINDIR/launch4j_config.mod $WINDIR/launch4j_config.xml
JARFILE="$ROOTDIR/prjwin/target/scala-$SCALA_VERSION/dbtarzan-assembly-$VERSION.jar"
OUTFILE="$WINDIR/dbtarzan_$VERSION.exe" 
ICONFILE="$WINDIR/monkey-face-cartoon.ico"
JARESCAPED="${JARFILE//\//\\\/}"
OUTESCAPED="${OUTFILE//\//\\\/}"
ICONESCAPED="${ICONFILE//\//\\\/}"
LAUNCH4J_CONFIG=$WINDIR/launch4j_config.xml
echo "LAUNCH4J_CONFIG $LAUNCH4J_CONFIG"
sed -i "s/JARFILE/$JARESCAPED/g" $LAUNCH4J_CONFIG
sed -i "s/OUTFILE/$OUTESCAPED/g" $LAUNCH4J_CONFIG
sed -i "s/ICONFILE/$ICONESCAPED/g" $LAUNCH4J_CONFIG
#it must be in unix format to be able to work. dos2unix can help
$ROOTDIR/../../bin/launch4j/launch4j $LAUNCH4J_CONFIG
if [ $? -eq 0 ]
then
  rm -r $JRE
  wget -O $JRE.zip "https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.19%2B7/OpenJDK11U-jre_x64_windows_hotspot_11.0.19_7.zip"
  unzip $JRE.zip
  mv jdk* $JRE
  makensis -DVERSION=$VERSION $WINDIR/nsis.nsi
else
  echo "launch4j failed with exit code $?"
fi
