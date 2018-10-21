ROOTDIR=$1
VERSION=$2
SNAPDIR=$ROOTDIR/mksnap
cd $SNAPDIR
cp snapcraft.mod snapcraft.yaml
sed -i "s/VERSION/$VERSION/g" snapcraft.yaml
rm dbtarzan_$VERSION.0_amd64.snap
find . -type f -name dbtarzan-assembly* -delete
cp ../target/scala-2.12/dbtarzan-assembly-$VERSION.jar .
cp ../target/scala-2.12/dbtarzan-assembly-$VERSION.jar $SNAPDIR/source/
snapcraft clean
snapcraft build
find parts/java -type f -name dbtarzan-assembly* -delete
find . -type f -name dbtarzan_source.tar.bz2 -delete
snapcraft snap
cd -