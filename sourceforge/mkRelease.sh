#!/bin/bash

#example: sourceforge/mkRelease.sh $PWD 1.27 3.4.0
ROOTDIR=$1
VERSION=$2
SCALA_VER=$3
RELEASEDIR=$ROOTDIR/sourceforge/release
mkdir $RELEASEDIR
cp $ROOTDIR/mksnap/dbtarzan_$VERSION.0_amd64.snap $RELEASEDIR
cp $ROOTDIR/mkmacosx/DBTarzan-$VERSION.app.zip $RELEASEDIR
cp $ROOTDIR/mkwin/DBTarzan-Install-$VERSION.exe $RELEASEDIR
cp $ROOTDIR/prjlinux/target/dbtarzan_${VERSION}_all.deb $RELEASEDIR
cp $ROOTDIR/prjlinux/target/scala-$SCALA_VER/dbtarzan-assembly-$VERSION.jar $RELEASEDIR/dbtarzan-assembly-${VERSION}_linux.jar
cp $ROOTDIR/prjmac/target/scala-$SCALA_VER/dbtarzan-assembly-$VERSION.jar $RELEASEDIR/dbtarzan-assembly-${VERSION}_mac.jar
cp $ROOTDIR/prjwin/target/scala-$SCALA_VER/dbtarzan-assembly-$VERSION.jar $RELEASEDIR/dbtarzan-assembly-${VERSION}_win.jar
