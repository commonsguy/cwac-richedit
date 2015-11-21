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

package com.commonsware.cwac.richedit;

import android.text.Spannable;
import android.text.style.BulletSpan;
import com.commonsware.cwac.richtextutils.Selection;

public class BulletEffect extends Effect<Boolean> {
  @Override
  public boolean existsInSelection(RichEditText editor) {
    Spannable str=editor.getText();
    Selection selection=new Selection(editor).extendToFullLine(str);

    return (getBulletSpans(str, selection).length > 0);
  }

  @Override
  public Boolean valueInSelection(RichEditText editor) {
    return (existsInSelection(editor));
  }

  @Override
  public void applyToSelection(RichEditText editor, Boolean add) {
    Spannable str=editor.getText();
    Selection selection=new Selection(editor).extendToFullLine(str);

    for (BulletSpan span : getBulletSpans(str, selection)) {
      str.removeSpan(span);
    }

    if (add.booleanValue()) {
      for (Selection chunk : selection.buildSelectionsForLines(str)) {
        str.setSpan(new BulletSpan(), chunk.getStart(),
            chunk.getEnd(),
            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
      }
    }
  }

  void applyToSelection(RichEditText editor, Selection bullets, Boolean add) {
    Spannable str=editor.getText();
    Selection selection=bullets.extendToFullLine(str);

    for (BulletSpan span : getBulletSpans(str, selection)) {
      str.removeSpan(span);
    }

    if (add.booleanValue()) {
      for (Selection chunk : selection.buildSelectionsForLines(str)) {
        str.setSpan(new BulletSpan(), chunk.getStart(),
            chunk.getEnd(),
            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
      }
    }
  }

  private BulletSpan[] getBulletSpans(Spannable str,
                                      Selection selection) {
    return (str.getSpans(selection.getStart(), selection.getEnd(),
        BulletSpan.class));
  }

  void updateBullets(RichEditText editor) {
    Selection initial=new Selection(editor.getSelectionStart(), editor.getSelectionEnd());
    Selection extended=extendToBulletedList(initial, editor.getText(), 0);

    if (extended!=initial) {
      applyToSelection(editor, extended, true);
    }
  }

  Selection extendToBulletedList(Selection initial, Spannable src, int lastSpanCount) {
    BulletSpan[] spans=src.getSpans(initial.getStart(), initial.getEnd(),
                                    BulletSpan.class);

    if (spans.length>lastSpanCount) {
      int firstSelStart=(initial.getStart() > 1 ? initial.getStart() - 2 : 0);
      Selection chunk=new Selection(firstSelStart, initial.getEnd()).extendToFullLine(src);

      return(extendToBulletedList(chunk, src, spans.length));
    }
    else {
      if (spans.length>0) {
        int start=src.getSpanStart(spans[0]);

        for (BulletSpan span : spans) {
          if (src.getSpanStart(span)<start)
            start=src.getSpanStart(span);
        }

        if (initial.getStart()!=start) {
          initial=new Selection(start, initial.getEnd());
        }
      }

      return(initial);
    }
  }
}
