#!/bin/bash

#example: mkmacosx/package.sh $PWD 1.27

DIR=$1
VERSION=$2
SCALA_VERSION=2.13
JRE=jre17
# delete previous versions
rm -r $DIR/DBTarzan-*.app*
rm $DIR/dbtarzan-assembly-*.jar
# download jre
rm -r $DIR/$JRE
wget -O $DIR/$JRE.tar.gz "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.8%2B7/OpenJDK17U-jre_aarch64_mac_hotspot_17.0.8_7.tar.gz"
tar -zxvf $DIR/$JRE.tar.gz -C $DIR
mv $DIR/jdk* $DIR/$JRE
# create app
APP=DBTarzan-$VERSION.app
cp $DIR/../prjmac/target/scala-$SCALA_VERSION/dbtarzan-assembly-$VERSION.jar $DIR
(cd $DIR; jar2app dbtarzan-assembly-$VERSION.jar -n "DBTarzan-$VERSION" -i monkey-face-cartoon_256x256.icns -j "-DconfigPath=\$HOME/Library/ApplicationSupport/dbtarzan" -e universalJavaApplicationStub -r $DIR/jre17)
# fix executable (https://github.com/tofi86/universalJavaApplicationStub)
cp $DIR/universalJavaApplicationStub $DIR/$APP/Contents/MacOS
#unzip -d $DIR/$APP/Contents/PlugIns/jre17/Contents/Home/lib $DIR/dbtarzan-assembly-$VERSION.jar  "*.dylib"
# python must be available as "python". Use a link if it is called "python3"
chmod a+x $DIR/$APP/Contents/MacOS/universalJavaApplicationStub
# fix space in the Info.plist configuration directory  
sed -i 's/ApplicationSupport/Application Support/g' $DIR/$APP/Contents/Info.plist
(cd $DIR; sed -i '/<string>APPL<\/string>/r LSEnvironment.txt' $APP/Contents/Info.plist)
# build zip
(cd $DIR; zip -r $APP.zip $APP/*)