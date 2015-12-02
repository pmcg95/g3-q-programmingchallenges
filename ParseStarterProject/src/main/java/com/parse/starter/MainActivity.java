/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.*;
import java.util.List;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

  private String android_id;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    android_id = Settings.Secure.getString(this.getContentResolver(),
            Settings.Secure.ANDROID_ID);

    ParseAnalytics.trackAppOpenedInBackground(getIntent());

    final Button button = (Button) findViewById(R.id.AddButton);
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        QQueue.addSelfToQueue("Q", android_id, new SaveCallback() {
          @Override
          public void done(ParseException e) {
            getAndUpdateQueueValue();
          }
        });
      }
    });

    final Button removeButton = (Button) findViewById(R.id.RemoveButton);
    removeButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        QQueue.removeSelfFromQueue("Q", android_id, new DeleteCallback() {
          @Override
          public void done(ParseException e) {
            getAndUpdateQueueValue();
          }
        });
      }
    });

    final Button getPositionButton = (Button) findViewById(R.id.GetPosition);
    getPositionButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        getAndUpdateQueueValue();
      }
    });

    getAndUpdateQueueValue();
  }

  public void getAndUpdateQueueValue() {
    QQueue.getPlaceInQueue("Q", android_id, new GetCallback<ParseObject>() {
      public void done(ParseObject object, ParseException e) {
        if (object == null) {
          Log.d("getPlaceInQueue", "Doesn't exist");
          final TextView textView = (TextView) findViewById(R.id.qText);
          textView.setText("Not in queue");
        } else {
          Log.d("getPlaceInQueue", object.getNumber("place").toString());
          final TextView textView = (TextView) findViewById(R.id.qText);
          textView.setText(object.getInt("place")+"");
        }
      }
    });
    QQueue.getInfoAboutMe(android_id, new GetCallback<ParseObject>() {
      public void done(ParseObject object, ParseException e) {
        if (object == null) {
          Log.d("getPlaceInQueue", "Doesn't exist");
        } else {
          final TextView textView = (TextView) findViewById(R.id.infoText);
          textView.setText(object.getString("name") + " - " + object.getString("major") + " - " + object.getString("position"));
        }
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
