/***
 Copyright (c) 2015 CommonsWare, LLC

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.commonsware.cwac.richtextutils.test;

import android.content.res.AssetManager;
import android.test.AndroidTestCase;
import android.text.Html;
import android.text.Spanned;
import com.commonsware.cwac.richtextutils.SpanTagRoster;
import com.commonsware.cwac.richtextutils.SpannableStringGenerator;
import com.commonsware.cwac.richtextutils.SpannedXhtmlGenerator;
import java.util.Scanner;

public class AssetFileTestCase extends AndroidTestCase {
  public void testTheWholeShootingMatch() throws Throwable {
    AssetManager assets=getContext().getAssets();
    String[] tests=assets.list("testFiles");
    int testsRun=0;
    SpanTagRoster tagRoster=new SpanTagRoster();

    for (String testFile : tests) {
      String input=
        new Scanner(assets.open("testFiles/"+testFile))
          .useDelimiter("\\Z")
          .next();
      Spanned fromInput=
        new SpannableStringGenerator(tagRoster).fromXhtml(input);
      String firstRoundTrip=
        new SpannedXhtmlGenerator(tagRoster).toXhtml(fromInput);

      assertEquivalent(
        String.format("Test %s round trip", testFile),
        input, firstRoundTrip);
      testsRun++;
    }

    assertFalse("were any tests run?", 0==testsRun);
  }

  private void assertEquivalent(String msg, String expected, String actual) {
    assertEquals(msg, expected, actual);
  }
}
