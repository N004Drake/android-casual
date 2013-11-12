/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CASUAL.network.CASUALDevIntegration;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adamoutler
 */
public class CFAutoRootDbTest {
    
    public CFAutoRootDbTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of returnForMyDevice method, of class CFAutoRootDb.
     */
    @Test
    public void testReturnForMyDevice() {
        System.out.println("returnForMyDevice");
        CFAutoRootDb instance = new CFAutoRootDb(BUILDPROP);
        String expContains = "http://download.chainfire.eu/313/CF-Root/CF-Auto-Root/";
        String result = instance.returnForMyDevice();
        assert(result.contains(expContains));
    }

    
    final String BUILDPROP="# begin build properties\n" +
"# autogenerated by buildinfo.sh\n" +
"ro.build.id=JRO03C\n" +
"ro.build.display.id=JRO03C.I605VRALL4\n" +
"ro.build.version.incremental=I605VRALL4\n" +
"ro.build.version.sdk=16\n" +
"ro.build.version.codename=REL\n" +
"ro.build.version.release=4.1.1\n" +
"ro.build.date=Fri Dec 28 21:26:30 KST 2012\n" +
"ro.build.date.utc=1356697590\n" +
"ro.build.type=user\n" +
"ro.build.user=se.infra\n" +
"ro.build.host=SEP-79\n" +
"ro.build.tags=release-keys\n" +
"ro.product.model=SCH-I605\n" +
"ro.product.brand=Verizon\n" +
"ro.product.name=t0ltevzw\n" +
"ro.product.device=t0ltevzw\n" +
"otaupdater.otaid=jellybeanst0ltevzw\n" +
"otaupdater.otatime=20130422-0300\n" +
"otaupdater.otaver=0.1.4\n" +
"ro.product.board=smdk4x12\n" +
"ro.product.cpu.abi=armeabi-v7a\n" +
"ro.product.cpu.abi2=armeabi\n" +
"ro.product_ship=true\n" +
"ro.product.manufacturer=samsung\n" +
"ro.product.locale.language=en\n" +
"ro.product.locale.region=GB\n" +
"ro.wifi.channels=\n" +
"ro.board.platform=exynos4\n" +
"# ro.build.product is obsolete; use ro.product.device\n" +
"ro.build.product=t0ltevzw\n" +
"# Do not try to parse ro.build.description or .fingerprint\n" +
"ro.build.description=t0ltevzw-user 4.1.1 JRO03C I605VRALL4 release-keys\n" +
"ro.build.fingerprint=Verizon/t0ltevzw/t0ltevzw:4.1.1/JRO03C/I605VRALL4:user/release-keys\n" +
"ro.build.characteristics=verizon\n" +
"# Samsung Specific Properties\n" +
"ro.build.PDA=I605VRALL4\n" +
"ro.build.hidden_ver=I605VRALL4\n" +
"ro.build.changelist=414933\n" +
"# end build properties\n" +
"# 9x15 RIL Basic\n" +
"rild.libpath=/system/lib/libril-qc-qmi-1.so\n" +
"rild.libargs=-d /dev/ttyS0\n" +
"\n" +
"ro.sf.lcd_density=320\n" +
"ro.lcd_min_brightness=20\n" +
"\n" +
"ro.kernel.qemu=0\n" +
"\n" +
"ro.tvout.enable=true\n" +
"persist.sys.storage_preload=1\n" +
"\n" +
"net.streaming.rtsp.uaprof=http://wap.samsungmobile.com/uaprof/\n" +
"\n" +
"# Multimedia property for Smart View\n" +
"media.enable-commonsource=true\n" +
"\n" +
"\n" +
"\n" +
"# Use CDMALTE Phone\n" +
"telephony.lteOnCdmaDevice=1\n" +
"\n" +
"# Enable time services daemon\n" +
"persist.timed.enable=true\n" +
"\n" +
"# Keep SIM state on LPM mode\n" +
"persist.radio.apm_sim_not_pwdn=1\n" +
"\n" +
"# Don't wait the card state for RADIO POWER request\n" +
"persist.radio.no_wait_for_card=1\n" +
"\n" +
"# For sys info indication\n" +
"persist.radio.add_power_save=1\n" +
"\n" +
"# Snapshot Setting\n" +
"persist.radio.snapshot_enabled=1\n" +
"persist.radio.snapshot_timer=22\n" +
"\n" +
"# Control EONS (true = EONS enabled, false = EONS disabled)\n" +
"persist.eons.enabled=false\n" +
"\n" +
"# Data modules\n" +
"ro.use_data_netmgrd=true\n" +
"\n" +
"# Default ECCList (Except USCC)\n" +
"ro.ril.ecclist=112,911,#911,*911\n" +
"\n" +
"# Support Global Mode (Global Mode project)\n" +
"ro.config.multimode_cdma=1\n" +
"\n" +
"# Use data service state for signal display (Except SPR)\n" +
"ro.config.combined_signal=true\n" +
"\n" +
"# Restore delay of the default network - 30 mins (VZW)\n" +
"android.telephony.apn-restore=1800000\n" +
"\n" +
"# Restore delay of VZW Apps APN - 1 min (VZW)\n" +
"vzw.telephony.appsapn-restore=60000\n" +
"\n" +
"# Retry Timer (VZW)\n" +
"ro.gsm.data_retry_config=max_retries=infinite,5000,5000,60000,120000,480000,900000\n" +
"\n" +
"#\n" +
"# ADDITIONAL_BUILD_PROPERTIES\n" +
"#\n" +
"ro.opengles.version=131072\n" +
"debug.hwui.render_dirty_regions=false\n" +
"ro.sf.lcd_density=320\n" +
"ro.error.receiver.default=com.samsung.receiver.error\n" +
"dalvik.vm.heapstartsize=8m\n" +
"dalvik.vm.heapgrowthlimit=64m\n" +
"dalvik.vm.heapsize=256m\n" +
"ro.hdcp2.rx=tz\n" +
"ro.secwvk=220\n" +
"ro.sec.fle.encryption=true\n" +
"ro.config.ringtone=Enter_the_Nexus.ogg\n" +
"ro.config.notification_sound=Beat_Box_Android.ogg\n" +
"ro.config.alarm_alert=Walk_in_the_forest.ogg\n" +
"windowsmgr.max_events_per_sec=300\n" +
"ro.config.media_sound=Media_preview_Touch_the_light.ogg\n" +
"ro.config.vc_call_vol_steps=7\n" +
"keyguard.no_require_sim=true\n" +
"ro.com.android.dateformat=MM-dd-yyyy\n" +
"ro.carrier=unknown\n" +
"ro.com.google.clientidbase=android-samsung\n" +
"ro.ril.hsxpa=1\n" +
"ro.ril.gprsclass=10\n" +
"ro.adb.qemud=1\n" +
"ro.setupwizard.mode=OPTIONAL\n" +
"ro.com.google.apphider=off\n" +
"ro.com.google.clientidbase.ms=android-verizon\n" +
"ro.com.google.clientidbase.am=android-verizon\n" +
"ro.com.google.clientidbase.gmm=android-samsung\n" +
"ro.com.google.clientidbase.yt=android-verizon\n" +
"ro.com.google.clientidbase.vs=android-verizon\n" +
"ro.com.google.gmsversion=4.1_r2\n" +
"dalvik.vm.dexopt-flags=m=y\n" +
"net.bt.name=Android\n" +
"dalvik.vm.stack-trace-file=/data/anr/traces.txt\n" +
"\n" +
"#Beans Tweaks\n" +
"#disable adb debug notification\n" +
"persist.service.adb.enable=1\n" +
"persist.adb.notify=0\n" +
"ro.max.fling_velocity=12000 \n" +
"ro.min.fling_velocity=8000\n" +
"\n" +
"# Power Saving\n" +
"wifi.supplicant_scan_interval=250\n" +
"ro.ril.disable.power.collapse=1\n" +
"pm.sleep_mode=1\n" +
"\n" +
"#Davlik Tweaks\n" +
"debug.performance.tuning=1\n" +
"persist.sys.scrollingcache=3\n" +
"ro.kernel.android.checkjni=0 \n" +
"ro.kernel.checkjni=0\n" +
"dalvik.vm.verify-bytecode=false\n" +
"dalvik.vm.dexopt-flags=v=n,o=v,m=y\n" +
"\n" +
"#telephonyGPS tweaks\n" +
"ro.telephony.call_ring.delay=0\n" +
"ro.mot.eri.losalert.delay=1000";
}
