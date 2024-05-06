package juloo.keyboard2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;

public class LauncherActivity extends Activity
{
  /** Text is replaced when receiving key events. */
  VideoView _intro_video;
  TextView _tryhere_text;
  EditText _tryhere_area;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    
    super.onCreate(savedInstanceState);
    setContentView(R.layout.launcher_activity);
    _intro_video = (VideoView)findViewById(R.id.launcher_intro_video);
    _tryhere_text = (TextView)findViewById(R.id.launcher_tryhere_text);
    _tryhere_area = (EditText)findViewById(R.id.launcher_tryhere_area);
    if (VERSION.SDK_INT >= 28)
      _tryhere_area.addOnUnhandledKeyEventListener(
          this.new Tryhere_OnUnhandledKeyEventListener());
    setup_intro_video(_intro_video);
  }
  @Override
  public final boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.launcher_menu, menu);
    return true;
  }

  @Override
  public final boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.btnLaunchSettingsActivity) {
      Intent intent = new Intent(LauncherActivity.this, SettingsActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    }
    return super.onOptionsItemSelected(item);
  }
  public void launch_imesettings(View _btn)
  {
    startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
  }

  public void launch_imepicker(View v)
  {
    InputMethodManager imm =
      (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    imm.showInputMethodPicker();
  }

  static void setup_intro_video(final VideoView v)
  {
    if (VERSION.SDK_INT >= 26)
      v.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);
    v.setVideoURI(Uri.parse("android.resource://" +
          v.getContext().getPackageName() + "/" + R.raw.intro_video));
    v.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
          @Override
          public void onPrepared(MediaPlayer mp)
          {
            mp.setLooping(true);
          }
        });
    v.setOnErrorListener(new MediaPlayer.OnErrorListener()
        {
          @Override
          public boolean onError(MediaPlayer mp, int what, int extra)
          {
            v.stopPlayback();
            v.setVisibility(View.GONE);
            return true;
          }
        });
    v.start();
  }

  @TargetApi(28)
  final class Tryhere_OnUnhandledKeyEventListener implements View.OnUnhandledKeyEventListener
  {
    public boolean onUnhandledKeyEvent(View v, KeyEvent ev)
    {
      // Don't handle the back key
      if (ev.getKeyCode() == KeyEvent.KEYCODE_BACK)
        return false;
      // Key release of modifiers would erase interesting data
      if (KeyEvent.isModifierKey(ev.getKeyCode()))
        return false;
      StringBuilder s = new StringBuilder();
      if (ev.isAltPressed()) s.append("Alt+");
      if (ev.isShiftPressed()) s.append("Shift+");
      if (ev.isCtrlPressed()) s.append("Ctrl+");
      if (ev.isMetaPressed()) s.append("Meta+");
      // s.append(ev.getDisplayLabel());
      String kc = KeyEvent.keyCodeToString(ev.getKeyCode());
      s.append(kc.replaceFirst("^KEYCODE_", ""));
      _tryhere_text.setText(s.toString());
      return true;
    }
  }
}
