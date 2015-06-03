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

package com.commonsware.cwac.richedit.test;

import android.support.test.InstrumentationRegistry;
import android.text.Spanned;
import com.commonsware.cwac.richedit.Effect;
import com.commonsware.cwac.richedit.RichEditText;
import com.commonsware.cwac.richtextutils.SpanTagRoster;
import com.commonsware.cwac.richtextutils.SpannableStringGenerator;
import com.commonsware.cwac.richtextutils.SpannedXhtmlGenerator;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

abstract public class EffectTestsBase<T> {
  abstract protected void applyEffect(Effect<T> effect);
  abstract protected void removeEffect(Effect<T> effect);
  abstract protected Effect<T> getEffect();
  abstract protected String getStartTag();
  abstract protected String getEndTag();

  private static SpanTagRoster tagRoster=new SpanTagRoster();
  protected RichEditText richedit=null;

  @Before
  public void init() {
    InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
      @Override
      public void run() {
        richedit=new RichEditText(InstrumentationRegistry.getInstrumentation().getTargetContext());
      }
    });
  }

  protected Spanned buildSpanned(String msg) throws IOException, SAXException, ParserConfigurationException {
    return(new SpannableStringGenerator(tagRoster).fromXhtml(msg));
  }

  protected String buildResult() {
    return(new SpannedXhtmlGenerator(tagRoster).toXhtml(richedit.getText()));
  }

  protected void selectFullLine() {
    richedit.setSelection(0, richedit.getText().length());
  }

  @Test
  public void fullLine() throws Exception {
    String input="The quick brown fox jumped over the lazy dog.";
    Spanned fromInput=buildSpanned(input);

    richedit.setText(fromInput);
    selectFullLine();
    applyEffect(getEffect());
    Assert.assertEquals("effect across entire line, added",
        getStartTag() + input + getEndTag(), buildResult());

    removeEffect(getEffect());
    Assert.assertEquals("boolean effect across entire line, removed",
        input, buildResult());
  }

  @Test
  public void firstWord() throws Exception {
    String input="The quick brown fox jumped over the lazy dog";
    Spanned fromInput=buildSpanned(input);

    richedit.setText(fromInput);
    richedit.setSelection(0, 3);
    applyEffect(getEffect());
    Assert.assertEquals("effect on first word, added",
        getStartTag() + input.substring(0, 3) + getEndTag() + input.substring(3),
        buildResult());

    removeEffect(getEffect());
    Assert.assertEquals("boolean effect across first word, removed",
        input, buildResult());
  }

  @Test
  public void middleWord() throws Exception {
    String input="The quick brown fox jumped over the lazy dog";
    Spanned fromInput=buildSpanned(input);

    richedit.setText(fromInput);
    richedit.setSelection(16, 19);
    applyEffect(getEffect());
    Assert.assertEquals("effect on middle word, added",
        input.substring(0, 16) + getStartTag() +
        input.substring(16, 19) + getEndTag() +
        input.substring(19),
        buildResult());

    removeEffect(getEffect());
    Assert.assertEquals("boolean effect across middle word, removed",
        input, buildResult());
  }

  @Test
  public void endWord() throws Exception {
    String input="The quick brown fox jumped over the lazy dog";
    Spanned fromInput=buildSpanned(input);

    richedit.setText(fromInput);
    richedit.setSelection(42, 44);
    applyEffect(getEffect());
    Assert.assertEquals("effect on end word, added",
        input.substring(0, 42) + getStartTag() +
            input.substring(42, 44) + getEndTag(),
        buildResult());

    removeEffect(getEffect());
    Assert.assertEquals("boolean effect across end word, removed",
        input, buildResult());
  }
}
