DIR=$1
VERSION=$2
# delete previous versions
rm -r $DIR/DBTarzan-*.app*
rm $DIR/dbtarzan-assembly-*.jar
# download jre
rm -r $DIR/jre11
wget -O $DIR/jre11.tar.gz "https://api.adoptopenjdk.net/v2/binary/nightly/openjdk11?openjdk_impl=hotspot&os=mac&arch=x64&release=latest&type=jre"
tar -zxvf $DIR/jre11.tar.gz -C $DIR
mv $DIR/jdk* $DIR/jre11
# create app
APP=DBTarzan-$VERSION.app
cp $DIR/../prjmac/target/scala-2.12/dbtarzan-assembly-$VERSION.jar $DIR
(cd $DIR; jar2app dbtarzan-assembly-$VERSION.jar -n "DBTarzan-$VERSION" -i monkey-face-cartoon_256x256.icns -j "-DconfigPath=\$HOME/Library/ApplicationSupport/dbtarzan" -e universalJavaApplicationStub -r $DIR/jre11)
# fix executable (https://github.com/tofi86/universalJavaApplicationStub)
cp $DIR/universalJavaApplicationStub $DIR/$APP/Contents/MacOS
#unzip -d $DIR/$APP/Contents/PlugIns/jre11/Contents/Home/lib $DIR/dbtarzan-assembly-$VERSION.jar  "*.dylib"
chmod a+x $DIR/$APP/Contents/MacOS/universalJavaApplicationStub
# fix space in the Info.plist configuration directory  
sed -i 's/ApplicationSupport/Application Support/g' $DIR/$APP/Contents/Info.plist
(cd $DIR; sed -i '/<string>APPL<\/string>/r LSEnvironment.txt' $APP/Contents/Info.plist)
# build zip
(cd $DIR; zip -r $APP.zip $APP/*)