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

import android.text.Spanned;
import android.text.style.StrikethroughSpan;

import com.commonsware.cwac.richtextutils.SpanTagRoster;
import com.commonsware.cwac.richtextutils.SpannableStringGenerator;
import com.commonsware.cwac.richtextutils.SpannedXhtmlGenerator;
import com.commonsware.cwac.richtextutils.handler.ClassSpanTagHandler;

import junit.framework.TestCase;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

public class ManualXhtmlTests extends TestCase {
  public void testFullDoubleSpan() throws Exception {
    final String input="<b><i>The quick brown fox jumped over the lazy dog.</i></b>";
    final String altInput="<i><b>The quick brown fox jumped over the lazy dog.</b></i>";
    SpanTagRoster tagRoster=new SpanTagRoster();

    Spanned fromInput=new SpannableStringGenerator(tagRoster).fromXhtml(input);
    String roundTrip=new SpannedXhtmlGenerator(tagRoster).toXhtml(fromInput);

    if (!input.equals(roundTrip)) {
      assertEquals(altInput, roundTrip);
    }
  }

  public void testClassSpanTagHandler() throws IOException, SAXException, ParserConfigurationException {
    SpanTagRoster tagRoster=new SpanTagRoster();

    tagRoster.registerSpanTagHandler(new ClassSpanTagHandler<StrikethroughSpan>("myCustomClass") {
      @Override
      public StrikethroughSpan buildSpanForTag(String name, Attributes a, String context) {
        return(new StrikethroughSpan());
      }

      @Override
      public Class getSupportedCharacterStyle() {
        return(StrikethroughSpan.class);
      }
    });

    String input="The <span class=\"myCustomClass\">slow</span> quick brown fox jumped over the lazy dog.";
    Spanned fromInput=new SpannableStringGenerator(tagRoster).fromXhtml(input);
    String roundTrip=new SpannedXhtmlGenerator(tagRoster).toXhtml(fromInput);

    assertEquals(input, roundTrip);
  }

  public void testPartialDoubleSpanAtBeginning() throws Exception {
    final String input = "<b><i>The quick brown fox</i> jumped over the lazy dog.</b>";

    Spanned fromInput=new SpannableStringGenerator().fromXhtml(input);
    String roundTrip=new SpannedXhtmlGenerator().toXhtml(fromInput);

    assertEquals(input, roundTrip);
  }

  public void testPartialDoubleSpanAtEnding() throws Exception {
    final String input = "<b>The quick brown fox jumped over <i>the lazy dog.</i></b>";

    Spanned fromInput=new SpannableStringGenerator().fromXhtml(input);
    String roundTrip=new SpannedXhtmlGenerator().toXhtml(fromInput);

    assertEquals(input, roundTrip);
  }

  public void testBulletsWithTagsInside() throws Exception {
    final String input = "The<ul><li>qu<b>i</b>ck</li><li>brown</li></ul> fox jumped over the lazy dog.";

    Spanned fromInput=new SpannableStringGenerator().fromXhtml(input);
    String roundTrip=new SpannedXhtmlGenerator().toXhtml(fromInput);

    assertEquals(input, roundTrip);

  }

  public void testOrderedList() throws Exception {
    String input = "<ol><li>one</li><li>two</li><li>three</li></ol>";

    Spanned fromInput=new SpannableStringGenerator().fromXhtml(input);
    String roundTrip=new SpannedXhtmlGenerator().toXhtml(fromInput);

    assertEquals(input, roundTrip);
  }

}
