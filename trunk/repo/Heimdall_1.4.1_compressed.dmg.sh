curl http://android-casual.googlecode.com/svn/trunk/repo/Heimdall_1.4.1_compressed.dmg>./heimdall.dmg
hdiutil mount heimdall.dmg
sudo open /volumes/Heimdall_1.4.1/Heimdall\ Suite\ 1.4.1.pkg
hdiutil umount heimdall.dmg
rm ./heimdall.dmg
sudo chown -R root:wheel /System/Library/Extensions/heimdall.kext
sudo kextload /System/Library/Extensions/heimdall.kext
#Either restart USB somehow or figure out how to 
