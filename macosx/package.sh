VERSION=1.13
rm -r DBTarzan-$VERSION.app
rm DBTarzan-$VERSION.app.zip
cp ../target/scala-2.12/dbtarzan-assembly-$VERSION.jar .
jar2app dbtarzan-assembly-$VERSION.jar -n "DBTarzan-$VERSION" -i monkey-face-cartoon_256x256.icns
cp dbtarzanConfigPath.cfg DBTarzan-$VERSION.app/Contents
zip -r DBTarzan-$VERSION.app.zip DBTarzan-$VERSION.app
