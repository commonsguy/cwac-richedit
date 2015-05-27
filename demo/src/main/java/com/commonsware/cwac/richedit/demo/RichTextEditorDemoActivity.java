/***
  Copyright (c) 2012 CommonsWare, LLC
  
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

package com.commonsware.cwac.richedit.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Selection;
import android.text.style.URLSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.commonsware.cwac.colormixer.ColorMixerActivity;
import com.commonsware.cwac.richedit.ColorPicker;
import com.commonsware.cwac.richedit.ColorPickerOperation;
import com.commonsware.cwac.richedit.RichEditText;
import com.commonsware.cwac.richtextutils.SpannableStringGenerator;
import com.commonsware.cwac.richtextutils.SpannedXhtmlGenerator;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

public class RichTextEditorDemoActivity extends ActionBarActivity
  implements ColorPicker {
  private static final int COLOR_REQUEST=1337;
  private RichEditText editor=null;
  private ColorPickerOperation colorPickerOp=null;

  TextView viewer;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

    editor=(RichEditText)findViewById(R.id.editor);
    viewer= (TextView) findViewById(R.id.viewer);
    editor.setColorPicker(this);
    editor.enableActionModes(true);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.add_link:
        int offset=editor.getText().length();
        editor.append(" CommonsWare rocks!");
        editor.setSelection(offset+1, editor.getText().length());
        editor.applyEffect(RichEditText.URL, "https://commonsware.com");

        Selection.removeSelection(editor.getText());

        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public boolean pick(ColorPickerOperation op) {
    Intent i=new Intent(this, ColorMixerActivity.class);

    i.putExtra(ColorMixerActivity.TITLE, "Pick a Color");

    if (op.hasColor()) {
      i.putExtra(ColorMixerActivity.COLOR, op.getColor());
    }

    this.colorPickerOp=op;
    startActivityForResult(i, COLOR_REQUEST);

    return(true);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode,
                               Intent result) {
    int color=0;

    if (colorPickerOp!=null && requestCode==COLOR_REQUEST) {
      if (resultCode==Activity.RESULT_OK) {
        color=result.getIntExtra(ColorMixerActivity.COLOR, 0);
      }

      if (color==0) {
        colorPickerOp.onPickerDismissed();
      }
      else {
        colorPickerOp.onColorPicked(color);
      }

      colorPickerOp=null;
    }
  }

  public void RichTextAction(View v) {
    switch (v.getId()) {
      case R.id.viewxml:
        if (editor.getVisibility() == View.VISIBLE) {
          editor.setVisibility(View.GONE);
          viewer.setVisibility(View.VISIBLE);
          viewer.setText(new SpannedXhtmlGenerator().toXhtml(editor.getText()));
          ((Button)v).setText("edit");
        } else {
          editor.setVisibility(View.VISIBLE);
          viewer.setVisibility(View.GONE);
          try {
            editor.setText(new SpannableStringGenerator().fromXhtml(viewer.getText().toString()));
          } catch (ParserConfigurationException | SAXException | IOException e) {
            editor.setVisibility(View.GONE);
            viewer.setVisibility(View.VISIBLE);
            viewer.setText(e.getMessage());
          }
          ((Button)v).setText("xml");
        }
        break;
      case R.id.bullet:
        editor.toggleEffect(RichEditText.BULLET);
        break;
      case R.id.link:
        final int selStart;
        final int selEnd;
        URLSpan[] links = editor.getText().getSpans(editor.getSelectionStart(), editor.getSelectionEnd(), URLSpan.class);
        final EditText editText = new EditText(this);
        if (links.length > 0) {
          selStart = Math.min(editor.getText().getSpanStart(links[0]), editor.getSelectionStart());
          selEnd = Math.max(editor.getText().getSpanEnd(links[links.length - 1]), editor.getSelectionEnd());
          editText.setText(links[0].getURL());
        } else {
          selStart = editor.getSelectionStart();
          selEnd = editor.getSelectionEnd();
        }
        new AlertDialog.Builder(this)
                .setTitle("Link URL")
                .setView(editText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    String url = editText.getText().toString();
                    if (selStart == selEnd) {
                      editor.getText().insert(selStart, url);
                      editor.setSelection(selStart, selEnd + url.length());
                    } else {
                      editor.setSelection(selStart, selEnd);
                    }
                    editor.applyEffect(RichEditText.URL, url);
                    editor.setSelection(editor.getSelectionEnd());
                  }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                  }
                })
                .show();
        break;
    }
  }
}
