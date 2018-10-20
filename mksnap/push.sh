#e.g. 1.15
VERSION=%1
#e.g. 7
REVISION=%2
snapcraft push dbtarzan_$VERSION.0_amd64.snap
snapcraft release dbtarzan $REVISION stable
