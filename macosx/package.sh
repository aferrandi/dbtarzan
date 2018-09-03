VERSION=1.13
APP=DBTarzan-$VERSION.app
rm -r $APP
rm $APP.zip
cp ../target/scala-2.12/dbtarzan-assembly-$VERSION.jar .
jar2app dbtarzan-assembly-$VERSION.jar -n "DBTarzan-$VERSION" -i monkey-face-cartoon_256x256.icns -j "-DconfigPath=\$HOME/Library/ApplicationSupport/dbtarzan" -e universalJavaApplicationStub
# cp dbtarzanConfigPath.cfg $APP/Contents
cp ./universalJavaApplicationStub $APP/Contents/MacOS
chmod a+x $APP/Contents/MacOS/universalJavaApplicationStub
sed -i 's/ApplicationSupport/Application Support/g' $APP/Contents/Info.plist
zip -r $APP.zip $APP
