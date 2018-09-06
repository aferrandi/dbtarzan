#!/bin/bash
ROOTDIR=$1
WINDIR=$ROOTDIR/mkwin
VERSION=$2
echo "ROOTDIR $ROOTDIR WINDIR $WINDIR VERSION $VERSION"
cp $WINDIR/launch4j_config.mod $WINDIR/launch4j_config.xml
JARFILE="$ROOTDIR/target/scala-2.12/dbtarzan-assembly-$VERSION.jar"
OUTFILE="$WINDIR/dbtarzan_$VERSION.exe" 
ICONFILE="$WINDIR/monkey-face-cartoon.ico"
JARESCAPED="${JARFILE//\//\\\/}"
OUTESCAPED="${OUTFILE//\//\\\/}"
ICONESCAPED="${ICONFILE//\//\\\/}"
sed -i "s/JARFILE/$JARESCAPED/g" $WINDIR/launch4j_config.xml
sed -i "s/OUTFILE/$OUTESCAPED/g" $WINDIR/launch4j_config.xml
sed -i "s/ICONFILE/$ICONESCAPED/g" $WINDIR/launch4j_config.xml
$ROOTDIR/../../bin/launch4j/launch4j $WINDIR/launch4j_config.xml