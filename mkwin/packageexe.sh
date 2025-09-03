#!/bin/bash

#example: mkwin/packageexe.sh $PWD 1.27 3.1.1
ROOTDIR=$1
WINDIR=$ROOTDIR/mkwin
VERSION=$2
SCALA_VERSION=$3
JRE=$WINDIR/jre21
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
  rm -rf $JRE
  wget -O $JRE.zip "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.8%2B9/OpenJDK21U-jre_x64_windows_hotspot_21.0.8_9.zip"
  unzip $JRE.zip
  mv jdk* $JRE
  makensis -DVERSION=$VERSION $WINDIR/nsis.nsi
else
  echo "launch4j failed with exit code $?"
fi
