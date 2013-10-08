gecho start;

echo Current location?
pwd;

echo Looking for installer.iss.base file...
ls -l installer.iss.base;

build_number=`tail -1 ./build.number | cut -f2 -d"=" | tr "\r" " "`;

echo build number is $build_number;

sed "s/__build__number__/Build $build_number/g" installer.iss.base > installer.iss;

sed "s/C:\\\\Users\\\\Public\\\\Documents\\\\Compendium/\{app\}/g" installer.iss > installer.xp.iss;

cd System/src/com/compendium/core;

ls -l ICoreConstants.java.base;

sed "s/__build__number__/Build $build_number/g" ICoreConstants.java.base > ICoreConstants.java;

cd ../../../../../;

pwd;

echo done;

exit;