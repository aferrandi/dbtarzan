#prj/dbtarzan> mksnap/install.sh $PWD 1.25 
ROOTDIR=$1
VERSION=$2
SNAPDIR=$ROOTDIR/mksnap
cd $SNAPDIR
snap remove dbtarzan
snap install dbtarzan_$VERSION.0_amd64.snap --devmode
mkdir -p ~/snap/dbtarzan/common
cp $ROOTDIR/connections.config ~/snap/dbtarzan/common/
cp $ROOTDIR/composites.config ~/snap/dbtarzan/common/
cp $ROOTDIR/global.config ~/snap/dbtarzan/common/
cd -
