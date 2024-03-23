#prj/dbtarzan> mksnap/push.sh $PWD 1.25
ROOTDIR=$1
VERSION=$2
snapcraft upload --release=stable $ROOTDIR/mksnap/dbtarzan_$VERSION.0_amd64.snap
