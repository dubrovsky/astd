package com.isc.astd.service.util;

import sun.security.util.ObjectIdentifier;

/**
 * Created by LAN on 03.10.2014.
 */
public class EcpOIDs {

    private static final int[] I_O_Arr = new int[]{2, 5, 4, 41};
    private static final int[] EXTENSION_UNP_Arr = new int[]{1, 3, 6, 1, 4, 1, 12656, 106, 101};

    public static final ObjectIdentifier EXTENSION_UNP_OID = ObjectIdentifier.newInternal(EXTENSION_UNP_Arr);
    public static final ObjectIdentifier I_O_OID = ObjectIdentifier.newInternal(I_O_Arr);
    public static final ObjectIdentifier PERSONAL_OID = ObjectIdentifier.newInternal(new int[]{2,5,4,5});
    public static final String GOSSUAK_EXTENSION_PERSONAL_NUMBER_OID = "1.2.112.1.2.1.1.1.1.1";
    public static final String GOSSUAK_EXTENSION_DOLJ_OID = "1.2.112.1.2.1.1.5.1";
}
