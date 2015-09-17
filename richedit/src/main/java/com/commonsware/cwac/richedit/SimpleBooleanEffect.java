/***
  Copyright (c) 2008-2011 CommonsWare, LLC
  
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
import android.util.Log;
import com.commonsware.cwac.richtextutils.Selection;

public class SimpleBooleanEffect<T> extends Effect<Boolean> {
  private Class<T> clazz;

  SimpleBooleanEffect(Class<T> clazz) {
    this.clazz=clazz;
  }

  @Override
  public boolean existsInSelection(RichEditText editor) {
    Selection selection=new Selection(editor);
    Spannable str=editor.getText();
    boolean result=false;

    if (selection.getStart() != selection.getEnd()) {
      T[] spans=str.getSpans(selection.getStart(), selection.getEnd(), clazz);

      result=(spans.length > 0);
    }
    else {
      T[] spansBefore=
          str.getSpans(selection.getStart() - 1, selection.getEnd(), clazz);

      result=(spansBefore.length > 0);
    }

    return(result);
  }

  @Override
  public Boolean valueInSelection(RichEditText editor) {
    return(existsInSelection(editor));
  }

  @Override
  public void applyToSelection(RichEditText editor, Boolean add) {
    applyToSpannable(editor.getText(), new Selection(editor), add);
  }

  void applyToSpannable(Spannable str, Selection selection, Boolean add) {
    T[] spans=str.getSpans(selection.getStart(), selection.getEnd(), clazz);
    int prologueStart=Integer.MAX_VALUE;
    int epilogueEnd=-1;

    for (T span : spans) {
      int spanStart=str.getSpanStart(span);

      if (spanStart < selection.getStart()) {
        prologueStart=Math.min(prologueStart, spanStart);
      }

      int spanEnd=str.getSpanEnd(span);

      if (spanEnd > selection.getEnd()) {
        epilogueEnd=Math.max(epilogueEnd, spanEnd);
      }

      str.removeSpan(span);
    }

    try {
      if (add) {
        str.setSpan(clazz.newInstance(), selection.getStart(),
                    selection.getEnd(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
      }
      else {
        if (prologueStart < Integer.MAX_VALUE) {
          str.setSpan(clazz.newInstance(), prologueStart,
                      selection.getStart(),
                      Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }

        if (epilogueEnd > -1) {
          str.setSpan(clazz.newInstance(), selection.getEnd(),
                      epilogueEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
      }
    }
    catch (IllegalAccessException e) {
      Log.e("RichEditText",
            "Exception instantiating " + clazz.toString(), e);
    }
    catch (InstantiationException e) {
      Log.e("RichEditText",
            "Exception instantiating " + clazz.toString(), e);
    }
  }
}
