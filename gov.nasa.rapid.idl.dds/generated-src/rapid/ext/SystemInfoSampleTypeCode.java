
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.typecode.*;

public class  SystemInfoSampleTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[7];

        sm[__i]=new  ValueMember("batteries", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.BatteryInfoSampleSequenceTypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("memory", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.MemoryInfoSampleTypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  ValueMember("cpus", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.CpuInfoSampleSequenceTypeCode.VALUE,3 , false);__i++;
        sm[__i]=new  ValueMember("filesystems", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.FilesystemInfoSampleSequenceTypeCode.VALUE,4 , false);__i++;
        sm[__i]=new  ValueMember("temperatures", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.ThermalInfoSampleSequenceTypeCode.VALUE,5 , false);__i++;
        sm[__i]=new  ValueMember("netInterfaces", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.NetTrafficInfoSampleSequenceTypeCode.VALUE,6 , false);__i++;
        sm[__i]=new  ValueMember("wifiInterfaces", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.WifiInfoSampleSequenceTypeCode.VALUE,7 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::SystemInfoSample",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

