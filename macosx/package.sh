DIR=$1
VERSION=$2
# delete previous versions
rm -r $DIR/DBTarzan-*.app*
rm $DIR/dbtarzan-assembly-*.jar
# create app
APP=DBTarzan-$VERSION.app
cp $DIR/../target/scala-2.12/dbtarzan-assembly-$VERSION.jar $DIR
(cd $DIR; jar2app dbtarzan-assembly-$VERSION.jar -n "DBTarzan-$VERSION" -i monkey-face-cartoon_256x256.icns -j "-DconfigPath=\$HOME/Library/ApplicationSupport/dbtarzan" -e universalJavaApplicationStub)
# fix executable (https://github.com/tofi86/universalJavaApplicationStub)
cp $DIR/universalJavaApplicationStub $DIR/$APP/Contents/MacOS
chmod a+x $DIR/$APP/Contents/MacOS/universalJavaApplicationStub
# fix space in the Info.plist configuration directory  
sed -i 's/ApplicationSupport/Application Support/g' $DIR/$APP/Contents/Info.plist
# build zip
zip -r $DIR/$APP.zip $DIR/$APP
