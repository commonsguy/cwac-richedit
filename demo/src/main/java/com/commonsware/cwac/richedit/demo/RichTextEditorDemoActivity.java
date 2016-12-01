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
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.Selection;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import com.commonsware.cwac.colormixer.ColorMixerActivity;
import com.commonsware.cwac.richedit.*;
import com.commonsware.cwac.richtextutils.SpannedXhtmlGenerator;

import java.util.List;

public class RichTextEditorDemoActivity extends Activity
  implements ColorPicker {
  private static final int COLOR_REQUEST=1337;
  private RichEditText editor=null;
  private ToggleButton boldToggleButton;
  private ToggleButton italicToggleButton;
  private ToggleButton fontSizeToggleButton;
  private ToggleButton alignmentToggleButton;
  private ToggleButton bulletToggleButton;
  private Button htmlPreviewButton;
  private ColorPickerOperation colorPickerOp=null;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.main);
    
    editor=(RichEditText)findViewById(R.id.editor);
    boldToggleButton=(ToggleButton)findViewById(R.id.bold);
    italicToggleButton=(ToggleButton)findViewById(R.id.italic);
    fontSizeToggleButton=(ToggleButton)findViewById(R.id.fontSize);
    alignmentToggleButton=(ToggleButton)findViewById(R.id.alignment);
    bulletToggleButton=(ToggleButton)findViewById(R.id.bullet);
    htmlPreviewButton=(Button)findViewById(R.id.htmlPreview);

    editor.setColorPicker(this);
    editor.enableActionModes(true);
    editor.setOnSelectionChangedListener(new RichEditText.OnSelectionChangedListener() {
      @Override
      public void onSelectionChanged(int start, int end, List<Effect<?>> effects) {
        Log.d("DemoActivity", "onSelectionChanged, start: " + start + ", end: " + end + ", effects: " + effects);
        boolean boldEnabled = false;
        boolean italicEnabled = false;
        boolean fontSizeChanged = false;
        boolean alignmentChanged = false;
        boolean bulletEnabled = false;

        for (Effect effect : effects) {
          if (effect == RichEditText.BOLD) {
            boldEnabled = true;
          } else if (effect == RichEditText.ITALIC) {
            italicEnabled = true;
          } else if (effect == RichEditText.LINE_ALIGNMENT) {
            alignmentChanged = effect.valueInSelection(editor).equals(Layout.Alignment.ALIGN_OPPOSITE);
          } else if (effect == RichEditText.BULLET) {
            bulletEnabled = true;
          } else if (effect == RichEditText.RELATIVE_SIZE) {
            fontSizeChanged = effect.valueInSelection(editor).equals(1.5f);
          }
        }

        boldToggleButton.setChecked(boldEnabled);
        italicToggleButton.setChecked(italicEnabled);
        fontSizeToggleButton.setChecked(fontSizeChanged);
        alignmentToggleButton.setChecked(alignmentChanged);
        bulletToggleButton.setChecked(bulletEnabled);
      }
    });

    boldToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        editor.applyEffect(RichEditText.BOLD, isChecked);
      }
    });

    italicToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        editor.applyEffect(RichEditText.ITALIC, isChecked);
      }
    });

    fontSizeToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        editor.applyEffect(RichEditText.RELATIVE_SIZE, isChecked ? 1.5f : 1f);
      }
    });

    alignmentToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        editor.applyEffect(RichEditText.LINE_ALIGNMENT,
                isChecked ? Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_NORMAL);
      }
    });

    bulletToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        editor.applyEffect(RichEditText.BULLET, isChecked);
      }
    });

    htmlPreviewButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new AlertDialog.Builder(RichTextEditorDemoActivity.this)
                .setMessage(new SpannedXhtmlGenerator().toXhtml(editor.getText()))
                .setPositiveButton(android.R.string.ok, null)
                .show();
      }
    });
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
        editor.applyEffect(new URLEffect(), "https://commonsware.com");

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
}
