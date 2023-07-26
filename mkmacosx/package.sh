#!/bin/bash

#example: mkmacosx/package.sh $PWD 1.27

DIR=$1
VERSION=$2
SCALA_VERSION=2.13
JRE=jre11
# delete previous versions
rm -r $DIR/DBTarzan-*.app*
rm $DIR/dbtarzan-assembly-*.jar
# download jre
rm -r $DIR/$JRE
wget -O $DIR/$JRE.tar.gz "https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.20%2B8/OpenJDK11U-jre_x64_mac_hotspot_11.0.20_8.tar.gz"
tar -zxvf $DIR/$JRE.tar.gz -C $DIR
mv $DIR/jdk* $DIR/$JRE
# create app
APP=DBTarzan-$VERSION.app
cp $DIR/../prjmac/target/scala-$SCALA_VERSION/dbtarzan-assembly-$VERSION.jar $DIR
(cd $DIR; jar2app dbtarzan-assembly-$VERSION.jar -n "DBTarzan-$VERSION" -i monkey-face-cartoon_256x256.icns -j "-DconfigPath=\$HOME/Library/ApplicationSupport/dbtarzan" -e universalJavaApplicationStub -r $DIR/jre11)
# fix executable (https://github.com/tofi86/universalJavaApplicationStub)
cp $DIR/universalJavaApplicationStub $DIR/$APP/Contents/MacOS
#unzip -d $DIR/$APP/Contents/PlugIns/jre11/Contents/Home/lib $DIR/dbtarzan-assembly-$VERSION.jar  "*.dylib"
# python must be available as "python". Use a link if it is called "python3"
chmod a+x $DIR/$APP/Contents/MacOS/universalJavaApplicationStub
# fix space in the Info.plist configuration directory  
sed -i 's/ApplicationSupport/Application Support/g' $DIR/$APP/Contents/Info.plist
(cd $DIR; sed -i '/<string>APPL<\/string>/r LSEnvironment.txt' $APP/Contents/Info.plist)
# build zip
(cd $DIR; zip -r $APP.zip $APP/*)