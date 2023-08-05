#prj/dbtarzan> mksnap/create.sh $PWD 1.28 3.1.1
ROOTDIR=$1
VERSION=$2
SCALA_VER=$3
SNAPDIR=$ROOTDIR/mksnap
cd $SNAPDIR
cp snapcraft.mod snapcraft.yaml
sed -i "s/VERSION/$VERSION/g" snapcraft.yaml
rm dbtarzan_$VERSION.0_amd64.snap
find . -type f -name "dbtarzan-assembly*" -delete
ASSEMBLY=../prjlinux/target/scala-$SCALA_VER/dbtarzan-assembly-$VERSION.jar
cp $ASSEMBLY .
cp $ASSEMBLY $SNAPDIR/source/
cp monkey-face-cartoon.png $SNAPDIR/source/
cp dbtarzan.desktop $SNAPDIR/source/
snapcraft clean
snapcraft build
find parts/java -type f -name "dbtarzan-assembly*" -delete
find . -type f -name "dbtarzan_source.tar.bz2" -delete
snapcraft snap
cd -
