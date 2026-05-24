#!/bin/bash

#example: mkwin/packageexe.sh $PWD 1.27 3.1.1 25
ROOTDIR=$1
WINDIR=$ROOTDIR/mkwin
VERSION=$2
SCALA_VERSION=$3
JAVA_VERSION=$4
JRE=$WINDIR/jre$JAVA_VERSION
echo "ROOTDIR $ROOTDIR"
echo "WINDIR $WINDIR"
echo "VERSION $VERSION"
echo "JAVA_VERSION $JAVA_VERSION"
cp $WINDIR/launch4j_config.mod $WINDIR/launch4j_config.xml
JARFILE="$ROOTDIR/prjwin/target/scala-$SCALA_VERSION/dbtarzan-assembly-$VERSION.jar"
OUTFILE="$WINDIR/dbtarzan_$VERSION.exe" 
ICONFILE="$WINDIR/monkey-face-cartoon.ico"
JARESCAPED="${JARFILE//\//\\\/}"
OUTESCAPED="${OUTFILE//\//\\\/}"
ICONESCAPED="${ICONFILE//\//\\\/}"
LAUNCH4J_CONFIG=$WINDIR/launch4j_config.xml
echo "LAUNCH4J_CONFIG $LAUNCH4J_CONFIG"
sed -i -e "s/%JARFILE%/$JARESCAPED/g" -e "s/%OUTFILE%/$OUTESCAPED/g" -e "s/%ICONFILE%/$ICONESCAPED/g" -e "s/%JAVAVERSION%/$JAVA_VERSION/g" $LAUNCH4J_CONFIG
#it must be in unix format to be able to work. dos2unix can help
$ROOTDIR/../../bin/launch4j/launch4j $LAUNCH4J_CONFIG
if [ $? -eq 0 ]
then
  rm -rf $JRE
  # from https://github.com/adoptium/temurin25-binaries/releases/
  JRE_URL="https://github.com/adoptium/temurin25-binaries/releases/download/jdk-25.0.3%2B9/OpenJDK25U-jre_x64_windows_hotspot_25.0.3_9.zip"
  wget -O $JRE.zip $JRE_URL
  unzip $JRE.zip
  mv jdk* $JRE
  makensis -DVERSION=$VERSION $WINDIR/nsis.nsi
else
  echo "launch4j failed with exit code $?"
fi
