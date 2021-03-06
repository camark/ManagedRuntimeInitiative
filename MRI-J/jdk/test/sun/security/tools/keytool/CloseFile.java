/*
 * Copyright 2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

/*
 * @test
 * @bug 6489721
 * @summary keytool has not closed several file streams
 * @author weijun.wang
 *
 * This test is only useful on Windows, which fails before the fix and succeeds
 * after it. On other platforms, it always passes.
 */

import sun.security.tools.KeyTool;
import java.io.*;

public class CloseFile {
    public static void main(String[] args) throws Exception {
        remove("f0", false);
        remove("f1", false);
        run("-keystore f0 -storepass changeit -keypass changeit -genkeypair -dname CN=Haha");
        run("-keystore f0 -storepass changeit -keypass changeit -certreq -file f2");
        remove("f2", true);
        run("-keystore f0 -storepass changeit -keypass changeit -exportcert -file f2");
        remove("f2", true);
        run("-keystore f0 -storepass changeit -keypass changeit -exportcert -file f2");
        run("-keystore f0 -storepass changeit -keypass changeit -delete -alias mykey");
        run("-keystore f0 -storepass changeit -keypass changeit -importcert -file f2 -noprompt");
        remove("f2", true);
        run("-keystore f0 -storepass changeit -keypass changeit -exportcert -file f2");
        run("-printcert -file f2");
        remove("f2", true);
        remove("f0", true);
        run("-keystore f0 -storepass changeit -keypass changeit -genkeypair -dname CN=Haha");
        run("-importkeystore -srckeystore f0 -destkeystore f1 -srcstorepass changeit -deststorepass changeit");
        remove("f0", true);
        remove("f1", true);
    }

    static void run(String s) throws Exception {
        KeyTool.main((s+" -debug").split(" "));
    }
    static void remove(String filename, boolean check) {
        new File(filename).delete();
        if (check && new File(filename).exists()) {
            throw new RuntimeException("Error deleting " + filename);
        }
    }
}
